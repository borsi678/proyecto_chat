package app;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import app.ExcepcionMensajeInvalido;
/**
 * <p> Clase que extiende de Procesador para impelemtar el comportamiento del Cliente</p>
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
            String mensajeServidorSerializado=entrada.readUTF();
            Mensajes mensajeServidor=deserializaMensaje(mensajeServidorSerializado);
            cliente.imprimeMensaje(mensajeServidor.getTipo()+" "+mensajeServidor.getMensaje());
            Scanner scanner = new Scanner(System.in);
            String nombre=scanner.nextLine();
            Mensajes mensajeCliente=MensajesServidorCliente.conTipoUsuario(String.format("IDENTIFY %s", nombre));
            salida.writeUTF(serializaMensaje(mensajeCliente));
            mensajeServidorSerializado=entrada.readUTF();
            mensajeServidor=deserializaMensaje(mensajeServidorSerializado);
            if(mensajeServidor.getTipo().equals("INFO") 
                    && mensajeServidor.getOperacion().equals("IDENTIFY")){
                cliente.imprimeMensaje("INFO Se ha identificado satisfactoriamente");
                return;
            }
            else if(mensajeServidor.getTipo().equals("WARNING") 
                    && mensajeServidor.getOperacion().equals("IDENTIFY") 
                    && mensajeServidor.getNombreUsuario().equals(nombre)){
                this.cliente.imprimeMensaje(mensajeServidor.getTipo()+" "+mensajeServidor.getMensaje());
                this.cliente.imprimeMensaje("Intentalo de nuevo");
                cliente.iniciaCliente();
                return;
            }
        } catch(NullPointerException ex){
            throw new ExcepcionMensajeInvalido("Mensaje Invalido");
        } catch (IOException ex) {
            System.out.println("No se pudo realizar la conexion");
        }
    }

    /**Metodo que se encarga de procesar los mensajes recibidos por el servidor. Este metodo es 
     * usuado por el hilo de ejecucion que es lanzado por el cliente.
     */
    @Override public void run(){
        String mensajeServidor="";
        Mensajes mensaje;
        while(true){
            try {
                mensajeServidor=entrada.readUTF();
                mensaje=deserializaMensaje(mensajeServidor);
                mensajesRecibidos(mensaje);
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
                        return;
            }
        }catch(ExcepcionMensajeInvalido ex){
            System.out.println("Error en la operacion "+argumentoMensaje[0]);
            System.out.println(ex.toString());
            System.out.println("Ingrese un mensaje valido");
        }catch(ExcepcionEstadoInvalido ex){
            System.out.println("Error en la operacion "+argumentoMensaje[0]);
            System.out.println(ex.toString());
            System.out.println("Ingrese un estado valido");
        }catch(ExcepcionDeserializa ex){
            System.out.println("Error en la operacion "+argumentoMensaje[0]);
            System.out.println(ex.toString());
        }catch(ExcepcionSerializa ex){
            System.out.println("Error en la operacion "+argumentoMensaje[0]);
            System.out.println(ex.toString());
        }catch(IOException ex){
                System.out.println("Error al procesar el mensaje");
                System.out.println(ex.toString());
                System.out.println("Intentelo de nuevo");
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
                cliente.imprimeMensaje("INFO Se ha conectado al servidor el usuario: "+mensaje.getNombreUsuario());
                break;
            case "NEW_STATUS": 
                cliente.imprimeMensaje("INFO El usuario '"+mensaje.getNombreUsuario()+
                    "' ha cambiado el estado a "+mensaje.getEstado());
                break;
            case "USER_LIST":   
                cliente.imprimeMensaje("[General] Lista de usuarios conectados: "
                    +mensaje.usuariosToString());
                break;
            case "ROOM_USER_LIST":    
                mensajeImprimir=String.format("[%s] Lista de usuarios conectados a la sala: %s"
                        , mensaje.getNombreCuarto(), mensaje.getNombresUsuarios().toString());
                cliente.imprimeMensaje(mensajeImprimir);
                break;
            case "MESSAGE_FROM":
                mensajeImprimir=String.format("[Mensaje Privado][%s] %s", mensaje.getNombreUsuario()
                , mensaje.getMensaje());
                cliente.imprimeMensaje(mensajeImprimir);
                break;
            case "PUBLIC_MESSAGE_FROM":    
                mensajeImprimir=String.format("[General][%s] %s", mensaje.getNombreUsuario()
                , mensaje.getMensaje());
                cliente.imprimeMensaje(mensajeImprimir);
                break;
            case "INVITATION":    
                mensajeImprimir=String.format("[Invitacion] %s", mensaje.getMensaje());
                cliente.imprimeMensaje(mensajeImprimir);
                break;
            case "JOINED_ROOM":
                mensajeImprimir=String.format("[%s] El usuario '%s' se ha unido a la sala.", 
                        mensaje.getNombreCuarto(),mensaje.getNombreUsuario());
                cliente.imprimeMensaje(mensajeImprimir);
                break;
            case "ROOM_MESSAGE_FROM":
                mensajeImprimir=String.format("[%s][%s] %s", mensaje.getNombreCuarto(),
                        mensaje.getNombreUsuario(), mensaje.getMensaje());
                cliente.imprimeMensaje(mensajeImprimir);
                break;
            case "LEFT_ROOM":   
                mensajeImprimir=String.format("[%s] El usuario ' %s' a salido del cuarto", 
                        mensaje.getNombreCuarto(), mensaje.getMensaje());
                cliente.imprimeMensaje(mensajeImprimir);
                break;
            case "DISCONNECTED":         
                mensajeImprimir=String.format("[General] El usuario ' %s' a salido del cuarto", 
                        mensaje.getNombreUsuario());
                cliente.imprimeMensaje(mensajeImprimir);
                break;      
        }
        if(mensaje.getOperacion().equals("STATUS"))
            verificaMensajeEstado(mensaje);
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
    //    Mensajes mensajeServidor=deserializaMensaje(entrada.readUTF());
     //   if(mensajeServidor.getTipo().equals("USER_LIST") && mensajeServidor.getNombresUsuarios() != null){
         //   cliente.imprimeMensaje(mensajeServidor.getNombresUsuarios().toString());
       // }
    }
   
    /**Metodo que encarga de enviar el mensaje al servidor con el nuevo estado del usuario.
     * El servidor responde si se realizo la operacion o hubo un error.
     * @param mensaje el mensaje a serializar.     //   if(mensajeServidor.getTipo().equals("USER_LIST") && mensajeServidor.getNombresUsuarios() != null){

     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del cliente.
     * @throws ExcepcionDeserializa si hubo un error al deserializar el mensaje del servidor.
     * @throws IOException si hubo un error con la conexion.
     */
    public void cambiaEstado(String mensaje) throws ExcepcionSerializa, ExcepcionDeserializa, IOException{
        Mensajes mensajeCliente=MensajesServidorCliente.conTipoEstado(mensaje);
        Mensajes mensajeServidor;
        salida.writeUTF(serializaMensaje(mensajeCliente));
        salida.flush();

    }
    
    public void verificaMensajeEstado(Mensajes mensajeServidor){
        String estado=mensajeServidor.getEstado();
        if(mensajeServidor.getTipo().equals("INFO") && mensajeServidor.getOperacion().equals("STATUS")
                ){
            EstadoUsuario estadoUsuario= MensajesServidorCliente.convertirCadenaAEstadoUsuario(estado);
            cliente.cambiaEstadoUsuario(estadoUsuario);
            cliente.imprimeMensaje("INFO Estado cambiado satisfactoriamente.");
            return;
        } else if(mensajeServidor.getTipo().equals("WARNING") && mensajeServidor.getOperacion().equals("STATUS")
                && mensajeServidor.getMensaje().equals("El estado ya es \'"+estado+"\'")
                && mensajeServidor.getEstado().equals(estado)){
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
        salida.writeUTF(serializaMensaje(mensajeCliente));
        String mensajeServidorSerializado=entrada.readUTF();
        if(mensajeServidorSerializado != null || !(mensajeServidorSerializado.equals(""))){
            Mensajes mensajeServidor=deserializaMensaje(mensajeServidorSerializado);
            if(mensajeServidor.getTipo().equals("WARNING") && 
                    mensajeServidor.getOperacion().equals("MESSAGE") &&
                    mensajeServidor.getNombreUsuario().equals(mensajeCliente.getNombreUsuario()) &&
                    mensajeServidor.getMensaje().equals("El usuario "+mensajeCliente.getNombreUsuario()+" no existe") )
               cliente.imprimeMensaje(mensajeServidor.getTipo()+mensajeServidor.getMensaje());
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
        Mensajes mensajeServidor=MensajesServidorCliente.conTipoMensaje(mensaje);
        salida.writeUTF(serializaMensaje(mensajeServidor));
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
        Mensajes mensajeServidor=deserializaMensaje(entrada.readUTF());
        String nombreCuarto=mensajeCliente.getNombreCuarto();
        if(mensajeServidor.getTipo().equals("INFO") 
                && mensajeServidor.getOperacion().equals("NEW_ROOM")
                && mensajeServidor.getMensaje().equals("success")){
            cliente.imprimeMensaje("Cuarto creado satisfactoriamente");
        } else if(mensajeServidor.getTipo().equals("WARINIG")
                && mensajeServidor.getOperacion().equals("NEW_ROOM")
                && mensajeServidor.getNombreCuarto().equals(nombreCuarto)
                && mensajeServidor.getMensaje().equals("El cuarto '"+nombreCuarto+" ya existe"))
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
        Mensajes mensajeServidor=deserializaMensaje(entrada.readUTF());
        String nombreCuarto=mensajeCliente.getNombreCuarto();
        if(mensajeServidor.getTipo().equals("INFO") 
                && mensajeServidor.getOperacion().equals("JOIN_ROOM")
                && mensajeServidor.getNombreCuarto().equals(nombreCuarto)
                && mensajeServidor.getMensaje().equals("success")){
            cliente.imprimeMensaje("INFO Se ha unido a la sala satisfactoraimente");
        } else if(mensajeServidor.getTipo().equals("WARINIG")
                && mensajeServidor.getOperacion().equals("JOIN_ROOM")
                && mensajeServidor.getNombreCuarto().equals(nombreCuarto)){
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
        Mensajes mensajeServidor=deserializaMensaje(entrada.readUTF());
        String nombreCuarto=mensajeCliente.getNombreCuarto();
        if(mensajeServidor.getTipo().equals("ROOM_USER_LIST"))
            cliente.imprimeMensaje(mensajeServidor.getNombresUsuarios().toString());
        else if(mensajeServidor.getTipo().equals("WARNING")
                && mensajeServidor.getOperacion().equals("ROOM_USERS")
                && mensajeServidor.getNombreCuarto().equals(nombreCuarto))
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
        Mensajes mensajeServidor=deserializaMensaje(entrada.readUTF());
        String nombreCuarto=mensajeCliente.getNombreCuarto();
        if(mensajeServidor.getTipo().equals("INFO") 
                && mensajeServidor.getOperacion().equals("LEAVE_ROOM")
                && mensajeServidor.getNombreCuarto().equals(nombreCuarto)
                && mensajeServidor.getMensaje().equals("success"))
            cliente.imprimeMensaje("INFO Se ha abandoando el cuarto satisfactoriamente");
        else if(mensajeServidor.getTipo().equals("WARNING")
                && mensajeServidor.getOperacion().equals("LEAVE_ROOM")
                && mensajeServidor.getNombreCuarto().equals(nombreCuarto))
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
        salida.writeUTF(serializaMensaje(mensajeCliente));
        Mensajes mensajeServidor=deserializaMensaje(entrada.readUTF());
        String nombreCuarto=mensajeCliente.getNombreCuarto();
        if(mensajeServidor.getTipo().equals("INFO") 
                && mensajeServidor.getOperacion().equals("INVITE")
                && mensajeServidor.getNombreCuarto().equals(nombreCuarto)
                && mensajeServidor.getMensaje().equals("success"))
            cliente.imprimeMensaje("INFO Se ha invitado a todos los usuarios al cuarto satisfactoriamente");
        else if(mensajeServidor.getTipo().equals("WARNING")
                && mensajeServidor.getOperacion().equals("INVITE")){
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
        salida.writeUTF(serializaMensaje(mensajeCliente));
        Mensajes mensajeServidor=deserializaMensaje(entrada.readUTF());
        String nombreCuarto=mensajeCliente.getNombreCuarto();
        if(mensajeServidor.getTipo().equals("WARNING") 
                && mensajeServidor.getOperacion().equals("ROOM_MESSAGE")
                && mensajeServidor.getNombreCuarto().equals(nombreCuarto))
            cliente.imprimeMensaje(mensajeServidor.getTipo()+" "+mensajeServidor.getMensaje());
    }
}
