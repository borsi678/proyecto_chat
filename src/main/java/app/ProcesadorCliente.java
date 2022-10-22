package app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * <p> Clase que extiende de Procesador para impelemtenar el comportamiento del Cliente</p>
 * 
 * <p> La clase se encarga de enviar mensajes al servidor que escribe el usuario
 *      asi como de recibir los mensajes que envia el servidor.</p>
 * 
 */
public class ProcesadorCliente extends Procesador{
    
    /**El cliente del usuario */
    private Cliente cliente;
    
    /**Constructor, no recibe ningun parametro e inicializa los @param de la clase en null */
    public ProcesadorCliente(){
        this.cliente=null;
        this.entrada=null;
        this.salida=null;
    }
    
    /**Constructor que recibe un cliente y un socket
     * @param cliente el cliente del usuario
     * @param socket el socket de la conexion del cliente
    */
    public ProcesadorCliente(Cliente cliente, Socket socket) throws IOException{
        this.cliente=cliente;
        this.entrada=new DataInputStream(socket.getInputStream());
        this.salida=new DataOutputStream(socket.getOutputStream());
    }
    
    /**Metodo que se encarga de incializar la conexion con el servidor. El servidor envia un mensaje 
     * pidiendo la identificacion del usuario, el usuario envia un mensaje son su nombre.
     * El servidor responde si se realizo la operacion o si hubo un error, si ocurrio un error el servidor 
     * nos desconectara.
     * 
     */
    public void iniciaConexion(){
        try {
            System.out.println("Identificate");
            Scanner scanner = new Scanner(System.in);
            String nombre="";
            while(nombre.equals("")){
                    nombre=scanner.nextLine();
            }
            Mensajes mensajeCliente=MensajesServidorCliente.conTipoUsuario(String.format("IDENTIFY %s", nombre));
            salida.writeUTF(serializaMensaje(mensajeCliente));
            salida.flush();
            String mensajeServidorSerializado=entrada.readUTF();
            Mensajes mensajeServidor=deserializaMensaje(mensajeServidorSerializado);
            if(mensajeServidor.getTipo().equals("INFO") 
                    && mensajeServidor.getOperacion().equals("IDENTIFY")){
                cliente.imprimeMensaje("INFO Se ha identificado satisfactoriamente");
                Cliente.setNombreUsuario(nombre);
                Cliente.cambiaEstadoConexion(true);
            }
            else if(mensajeServidor.getTipo().equals("WARNING") 
                    && mensajeServidor.getOperacion().equals("IDENTIFY") 
                    && mensajeServidor.getNombreUsuario().equals(nombre)){
                this.cliente.imprimeMensaje(mensajeServidor.getTipo()+" "+mensajeServidor.getMensaje());
                this.cliente.imprimeMensaje("Intentalo de nuevo");
                cliente.iniciaCliente();
            }
        } catch(NullPointerException ex){
            throw new ExcepcionMensajeInvalido("Mensaje Invalido");
        } catch (IOException ex) {
            Cliente.cambiaEstadoConexion(false);
        }
    }

    /**Metodo que se encarga de procesar los mensajes recibidos por el servidor. Este metodo es 
     * usuado por el hilo de ejecucion que es lanzado por el cliente.
     */
    @Override public void run(){
        String mensajeServidor="";
        Mensajes mensaje;
        while(Cliente.getConexion()){
            try {
                mensajeServidor=entrada.readUTF();
                if(!Cliente.getConexion())
                    return;
                mensaje=deserializaMensaje(mensajeServidor);
                mensajesRecibidos(mensaje);
            }catch(EOFException ex){
                System.out.println("\nSe perdio la conexion con el sevidor.\nReiniciando."); 
                Cliente.cambiaEstadoConexion(false);
                return;
            } catch (IOException ex) {
                Logger.getLogger(ProcesadorCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**Metodo que se encarga de procesar los mensajes que envia el usuario al char para el servidor.
     * Las posibles operaciones son: USERS, STATUS, MESSAGE, PUBLIC_MESSAGE, NEW_ROOM, JOIN_ROOM, 
     * ROOM_USERS, LEAVE_ROOM, INVITE, ROOM_MESSAGE, DISCONNECT.
     * @param mensaje el mensaje a procesar.
     */
    @Override
    public void menuMensajes(String mensaje){

        String[] argumentoMensaje=mensaje.split(" ");
            try{
                switch(argumentoMensaje[0].toUpperCase()){
                    case "USERS":
                        recibeListaUsuariosServidor(argumentoMensaje[0]);
                        break;
                    case "STATUS":    
                        cambiaEstado(mensaje);
                        break;
                    case "MESSAGE":
                        enviaMensajePrivado(mensaje);
                        break;
                    case "PUBLIC_MESSAGE":
                        enviaMensajePublico(mensaje);
                        break;
                    case "NEW_ROOM":
                        creaNuevoCuarto(mensaje);
                        break;
                    case "JOIN_ROOM":  
                        unirseCuarto(mensaje);
                        break;
                    case "ROOM_USERS":
                        recibeUsuariosCuarto(mensaje);
                        break;
                    case "LEAVE_ROOM":
                        abandonaCuarto(mensaje);
                        break;
                    case "INVITE":
                        invitaUsuariosCuarto(mensaje);
                        break;
                    case "ROOM_MESSAGE": 
                        enviaMensajeCuarto(mensaje);
                        break;
                    case "DISCONNECT":    
                        Mensajes mensajeCliente=MensajesServidorCliente.conTipo("DISCONNECT");
                        salida.writeUTF(serializaMensaje(mensajeCliente));
                        break;
                    default:
                        System.out.println("No ingreso una opcion valida, intentalo de nuevo.");
            }

        } catch(ArrayIndexOutOfBoundsException ex){
            System.out.println("Error en la operacion "+argumentoMensaje[0]);
            System.out.println("Faltan argumentos, intentalo de nuevo");
            System.out.println("Escribe operacion:");
        } catch(ExcepcionMensajeInvalido ex){
            System.out.println("Error en la operacion "+argumentoMensaje[0]);
            System.out.println(ex.toString());
            System.out.println("Ingrese un mensaje valido");
            System.out.println("Escribe operacion:");
        }catch(ExcepcionEstadoInvalido ex){
            System.out.println("Error en la operacion "+argumentoMensaje[0]);
            System.out.println("Falta el estado o es un estado invalido.");
            System.out.println("Ingrese un estado valido");
            System.out.println("Escribe operacion:");
        }catch(ExcepcionDeserializa ex){
            System.out.println("Error en la operacion "+argumentoMensaje[0]);
            System.out.println(ex.toString());
            System.out.println("Escribe operacion:");
        }catch(ExcepcionSerializa ex){
            System.out.println("Error en la operacion "+argumentoMensaje[0]);
            System.out.println(ex.toString());
        }catch(IOException ex){
                System.out.println("Error al procesar el mensaje");
                System.out.println(ex.toString());
                System.out.println("Intentelo de nuevo");
                System.out.println("Escribe operacion:");
        }
            
    }
    /**Metodo que se encarga de procesar los mensajes recibidos por el servidor, las posibles 
     * operaciones son: NEW_USER, NEW_STATUS, USER_LIST, ROOM_USER_LIST, MESSAGE_FROM,
     * PUBLIC_MESSAGE_FROM, INVITATION, JOINED_ROOM, ROOM_MESSAGE_FROM, LEFT_ROOM,
     * DISCONNECTED.
     * @param mensaje el mensaje a procesar.
     */
    public void mensajesRecibidos(Mensajes mensaje){
        String mensajeImprimir;
        switch(mensaje.getTipo().toUpperCase()){
            case "NEW_USER":
                cliente.imprimeMensaje("\nINFO Se ha conectado al servidor el usuario: "+mensaje.getNombreUsuario());
                break;
            case "NEW_STATUS": 
                cliente.imprimeMensaje("\nINFO El usuario '"+mensaje.getNombreUsuario()+
                    "' ha cambiado el estado a "+mensaje.getEstado());
                break;
            case "USER_LIST":   
                cliente.imprimeMensaje("\n[General] Lista de usuarios conectados: "
                    +mensaje.usuariosToString());
                break;
            case "ROOM_USER_LIST":    
                mensajeImprimir=String.format("\nLista de usuarios conectados a la sala: %s", mensaje.usuariosToString());
                cliente.imprimeMensaje(mensajeImprimir);
                break;
            case "MESSAGE_FROM":
                mensajeImprimir=String.format("\n[Mensaje Privado][%s] %s", mensaje.getNombreUsuario()
                , mensaje.getMensaje());
                cliente.imprimeMensaje(mensajeImprimir);
                break;
            case "PUBLIC_MESSAGE_FROM":    
                mensajeImprimir=String.format("\n[General][%s] %s", mensaje.getNombreUsuario()
                , mensaje.getMensaje());
                cliente.imprimeMensaje(mensajeImprimir);
                break;
            case "INVITATION":    
                mensajeImprimir=String.format("\n[Invitacion] %s", mensaje.getMensaje());
                cliente.imprimeMensaje(mensajeImprimir);
                break;
            case "JOINED_ROOM":
                mensajeImprimir=String.format("\n[%s] El usuario %s se ha unido a la sala.", 
                        mensaje.getNombreCuarto(), mensaje.getNombreUsuario());
                cliente.imprimeMensaje(mensajeImprimir);
                break;
            case "ROOM_MESSAGE_FROM":
                mensajeImprimir=String.format("\n[%s][%s] %s", mensaje.getNombreCuarto(),
                        mensaje.getNombreUsuario(), mensaje.getMensaje());
                cliente.imprimeMensaje(mensajeImprimir);
                break;
            case "LEFT_ROOM":   
                mensajeImprimir=String.format("\n[%s] El usuario '%s' a salido del cuarto", 
                        mensaje.getNombreCuarto(), mensaje.getNombreUsuario());
                cliente.imprimeMensaje(mensajeImprimir);
                break;
            case "DISCONNECTED":         
                mensajeImprimir=String.format("\n[General] El usuario ' %s' a salido del cuarto", 
                        mensaje.getNombreUsuario());
                cliente.imprimeMensaje(mensajeImprimir);
                break;      
        }
        if(mensaje.getOperacion() != null){
            switch(mensaje.getOperacion().toUpperCase()){
                case "STATUS":
                    procesaMensajeEstado(mensaje);
                    break;
                case "MESSAGE":    
                    procesaMensajePrivado(mensaje);
                    break;
                case "NEW_ROOM": 
                    procesaMensajeNuevoCuarto(mensaje);
                    break;
                case "JOIN_ROOM":   
                    procesaMensajeUnirseCuarto(mensaje);
                    break;
                case "ROOM_USERS":
                    procesaMensajeUsuariosCuarto(mensaje);
                    break;
                case "LEAVE_ROOM":
                    procesaMensajeAbandonaCuarto(mensaje);
                    break;
                case "INVITE":
                    procesaMensajeInvitaUsuariosCuarto(mensaje);
                    break;
                case "ROOM_MESSAGE":
                    procesaMensajeCuarto(mensaje);
                   break;
            }
        }
        System.out.print("Escribe operacion:");
    }
    /**Metodo que se encarga de enviar el mensaje al servidor para pedir la lista de usuarios conectados.
     * El servidor responde con la lista de usuarios conectados.
     * @param tipo el tipo de operacion del mensaje.
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del cliente.
     * @throws ExcepcionDeserializa si hubo un error al deserializar el mensaje del servidor.
     * @throws IOException si hubo un error con la conexion.
     */
    public void recibeListaUsuariosServidor(String tipo) throws ExcepcionSerializa, ExcepcionDeserializa, IOException{
        Mensajes mensajeCliente= MensajesServidorCliente.conTipo(tipo);
        salida.writeUTF(serializaMensaje(mensajeCliente));
        salida.flush();
    }
   
    /**Metodo que encarga de enviar el mensaje al servidor con el nuevo estado del usuario.
     * El servidor responde si se realizo la operacion o hubo un error.
     * @param mensaje el mensaje a serializar.     
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del cliente.
     * @throws ExcepcionDeserializa si hubo un error al deserializar el mensaje del servidor.
     * @throws IOException si hubo un error con la conexion.
     */
    public void cambiaEstado(String mensaje) throws ExcepcionSerializa, ExcepcionDeserializa, IOException{
        Mensajes mensajeCliente=MensajesServidorCliente.conTipoEstado(mensaje);
        salida.writeUTF(serializaMensaje(mensajeCliente));
        salida.flush();
        EstadoUsuario estadoUsuario= MensajesServidorCliente.
                    convertirCadenaAEstadoUsuario(mensajeCliente.getEstado());
        cliente.cambiaEstadoUsuario(estadoUsuario);
    }
    
    /**
     * Metodo que se encarga de procesar el mensaje del servidor al cambiar el estado del usuario
     * @param mensajeServidor  el mensaje a procesar del servidor
     */
    public void procesaMensajeEstado(Mensajes mensajeServidor){
        if(mensajeServidor.getTipo().equals("INFO") && mensajeServidor.getOperacion().equals("STATUS")){
            cliente.imprimeMensaje("INFO Estado cambiado satisfactoriamente.");
            return;
        } else if(mensajeServidor.getTipo().equals("WARNING") && mensajeServidor.getOperacion().equals("STATUS")){
            cliente.imprimeMensaje(mensajeServidor.getMensaje());
            return;
        }
    }
    
    /**Metodo que se encarga de enviar un mensaje privado a un usuario al servidor, el mensaje 
     * contiene el nombre del usuario receptor y el mensaje del usuario.
     * El servidor responde si se realizo la operacion o hubo un error.
     * @param mensaje el mensaje a serializar.
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del cliente.
     * @throws ExcepcionDeserializa si hubo un error al deserializar el mensaje del servidor.
     * @throws IOException si hubo un error con la conexion.
    */
    public void enviaMensajePrivado(String mensaje) throws ExcepcionSerializa, ExcepcionDeserializa, IOException{
        Mensajes mensajeCliente=MensajesServidorCliente.conTipoUsuarioMensaje(mensaje);
        if(mensajeCliente.getMensaje().equals("") || mensajeCliente.getNombreUsuario().equals(Cliente.getNombreUsuario()))
            throw new ExcepcionMensajeInvalido();
        salida.writeUTF(serializaMensaje(mensajeCliente));
        salida.flush();
    }
    
    /**
     * Metodo que se encarga de procesar el mensaje privado que envia el servidor.
     * @param mensajeServidor el mensaje a procesar del servidor
     */
    public void procesaMensajePrivado(Mensajes mensajeServidor){
        if(mensajeServidor.getTipo().equals("WARNING")){
                cliente.imprimeMensaje(mensajeServidor.getTipo()+" "+mensajeServidor.getMensaje());
        }
    }
    
    /**Metodo que se encarga de enviar un mensaje publico por parte del usuario al servidor. 
     * EL servidor no responde nada y envia el mensaje a todos los clientes conectados.
     * @param mensaje el mensaje a serializar.
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del cliente.
     * @throws ExcepcionDeserializa si hubo un error al deserializar el mensaje del servidor.
     * @throws IOException si hubo un error con la conexion.
     */
    public void enviaMensajePublico(String mensaje) throws ExcepcionSerializa, ExcepcionDeserializa, IOException{
        Mensajes mensajeCliente=MensajesServidorCliente.conTipoMensaje(mensaje);
        if(mensajeCliente.getMensaje().equals(""))
            throw new ExcepcionMensajeInvalido();
        salida.writeUTF(serializaMensaje(mensajeCliente));
        salida.flush();
    }
    
    /**Metodo que se encarga de enviar un mensaje al servidor para crear un cuarto, el mensaje 
     * contiene el nombre del cuarto.
     * El servidor responde si se completo la operacion o hubo un error.
     * @param mensaje el mensaje a serializar.
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del cliente.
     * @throws ExcepcionDeserializa si hubo un error al deserializar el mensaje del servidor.
     * @throws IOException si hubo un error con la conexion.
     */
    public void creaNuevoCuarto(String mensaje) throws ExcepcionSerializa, ExcepcionDeserializa, IOException{
        Mensajes mensajeCliente= MensajesServidorCliente.conTipoNombreCuarto(mensaje);
        salida.writeUTF(serializaMensaje(mensajeCliente));
        salida.flush();
    }
    
    /**
     * Metodo que se encarga de procesar el mensaje del servidor al crear un cuarto.
     * @param mensajeServidor el mensaje a procesar del servidor
     */
    public void procesaMensajeNuevoCuarto(Mensajes mensajeServidor){
        if(mensajeServidor.getTipo().equals("INFO") 
                && mensajeServidor.getMensaje().equals("success")){
            cliente.imprimeMensaje("Cuarto creado satisfactoriamente");
        } else if(mensajeServidor.getTipo().equals("WARNING"))
            cliente.imprimeMensaje(mensajeServidor.getTipo()+" "+mensajeServidor.getMensaje());
    }
    
    /** Metodo que se encarga de enviar el mensaje al servidor para unirse a una sala determinada,
     * la sala debe existir y el usuario ser previamente invitado.
     * El servidor responde si se realizo la operacion o hubo un error.
     * @param mensaje el mensaje a serializar.
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del cliente.
     * @throws ExcepcionDeserializa si hubo un error al deserializar el mensaje del servidor.
     * @throws IOException si hubo un error con la conexion.
     */
    public void unirseCuarto(String mensaje) throws ExcepcionSerializa, ExcepcionDeserializa, IOException{
        Mensajes mensajeCliente= MensajesServidorCliente.conTipoNombreCuarto(mensaje);
        salida.writeUTF(serializaMensaje(mensajeCliente));
        salida.flush();
    }
    
    /**
     * Metodo que se encarga de procesar el mensaje del servidor al uniser a un cuarto.
     * @param mensajeServidor el mensaje a procesar del servidor 
     */
    public void procesaMensajeUnirseCuarto(Mensajes mensajeServidor){
        if(mensajeServidor.getTipo().equals("INFO") 
                && mensajeServidor.getMensaje().equals("success")){
            cliente.imprimeMensaje("INFO Se ha unido a la sala satisfactoraimente");
        } else if(mensajeServidor.getTipo().equals("WARNING")){
            cliente.imprimeMensaje(mensajeServidor.getTipo()+" "+mensajeServidor.getMensaje());           
        }
    }
    /** Metodo que se encarga de pedir la lista de usuarios de una sala determinda
     * Si la sala existe y el usuario esta unido a ella el servidor responde con la lista de usuarios, si no el 
     * servidor responde con un error.
     * @param mensaje el mensaje a serializar.
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del cliente.
     * @throws ExcepcionDeserializa si hubo un error al deserializar el mensaje del servidor.
     * @throws IOException si hubo un error con la conexion.
     * 
     */
    public void recibeUsuariosCuarto(String mensaje) throws ExcepcionSerializa, ExcepcionDeserializa, IOException{
        Mensajes mensajeCliente= MensajesServidorCliente.conTipoNombreCuarto(mensaje);
        salida.writeUTF(serializaMensaje(mensajeCliente));
        salida.flush();
    }
    
    /**
     * Metodo que se encarga de procesar el mensaje del servidor al obtener la lista de usuarios de un cuarto.
     * @param mensajeServidor el mensaje a procesar del servidor
     */
    public void procesaMensajeUsuariosCuarto(Mensajes mensajeServidor){
        if(mensajeServidor.getTipo().equals("WARNING"))
                cliente.imprimeMensaje(mensajeServidor.getTipo()+" "+mensajeServidor.getMensaje());
    }
    
    /**
     * Metodo que se encarga de enviar el mensaje para abandonar el cuarto
     * El servidor responde si se realizo la operacion o sucedio un error.
     * @param mensaje el mensaje a serializar.
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del cliente.
     * @throws ExcepcionDeserializa si hubo un error al deserializar el mensaje del servidor.
     * @throws IOException si hubo un error con la conexion. 
     */
    public void abandonaCuarto(String mensaje) throws ExcepcionSerializa, ExcepcionDeserializa, IOException{
        Mensajes mensajeCliente= MensajesServidorCliente.conTipoNombreCuarto(mensaje);
        salida.writeUTF(serializaMensaje(mensajeCliente));
        salida.flush();
    }
    
    /**
     * Metodo que se encarga de procesar el mensaje del servidor al abandonar un cuarto.
     * @param mensajeServidor el mensaje a procesar del servidor
     */
    public void procesaMensajeAbandonaCuarto(Mensajes mensajeServidor){
        if(mensajeServidor.getTipo().equals("INFO") 
                && mensajeServidor.getMensaje().equals("success"))
            cliente.imprimeMensaje("INFO Se ha abandoando el cuarto satisfactoriamente");
        else if(mensajeServidor.getTipo().equals("WARNING"))
            cliente.imprimeMensaje(mensajeServidor.getTipo()+" "+mensajeServidor.getMensaje());
    }
    
    /**
     * Metodo que se encarga de enviar el mensaje al servidor con la invitacion, normbre de cuarto y
     * los nombres de los usuarios. El servidor responde si se realizo la operacion o sucedio un error.
     * @param mensaje el mensaje a serializar
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del cliente.
     * @throws ExcepcionDeserializa si hubo un error al deserializar el mensaje del servidor.
     * @throws IOException si hubo un error con la conexion.
     */
    public void invitaUsuariosCuarto(String mensaje) throws ExcepcionSerializa, ExcepcionDeserializa, IOException{
        Mensajes mensajeCliente= MensajesServidorCliente.conTipoNombreCuartoUsuarios(mensaje);
        if(mensajeCliente.usuariosToString().contains(Cliente.getNombreUsuario()))
            throw new ExcepcionMensajeInvalido("No se puede enviar la invitacion a si mismo.");
        salida.writeUTF(serializaMensaje(mensajeCliente));
        salida.flush();
    }
    
    /**
     * Metodo que se encarga de procesar el mensaje del servidor al invitar usuarios a un cuarto.
     * @param mensajeServidor el mensaje a procesar del servidor
     */
    public void procesaMensajeInvitaUsuariosCuarto(Mensajes mensajeServidor){
        if(mensajeServidor.getTipo().equals("INFO") 
                && mensajeServidor.getMensaje().equals("success"))
            cliente.imprimeMensaje("INFO Se ha invitado a todos los usuarios al cuarto satisfactoriamente");
        else if(mensajeServidor.getTipo().equals("WARNING")){
                cliente.imprimeMensaje(mensajeServidor.getTipo()+" "+mensajeServidor.getMensaje());
        }
    }
    
    /**
     * Metodo que se escarga de enviar el mensaje al servidor que contiene el nombre de cuarto y el
     * mensaje del usuario. El servidor solo responde si ocurrio un error.
     * @param mensaje el mensaje a serializar.
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del cliente.
     * @throws ExcepcionDeserializa si hubo un error al deserializar el mensaje del servidor.
     * @throws IOException si hubo un error con la conexion.
     */
    public void enviaMensajeCuarto(String mensaje) throws ExcepcionSerializa, ExcepcionDeserializa, IOException{
        Mensajes mensajeCliente= MensajesServidorCliente.conTipoNombreCuartoMensaje(mensaje);
        if(mensajeCliente.getMensaje().equals(""))
            throw new ExcepcionMensajeInvalido();
        salida.writeUTF(serializaMensaje(mensajeCliente));
        salida.flush();
    }
    
    /**
     * Metodo que se encarga de procesar el mensaje del servidor al enviar un mensaje a un cuarto.
     * @param mensajeServidor el mensaje a procesar del servidor
     */
    public void procesaMensajeCuarto(Mensajes mensajeServidor){
        if(mensajeServidor.getTipo().equals("WARNING"))
            cliente.imprimeMensaje(mensajeServidor.getTipo()+" "+mensajeServidor.getMensaje());
    }
}
