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
    
    /**Constructor, no recibe ningun parametro e inicializa los @param de la clase en  */
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
    
    /**Metodo que se encarga de incializar la conexion con el servidor
     * Falla si el cliente manda un mensaje diferente a IDENTIFY NombreUsuario
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

    /**Metodo  para el hilo de jecucion que es lanzado en el cliente
     * Se encarga de recibir mensajes del servidor y procesarlos para el cliente
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
    
    /**Metodo que contiene todos los posibles casos de mensajes que puede enviar el cliente por parte del usuario
     * Falla si el mensaje no es valido o contiene un estado no valido*/
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
    /**Metodo que se encarga de procesar los mensajes recibidos por el servidor
     * Contiene los posibles casos de mensajes del protocolo establecido
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
    }
    /**Metodo que se encarga de enviar el mensaje al servidor para pedir la lista de usuarios conectados
     * Ademas de que recibe el mensaje por parte del servidor que contiene la lista de usuarios
     * @param tipo eL tipo de mensaje
     */
    public void recibeListaUsuariosServidor(String tipo) throws JsonProcessingException, IOException{
        Mensajes mensajeCliente= MensajesServidorCliente.conTipo(tipo);
        salida.writeUTF(serializaMensaje(mensajeCliente));
        Mensajes mensajeServidor=deserializaMensaje(entrada.readUTF());
        if(mensajeServidor.getTipo().equals("USER_LIST") && mensajeServidor.getNombresUsuarios() != null){
            cliente.imprimeMensaje(mensajeServidor.getNombresUsuarios().toString());
        }
    }
   
    /**Metodo que encarga de enviar el mensaje al servidor con el nuevo estado del usuario
     * Recibe un mensaje del servidor diciendo si se cumplio la accion o hubo un error
     */
    public void cambiaEstado(String mensaje) throws IOException, ExcepcionEstadoInvalido{
        Mensajes mensajeCliente=MensajesServidorCliente.conTipoEstado(mensaje);
        Mensajes mensajeServidor;
        salida.writeUTF(serializaMensaje(mensajeCliente));
        System.out.println("Antes2");
        String mensajeServidorSerializado=entrada.readUTF();
        mensajeServidor=deserializaMensaje(mensajeServidorSerializado);
        String estado=mensajeCliente.getEstado();
        System.out.println("Antes");
        if(mensajeServidor.getTipo().equals("INFO") && mensajeServidor.getOperacion().equals("STATUS")
                ){
            System.out.println("CAmbiando");
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
    
    /**Metodo que se encarga de enviar un mensaje privado a un usuario quu esta conectado con el servidor
     *  si el usuario no existe se imprime el error que manda el mensaje del servidor
    */
    public void enviaMensajePrivado(String mensaje) throws JsonProcessingException, IOException{
        Mensajes mensajeCliente=MensajesServidorCliente.conTipoUsuarioMensaje(mensaje);
        salida.writeUTF(serializaMensaje(mensajeCliente));
        String mensajeServidorSerializado=entrada.readUTF();
        if(mensajeServidorSerializado != null || !(mensajeServidorSerializado.equals(""))){
            Mensajes mensajeServidor=deserializaMensaje(mensajeServidorSerializado);
            if(mensajeServidor.getTipo().equals("WARNING") && 
                    mensajeServidor.getOperacion().equals("MESSAGE") &&
                    mensajeServidor.getNombreUsuario().equals(mensajeCliente.getNombreUsuario()) &&
                    mensajeServidor.getMensaje().equals("El usaurio "+mensajeCliente.getNombreUsuario()+" no existe") )
               cliente.imprimeMensaje(mensajeServidor.getTipo()+mensajeServidor.getMensaje());
        }
    }
    
    /**Metodo que se encarga de enviar un mensaje publico por parte del usuario al servidor */
    public void enviaMensajePublico(String mensaje) throws JsonProcessingException, IOException{
        Mensajes mensajeServidor=MensajesServidorCliente.conTipoMensaje(mensaje);
        salida.writeUTF(serializaMensaje(mensajeServidor));
    }
    
    /**Metodo que se encarga de enviar un mensaje con la operacion NEW_ROOM y el nombre de la sala al servidor.
     * El servidor responde si se completo la accion satisfactoriamente o hubo un error.
     */
    public void creaNuevoCuarto(String mensaje) throws JsonProcessingException, IOException{
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
    
    /**Metodo que se encarga de enviar un mensaje al servidor con la Operacion JOIN_ROOM y el nombre de sala.
     * El servidor responde si se completo la accion satisfactoraimente o hubo un problema.
     */
    public void unirseCuarto(String mensaje) throws JsonProcessingException, IOException{
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
    
    /**Metodo que se encarga de pedir la lista de usuarios de una sala determinda
     * Si la sala existe y el usuario esta unido a ella el servidor responde con la lista de usuarios
     * 
     */
    public void recibeUsuariosCuarto(String mensaje) throws JsonProcessingException, IOException{
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
    
    public void abandonaCuarto(String mensaje) throws JsonProcessingException, IOException{
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
    
    public void invitaUsuariosCuarto(String mensaje) throws JsonProcessingException, IOException{
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
    
    public void enviaMensajeCuarto(String mensaje) throws JsonProcessingException, IOException{
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
