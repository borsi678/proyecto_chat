package app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.EOFException;
/**
 * <p> Clase que extiende de Procesador para implemetar el comportamiento del servidor</p>
 * <p> La clase se encarga de recibir los mennsajes del cliente, procesarlos y enviar los mensajes
 * los clientes con las operaciones especificadas</p>
 */
public class ProcesadorServidor extends Procesador {

    /**El servidor */
    private Servidor servidor;
    /**EL enchuife del cliente conectado */
    private Socket socketCliente;
    /**El usuario del cliente */
    private Usuario usuarioCliente;
    /**Escape de bucles */
    private boolean salir=true;
    
    /**Constructor unico que recibe un servidor y un socket
     * @param servidor el servidor
     * @param socketCliente el socket de la conexion del cliente
      */
    public ProcesadorServidor(Servidor servidor, Socket socketCliente) throws IOException {
        this.servidor = servidor;
        this.socketCliente = socketCliente;
        this.entrada = new DataInputStream(socketCliente.getInputStream());
        this.salida = new DataOutputStream(socketCliente.getOutputStream());
    }

    /**Metodo para el hilo de ejecucion que se encarga de inicializar la conexion con el cliente,
     * asi como procesar los mensajes que envie el cliente conectado.
     */
    @Override
    public void run() {
        try {
            String nombreCliente = entrada.readUTF();
            procesaNuevaConexion(nombreCliente);
            recibeMensajesCliente();
        }catch(ExcepcionMensajeInvalido ex){
            System.out.println("El cliente envio un mensaje no valido");
        }catch(EOFException ex){
            System.out.println("Error al hacer conexion con el cliente");
        } catch (IOException ex) {
            Logger.getLogger(ProcesadorServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**Metodo que se encarga de inciarlizar la conexcion con el cliente, recibiendo su identificacion.
     * Se envia un mensaje si se realizo la operacion o hubo un error.
     * @param nombreCliente el mensaje que contiene el nombre del cliente
     * @throws ExcepcionDeserializa si no se pudo deserializar el mensaje del cliente
     * @throws ExcepcionSerializa si no se pudo serializar el mensaje del servidor.
     * @throws IOException si ocurrio un error con la conexion del cliente
     */
    public void procesaNuevaConexion(String nombreCliente) throws ExcepcionSerializa,ExcepcionDeserializa, IOException {
        if (!(nombreCliente.startsWith("{") && nombreCliente.endsWith("}"))) {
            throw new ExcepcionMensajeInvalido("Mensaje con el formato incorrecto");
        }
        Mensajes mensajeCliente = deserializaMensaje(nombreCliente);
        usuarioCliente = new Usuario(mensajeCliente.getNombreUsuario());
        if (servidor.contieneUsuario(usuarioCliente)) {
            String mensajeConvertir = "IDENTIFY " + usuarioCliente.getNombre() + " El usuario \'"
                    + usuarioCliente.getNombre() + "\' ya existe";
            Mensajes mensaje = MensajesServidorCliente.conTIpoMensajeOperacionUsuario(mensajeConvertir,
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensaje));
            return;
        }
        String mensajeConvertir = "IDENTIFY success";
        Mensajes mensaje = MensajesServidorCliente.conTipoMensajeOperacion(mensajeConvertir, TiposMensaje.INFO);
        salida.writeUTF(serializaMensaje(mensaje));
        mensajeConvertir = "NEW_USER " + usuarioCliente.getNombre();
        mensaje = MensajesServidorCliente.conTipoUsuario(mensajeConvertir);
        this.servidor.transmiteMensajeClientes(serializaMensaje(mensaje));
        this.servidor.agregaSocketCliente(usuarioCliente, this.socketCliente);
    }

    /**Metodo que se encarga de recibir y procesar los emnsajes del cliente. */
    public void recibeMensajesCliente() {
        String mensajeClienteSerializado = "";
        Mensajes mensajeCliente;
        while (salir) {
            try {
                mensajeClienteSerializado = entrada.readUTF();
                mensajeCliente = deserializaMensaje(mensajeClienteSerializado);
                mensajesRecibidos(mensajeCliente);
            }catch(ExcepcionDeserializa ex){
                System.out.println("Error no se pudo deserailzar el mensaje");
            } catch(SocketException ex){
                servidor.eliminaConexion(usuarioCliente, socketCliente);
                servidor.eliminaUsuarioCuartos(usuarioCliente);
                return;
            } catch (IOException ex) {
                System.out.println("Ocurrio un error con la conexion del cliente: "+usuarioCliente.getNombre());
                return;
            }
        }
    }
    /**Metodo que se encarga de procesar los mensajes recibidos por el cliente, las posibles 
     * operaciones son: STATUS, USERS, MESSAGE, PUBLIC_MESSAGE, NEW_ROOM, INVITE, JOIN_ROOM,
     * ROOM_USERS, ROOM_MESSAGE, LEAVE_ROOM, DICONNECT.
     * @param mensaje el mensaje a procesar.
     */
    @Override
    public void mensajesRecibidos(Mensajes mensaje) {
        try {
            switch(mensaje.getTipo().toUpperCase()){
                case "STATUS":
                    usuarioEstado(mensaje);
                    break;
                case "USERS":    
                    listaUsuarios();
                    break;
                case "MESSAGE":
                    mensajePrivado(mensaje);
                    break;
                case "PUBLIC_MESSAGE":
                    mensajePublico(mensaje);
                    break;
                case "NEW_ROOM":
                    nuevoCuarto(mensaje);
                    break;
                case "INVITE":    
                    invitaUsuarios(mensaje);
                    break;
                case "JOIN_ROOM":    
                    unirseSala(mensaje);
                    break;
                case "ROOM_USERS":    
                    usuariosCuarto(mensaje);
                    break;
                case "ROOM_MESSAGE":
                    cuartoMensaje(mensaje);
                    break;
                case "LEAVE_ROOM":
                    abandonaCuarto(mensaje);
                    break;
                case "DISCONNECT":    
                    desconectaUsuario();
                    break;
            }
        } catch (ExcepcionEstadoInvalido ex){
            String excepcion =String.format("Error en la operacion %s Estado invalido.", mensaje.getTipo());
            System.out.println(excepcion);
            System.out.println(ex.toString());
        } catch (ExcepcionMensajeInvalido ex ){
            String excepcion =String.format("Error en la operacion %s Tipo de Mensaje invalido.", mensaje.getTipo());
            System.out.println(excepcion);
            System.out.println(ex.toString());
        } catch(ExcepcionDeserializa ex){
            String excepcion =String.format("Error en la operacion %s Desereailizacion fallo", mensaje.getTipo());
            System.out.println(excepcion);
            System.out.println(ex.toString());
        } catch(ExcepcionSerializa ex){
            String excepcion =String.format("Error en la operacion %s Serializacion fallo.", mensaje.getTipo());
            System.out.println(excepcion);
            System.out.println(ex.toString());
        }  catch (IOException ex) {
            String excepcion =String.format("Error en la operacion %s Problema con la conexion con el cliente.", mensaje.getTipo());
            System.out.println(excepcion);
            System.out.println(ex.toString());
        }
    }
    /**
     * Metodo que se encarga de procesar el mensaje del cliente para cambiar el estado del usuario.
     * Si el estado a cambiar es el que tiene el usuario ocurre un error y se envia el mensaje del error.
     * Si el estado se cambia correctamente se envia el mensaje al cliente y se notifica a los demas
     * clientes conectados que el usuario cambio de estado.
     * @param mensaje el mensaje a procesar 
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del servidor
     * @throws IOException si hubo un error con la conexion
     */
    public void usuarioEstado(Mensajes mensaje) throws IOException, ExcepcionSerializa {
        EstadoUsuario estado = MensajesServidorCliente.convertirCadenaAEstadoUsuario(mensaje.getEstado());
        Mensajes mensajeServidor;
        if (estado == EstadoUsuario.ACTIVE || estado == EstadoUsuario.BUSY || estado == EstadoUsuario.AWAY) {
            Usuario usuario = servidor.getUsuario(this.socketCliente);
            if (usuario.getEstado() == estado) {
                String mensajeEnviar = String.format("STATUS %s El estado ya es '%s'", estado.toString(), estado.toString());
                mensajeServidor = MensajesServidorCliente.conTipoMensajeOperacionEstado(mensajeEnviar,
                        TiposMensaje.WARNING);
                salida.writeUTF(serializaMensaje(mensajeServidor));
                return;
            }
            servidor.cambiaEstadoUsuario(usuario.getNombre(), estado);
            mensajeServidor = MensajesServidorCliente.conTipoMensajeOperacion("STATUS success", TiposMensaje.INFO);
            salida.writeUTF(serializaMensaje(mensajeServidor));
            salida.flush();
            mensajeServidor = MensajesServidorCliente.conTipoUsuarioEstado(
                    String.format("NEW_STATUS %s %s", usuario.getNombre(), estado.toString()));
            String mensajeServidorSerializado = serializaMensaje(mensajeServidor);
            servidor.transmiteMensajeClientes(mensajeServidorSerializado);
        }
    }
    
    /**
     * Metodo para enviar el mensaje al cliente con la lista de usuarios conectado al chat
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del servidor
     * @throws IOException si hubo un error con la conexion
     */
    public void listaUsuarios() throws ExcepcionSerializa, IOException{
        Mensajes mensajeServidor=MensajesServidorCliente.conTipoUsuarios("USER_LIST", 
                servidor.getListaUsuariosConectados());
        salida.writeUTF(serializaMensaje(mensajeServidor));
    }
    
    /**
     * Metodo que se encarga de enviar un mensaje privado a un determinado usuario.
     * SI el usuario no existe ocurre un error.
     * SI el usuario existe se envia el mensaje con el contenido.
     * @param mensaje el mensaje a procesar
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del servidor
     * @throws IOException si hubo un error con la conexion
     */
    public void mensajePrivado(Mensajes mensaje) throws ExcepcionSerializa, IOException{
         if(servidor.contieneUsuario(mensaje.getNombreUsuario())){
            Usuario usuarioTransmisor=servidor.getUsuario(socketCliente);
            Usuario usuarioReceptor=servidor.getUsuario(mensaje.getNombreUsuario());            
            mensaje.setTipo("MESSAGE_FROM");
            mensaje.setNombreUsuario(usuarioTransmisor.getNombre());
            servidor.transmiteMensajePrivado(serializaMensaje(mensaje), usuarioReceptor);
            return;
        }
        String mensajeEnviar=String.format("MESSAGE %s El usuario '%s' no existe", mensaje.getNombreUsuario(),
                mensaje.getNombreUsuario());
        Mensajes mensajeServidor=MensajesServidorCliente.conTIpoMensajeOperacionUsuario(mensajeEnviar, 
                TiposMensaje.WARNING);
        salida.writeUTF(serializaMensaje(mensajeServidor));
    }
    
    /**
     * Metodo que se encarga de enviar un mensaje publico a todos los clientes conectados.
     * @param mensaje el mensaje a procesar
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del servidor
     * @throws IOException si hubo un error con la conexion
     */
    public void mensajePublico(Mensajes mensaje) throws ExcepcionSerializa, IOException{
        Usuario usuarioTransmisor=servidor.getUsuario(socketCliente);
        mensaje.setTipo("PUBLIC_MESSAGE_FROM");
        mensaje.setNombreUsuario(usuarioTransmisor.getNombre());
        servidor.transmiteMensajeClientes(serializaMensaje(mensaje));
    }

    /**
     * Metodo que se encarga de crear un nuevo cuarto con el nombre especificado.
     * Si el nombre del cuarto ya existe ocurre un error.
     * Si no se crea y se envia un mensaje al cliente.
     * @param mensaje el mensaje a procesar
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del servidor
     * @throws IOException si hubo un error con la conexion
     */
    public void nuevoCuarto(Mensajes mensaje) throws ExcepcionSerializa, IOException{
        String nombreCuarto=mensaje.getNombreCuarto();
        if(!(servidor.contieneCuarto(nombreCuarto))){
            servidor.creaCuarto(nombreCuarto, this.usuarioCliente);
            Mensajes mensajeServidor= MensajesServidorCliente.conTipoMensaje("INFO success");
            mensajeServidor.setOperacion("NEW_ROOM");
            salida.writeUTF(serializaMensaje(mensajeServidor));
        } else{
        String mensajeEnviar=String.format("NEW_ROOM %s El cuarto '%s' ya existe", nombreCuarto, nombreCuarto);
        Mensajes mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar, 
                TiposMensaje.WARNING);
        salida.writeUTF(serializaMensaje(mensajeServidor));
        }
        salida.flush();
    }
    
    /**
     * Metodo que se encarga de enviar el mensaje con una invitacion a los usuarios especificados
     * Si el cuarto no existe, el usuario no esta unido al cuarto o no existe un usuario ocurre un error.
     * Si no se envia la invitacion a todos los usuarios y se responde al cliente que envio la solicitud.
     * @param mensaje el mensaje a procesar
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del servidor
     * @throws IOException si hubo un error con la conexion
     */
    public void invitaUsuarios(Mensajes mensaje) throws ExcepcionSerializa, IOException{
        LinkedList<Mensajes> invitaciones=new LinkedList<Mensajes>();
        String [] usuariosInvitar=mensaje.getNombresUsuarios();
        String nombreCuarto=mensaje.getNombreCuarto();
        Mensajes mensajeServidor;
        String mensajeEnviar;        
        if(servidor.contieneCuarto(nombreCuarto) && servidor.estaUsuarioUnidoACuarto(usuarioCliente, nombreCuarto)){
            for(String usuarioContenido : usuariosInvitar){ //Checamos si no existe algun usuario para invitar
                if(!(servidor.contieneUsuario(usuarioContenido))){
                    mensajeEnviar= String.format("INVITE %s El usuario '%s' no existe",usuarioContenido, usuarioContenido);
                    mensajeServidor=MensajesServidorCliente.conTIpoMensajeOperacionUsuario(mensajeEnviar, 
                            TiposMensaje.WARNING);
                    salida.writeUTF(serializaMensaje(mensajeServidor));
                    return;
                }
            }
            mensajeEnviar=String.format("INVITATION %s %s %s te invita al cuarto '%s'", usuarioCliente.getNombre()
            , nombreCuarto, usuarioCliente.getNombre(), nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeUsuarioNombreCuarto(mensajeEnviar);
            Usuario usuarioInvitado;
            for(String usuarioContenido : usuariosInvitar){ //Si todos los usuarios existen procedemos a enviarles el mensaje
                // y agregarlos al cuarto
                usuarioInvitado=servidor.getUsuario(usuarioContenido);
                if(!servidor.estaUsuarioInvitadoACuarto(usuarioInvitado, nombreCuarto))
                        servidor.agregaUsuarioInvitadoACuarto(usuarioInvitado, nombreCuarto);
                servidor.transmiteMensajePrivado(serializaMensaje(mensajeServidor), usuarioInvitado);
            }
            mensajeEnviar=String.format("INVITE %s success", nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.INFO);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        }else if(!(servidor.contieneCuarto(nombreCuarto))){
            mensajeEnviar=String.format("INVITE %s El cuarto '%s' no existe", nombreCuarto, nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar, 
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        } else {
            mensajeEnviar=String.format("INVITE %s El usuario no se unido al cuarto", nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar, 
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        }
        salida.flush();
        
    }
    
    /**
     * Metodo que se encarga de procesar la solicitad de un usuario para unirse a un cuarto determinado.
     * Si el cuarto no existe, el usuario no ha sido invitado o ya se unio al cuarto ocurre un error.
     * Si se realiza la operacion se envia un mensaje al cliente.
     * @param mensaje el mensaje a procesar
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del servidor
     * @throws IOException si hubo un error con la conexion
     */
    public void unirseSala(Mensajes mensaje) throws ExcepcionSerializa, IOException{
        String nombreCuarto=mensaje.getNombreCuarto();
        Mensajes mensajeServidor;
        String mensajeEnviar;
        if(servidor.contieneCuarto(nombreCuarto) && servidor.estaUsuarioInvitadoACuarto(usuarioCliente, 
                nombreCuarto)){
            servidor.eliminaUsuarioInvitadoACuarto(usuarioCliente, nombreCuarto);
            mensajeEnviar=String.format("JOINED_ROOM %s %s" , nombreCuarto, usuarioCliente.getNombre());
            mensajeServidor=MensajesServidorCliente.conTipoNombreCuartoUsuario(mensajeEnviar);
            servidor.transmiteMensajeACuarto(serializaMensaje(mensajeServidor), nombreCuarto);
            servidor.agregaUsuarioUnidoACuarto(usuarioCliente, nombreCuarto);
            mensajeEnviar=String.format("JOIN_ROOM %s success", nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.INFO);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        }else if(!(servidor.contieneCuarto(nombreCuarto))){
            mensajeEnviar=String.format("JOIN_ROOM %s El cuarto '%s' no existe", nombreCuarto, nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar
                    , TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));

        }else if(servidor.estaUsuarioUnidoACuarto(usuarioCliente, nombreCuarto)){
                mensajeEnviar=String.format("JOIN_ROOM %s El usuario ya se unio al cuarto '%s'", nombreCuarto, nombreCuarto);
                mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar, 
                        TiposMensaje.WARNING);
                salida.writeUTF(serializaMensaje(mensajeServidor));
        }else if(!(servidor.estaUsuarioInvitadoACuarto(usuarioCliente, nombreCuarto))){
            mensajeEnviar=String.format("JOIN_ROOM %s El usuario no ha sido invitado al cuarto", nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar, 
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));

        }
        salida.flush();
    }
    
    /**
     * Metodo que se encarga de enviar la lista de usuarios conectados en un determinado cuarto.
     * Si el cuarto no existe o el usuario no se ha unido  ocurre un error.
     * Si no se envia el mensaje con la lista de usuarios del cuarto.
     * @param mensaje el mensaje a procesar
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del servidor
     * @throws IOException si hubo un error con la conexion
     */
    public void usuariosCuarto(Mensajes mensaje) throws ExcepcionSerializa, IOException{
        String nombreCuarto= mensaje.getNombreCuarto();
        String mensajeEnviar;
        Mensajes mensajeServidor;
        if(servidor.contieneCuarto(nombreCuarto) && servidor.estaUsuarioUnidoACuarto(usuarioCliente,
                nombreCuarto)){
            LinkedList<Usuario> listaUsuariosCuarto=servidor.getListaUsuariosCuarto(nombreCuarto);
            String[] usuariosCuarto=new String[listaUsuariosCuarto.size()];
            int contador=0;
            for(Usuario usuario : listaUsuariosCuarto){
                usuariosCuarto[contador++]=usuario.getNombre();
            }
            mensajeServidor= MensajesServidorCliente.conTipoUsuarios("ROOM_USER_LIST", usuariosCuarto);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        } else if(!(servidor.contieneCuarto(nombreCuarto))){
            mensajeEnviar= String.format("ROOM_USERS %s El cuarto '%s' no existe",nombreCuarto, nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        } else {
            mensajeEnviar=String.format("ROOM_USERS %s EL usuario no se ha unido al cuarto '%s'", nombreCuarto, nombreCuarto);
            mensajeServidor= MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        }
        salida.flush();
    }
    
    /**
     *  Metodo que se encarga de enviar un mensaje a los usuarios conectados en un cuarto.
     * Si el cuarto no existe o el usuario no se ha unido al cuarto ocurre un error.
     * Si no se envia el mensaje a todos los usuarios del cuarto.
     * @param mensaje el mensaje a procesar
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del servidor
     * @throws IOException si hubo un error con la conexion
     */
    public void cuartoMensaje(Mensajes mensaje) throws ExcepcionSerializa, IOException{
        String nombreCuarto= mensaje.getNombreCuarto();
        String mensajeEnviar;
        Mensajes mensajeServidor;
        if(servidor.contieneCuarto(nombreCuarto) && servidor.estaUsuarioUnidoACuarto(usuarioCliente,
                nombreCuarto)){
            mensajeEnviar= String.format("ROOM_MESSAGE_FROM %s %s %s", nombreCuarto, usuarioCliente.getNombre(),
                    mensaje.getMensaje());
            mensajeServidor = MensajesServidorCliente.conTipoNombreCuartoUsuarioMensaje(mensajeEnviar);
            servidor.transmiteMensajeACuarto(serializaMensaje(mensajeServidor), nombreCuarto);
        }else if(!(servidor.contieneCuarto(nombreCuarto))){
            mensajeEnviar= String.format("ROOM_MESSAGE %s El cuarto '%s' no existe",nombreCuarto, nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        } else {
            mensajeEnviar=String.format("ROOM_MESSAGE %s EL usuario no se ha unido al cuarto '%s'", nombreCuarto, nombreCuarto);
            mensajeServidor= MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        }
    }
    
    /**
     * Metodo que se encarga de abandonar un cuarto por el usuario.
     * Si el cuarto no existe o el usuario no se ha unido al cuarto ocurre un error.
     * Si se realizo la operacion se envia un mensaje al usuario.
     * @param mensaje el mensaje a procesar
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del servidor
     * @throws IOException si hubo un error con la conexion
     */
    public void abandonaCuarto(Mensajes mensaje) throws ExcepcionSerializa, IOException{
        String nombreCuarto= mensaje.getNombreCuarto();
        String mensajeEnviar;
        Mensajes mensajeServidor;
        if(servidor.contieneCuarto(nombreCuarto) && servidor.estaUsuarioUnidoACuarto(usuarioCliente,
                nombreCuarto)){
            servidor.eliminaUsuarioUnidoACuarto(usuarioCliente, nombreCuarto);
            mensajeEnviar=String.format("LEAVE_ROOM %s success", nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.INFO);
            salida.writeUTF(serializaMensaje(mensajeServidor));
            mensajeEnviar=String.format("LEFT_ROOM %s %s", nombreCuarto, usuarioCliente.getNombre());
            mensajeServidor=MensajesServidorCliente.conTipoNombreCuartoUsuario(mensajeEnviar);
            servidor.transmiteMensajeACuarto(serializaMensaje(mensajeServidor), nombreCuarto);
        }else if(!(servidor.contieneCuarto(nombreCuarto))){
            mensajeEnviar= String.format("LEAVE_ROOM %s El cuarto '%s' no existe",nombreCuarto, nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        } else {
            mensajeEnviar=String.format("LEAVE_ROOM %s EL usuario no se ha unido al cuarto '%s'", nombreCuarto, nombreCuarto);
            mensajeServidor= MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        }
    }
    
    /**
     * Metodo que se encarga de desconectar al usuario del servidor.
     * Si el usuario se habia unido a cuartos se fuerza el abandono a estos, ademas se envia un mensaje 
     * a todos los clientes conectados diciendo que el usuario abandono el servidor.
     * @throws ExcepcionSerializa si hubo un error al serializar el mensaje del servidor
     * @throws IOException si hubo un error con la conexion
     */
    public void desconectaUsuario() throws ExcepcionSerializa, IOException{
        String mensajeEnviar=String.format("DISCONNECTED %s", usuarioCliente.getNombre());
        Mensajes mensajeServidor= MensajesServidorCliente.conTipoUsuario(mensajeEnviar);
        servidor.eliminaUsuarioUnidoACuarto(usuarioCliente, "General");
        salida.writeUTF(serializaMensaje(mensajeServidor));
        LinkedList<Cuarto> listaCuartos=servidor.getListaCuartosServidor();
        mensajeEnviar=String.format("LEFT_ROOM %s ", usuarioCliente.getNombre());
        mensajeServidor = MensajesServidorCliente.conTipoUsuario(mensajeEnviar);
        String nombreCuarto;
        for(Cuarto cuarto : listaCuartos){
            if(cuarto.estaUsuarioUnido(usuarioCliente)){
                nombreCuarto=cuarto.getNombre();
                mensajeServidor.setNombreCuarto(nombreCuarto);
                servidor.eliminaUsuarioUnidoACuarto(usuarioCliente, nombreCuarto);
                servidor.transmiteMensajeACuarto(serializaMensaje(mensajeServidor), nombreCuarto);
            }
        }
        salir=false;
    }
}
