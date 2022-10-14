package app;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.Transmitter;

public class ProcesadorServidor extends Procesador {

    private Servidor servidor;
    private Socket socketCliente;
    private Usuario usuarioCliente;
    private boolean salir=true;
    
    public ProcesadorServidor(Servidor servidor, Socket socketCliente) throws IOException {
        this.servidor = servidor;
        this.socketCliente = socketCliente;
        this.entrada = new DataInputStream(socketCliente.getInputStream());
        this.salida = new DataOutputStream(socketCliente.getOutputStream());
    }

    @Override
    public void run() {
        try {
            String identificacion = "{\"type\": \"INFO\",\"message\": \"Identificate\"}";
            salida.writeUTF(identificacion);
            String nombreCliente = entrada.readUTF();
            procesaNuevaConexion(nombreCliente);
            recibeMensajesCliente();
        } catch (IOException ex) {
            Logger.getLogger(ProcesadorServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void procesaNuevaConexion(String nombreCliente) throws JsonProcessingException, IOException {
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
    public void recibeMensajesCliente() {
        String mensajeClienteSerializado = "";
        Mensajes mensajeCliente;
        while (salir) {
            try {
                mensajeClienteSerializado = entrada.readUTF();
                mensajeCliente = deserializaMensaje(mensajeClienteSerializado);
                mensajesRecibidos(mensajeCliente);
            } catch (IOException ex) {

            }
        }
    }

    @Override
    public void mensajesRecibidos(Mensajes mensaje) {
        try {
            if (mensaje.getTipo().equals("STATUS"))
                usuarioEstado(mensaje);
            else if(mensaje.getTipo().equals("USERS"))
                listaUsuarios();
            else if(mensaje.getTipo().equals("MESSAGE"))
                mensajePrivado(mensaje);
            else if(mensaje.getTipo().equals("PUBLIC_MESSAGE"))
                mensajePublico(mensaje);
            else if(mensaje.getTipo().equals("NEW_ROOM"))
                nuevoCuarto(mensaje);
            else if(mensaje.getTipo().equals("INVITE"))
                invitaUsuarios(mensaje);
            else if(mensaje.getTipo().equals("JOIN_ROOM"))
                unirseSala(mensaje);
            else if(mensaje.getTipo().equals("ROOM_USERS"))
                usuariosCuarto(mensaje);
            else if(mensaje.getTipo().equals("ROOM_MESSAGE"))
                cuartoMensaje(mensaje);
            else if(mensaje.getTipo().equals("LEAVE_ROOM"))
                abandonaCuarto(mensaje);
            else if(mensaje.getTipo().equals("DISCONNECT"))
                desconectaUsuario();
        } catch (IOException ex) {

        }
    }

    public void usuarioEstado(Mensajes mensaje) throws JsonProcessingException, IOException {
        EstadoUsuario estado = MensajesServidorCliente.convertirCadenaAEstadoUsuario(mensaje.getEstado());
        Mensajes mensajeServidor;
        if (estado == EstadoUsuario.ACTIVE || estado == EstadoUsuario.BUSY || estado == EstadoUsuario.AWAY) {
            Usuario usuario = servidor.getUsuario(this.socketCliente);
            if (usuario.getEstado() == estado) {
                String mensajeEnviar = String.format("STATUS %s El estado ya es '%s'", estado.toString());
                mensajeServidor = MensajesServidorCliente.conTipoMensajeOperacionEstado(mensajeEnviar,
                        TiposMensaje.WARNING);
                return;
            }
            servidor.cambiaEstadoUsuario(usuario.getNombre(), estado);
            mensajeServidor = MensajesServidorCliente.conTipoMensajeOperacion("STATUS success", TiposMensaje.INFO);
            salida.writeUTF(serializaMensaje(mensajeServidor));
            mensajeServidor = MensajesServidorCliente.conTipoUsuarioEstado(
                    String.format("NEW_STATUS %s %s", usuario.getNombre(), estado.toString()));
            String mensajeServidorSerializado = serializaMensaje(mensajeServidor);
            servidor.transmiteMensajeClientes(mensajeServidorSerializado);
        }
    }
    
    public void listaUsuarios() throws JsonProcessingException, IOException{
        Mensajes mensajeServidor=MensajesServidorCliente.conTipoUsuarios("USER_LIST", 
                servidor.getListaUsuariosConectados());
        salida.writeUTF(serializaMensaje(mensajeServidor));
    }

    public void mensajePrivado(Mensajes mensaje) throws JsonProcessingException, IOException{

        if(servidor.contieneUsuario(mensaje.getNombreUsuario())){
            Usuario usuarioTransmisor=servidor.getUsuario(socketCliente);
            Usuario usuarioReceptor=servidor.getUsuario(mensaje.getNombreUsuario());            
            mensaje.setTipo("MESSAGE_FROM");
            mensaje.setNombreUsuario(usuarioTransmisor.getNombre());
            servidor.transmiteMensajePrivado(serializaMensaje(mensaje), usuarioReceptor);
            return;
        }
        String mensajeEnviar=String.format("MESSAGE %s El usuario '%s' no existe", mensaje.getNombreUsuario());
        Mensajes mensajeServidor=MensajesServidorCliente.conTIpoMensajeOperacionUsuario(mensajeEnviar, 
                TiposMensaje.WARNING);
    }
    
    public void mensajePublico(Mensajes mensaje) throws JsonProcessingException, IOException{
        Usuario usuarioTransmisor=servidor.getUsuario(socketCliente);
        mensaje.setTipo("PUBLIC_MESSAGE_FROM");
        mensaje.setNombreUsuario(usuarioTransmisor.getNombre());
        servidor.transmiteMensajeClientes(serializaMensaje(mensaje));
    }

    public void nuevoCuarto(Mensajes mensaje) throws JsonProcessingException, IOException{
        String nombreCuarto=mensaje.getNombreCuarto();
        if(!(servidor.contieneCuarto(nombreCuarto))){
            Usuario usuarioCreador= servidor.getUsuario(socketCliente);
            servidor.creaCuarto(nombreCuarto, usuarioCreador);
            Mensajes mensajeServidor= MensajesServidorCliente.conTipoMensaje("INFO success");
            mensajeServidor.setOperacion("NEW_ROOM");
            salida.writeUTF(serializaMensaje(mensajeServidor));
            return;
        }
        String mensajeEnviar=String.format("NEW_ROOM %s El cuarto '%s' ya existe", nombreCuarto);
        Mensajes mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar, 
                TiposMensaje.WARNING);
        salida.writeUTF(serializaMensaje(mensajeServidor));
    }
    
    public void invitaUsuarios(Mensajes mensaje) throws JsonProcessingException, IOException{
        LinkedList<Mensajes> invitaciones=new LinkedList<Mensajes>();
        String [] usuariosInvitar=mensaje.getNombresUsuarios();
        String nombreCuarto=mensaje.getNombreCuarto();
        Mensajes mensajeServidor;
        String mensajeEnviar;        
        if(servidor.contieneCuarto(nombreCuarto)){
            for(String usuarioContenido : usuariosInvitar){ //Checamos si no existe algun usuario para invitar
                if(!(servidor.contieneUsuario(usuarioContenido))){
                    mensajeEnviar= String.format("INVITE %s El usuario '%s' no existe",usuarioContenido);
                    mensajeServidor=MensajesServidorCliente.conTIpoMensajeOperacionUsuario(mensajeEnviar, 
                            TiposMensaje.WARNING);
                    salida.writeUTF(serializaMensaje(mensajeServidor));
                    return;
                }
            }
            mensajeEnviar=String.format("INVITATION %s %s % te invita al cuarto '%s'", usuarioCliente.getNombre()
            , nombreCuarto, usuarioCliente.getNombre(), nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeUsuarioNombreCuarto(mensajeEnviar);
            Usuario usuarioInvitado;
            for(String usuarioContenido : usuariosInvitar){ //Si todos los usuarios existen procedemos a enviarles el mensaje
                // y agregarlos al cuarto
                usuarioInvitado=servidor.getUsuario(usuarioContenido);
                servidor.agregaUsuarioInvitadoACuarto(usuarioInvitado, nombreCuarto);
                servidor.transmiteMensajePrivado(serializaMensaje(mensajeServidor), usuarioInvitado);
            }
            mensajeEnviar=String.format("INVITE %s success", nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.INFO);
            salida.writeUTF(serializaMensaje(mensajeServidor));
            return;
        }
        mensajeEnviar=String.format("INVITE %s El cuarto '%s' no existe", nombreCuarto);
        mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar, 
                TiposMensaje.WARNING);
    }
    
    public void unirseSala(Mensajes mensaje) throws JsonProcessingException, IOException{
        String nombreCuarto=mensaje.getNombreCuarto();
        Mensajes mensajeServidor;
        String mensajeEnviar;
        if(servidor.contieneCuarto(nombreCuarto) && servidor.estaUsuarioInvitadoACuarto(usuarioCliente, 
                nombreCuarto)){
            if(servidor.estaUsuarioUnidoACuarto(usuarioCliente, nombreCuarto)){
                mensajeEnviar=String.format("JOIN_ROOM %s El usuario ya se unio al cuarto '%s'", nombreCuarto);
                mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar, 
                        TiposMensaje.WARNING);
                salida.writeUTF(serializaMensaje(mensajeServidor));
                return;
            }
            servidor.eliminaUsuarioInvitadoACuarto(usuarioCliente, nombreCuarto);
            mensajeEnviar=String.format("JOINED_ROOM %s  %s" , nombreCuarto, usuarioCliente.getNombre());
            mensajeServidor=MensajesServidorCliente.conTipoNombreCuartoUsuario(mensajeEnviar);
            servidor.transmiteMensajeACuarto(serializaMensaje(mensajeServidor), nombreCuarto);
            servidor.agregaUsuarioUnidoACuarto(usuarioCliente, nombreCuarto);
            mensajeEnviar=String.format("JOIN_ROOM %s success", nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.INFO);
            return;
        }
        if(!(servidor.estaUsuarioInvitadoACuarto(usuarioCliente, nombreCuarto))){
                mensajeEnviar=String.format("JOIN_ROOM %s El usuario no ha sido invitado al cuarto", nombreCuarto);
                mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar, 
                        TiposMensaje.WARNING);
                salida.writeUTF(serializaMensaje(mensajeServidor));
                return;
            }
        mensajeEnviar=String.format("JOIN_ROOM %s El cuarto '%s' no existe", nombreCuarto);
        mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar
                , TiposMensaje.WARNING);
        salida.writeUTF(serializaMensaje(mensajeServidor));
    }
    
    public void usuariosCuarto(Mensajes mensaje) throws JsonProcessingException, IOException{
        String nombreCuarto= mensaje.getNombreCuarto();
        String mensajeEnviar;
        Mensajes mensajeServidor;
        if(servidor.contieneCuarto(nombreCuarto) && servidor.estaUsuarioUnidoACuarto(usuarioCliente,
                nombreCuarto)){
            LinkedList<Usuario> listaUsuariosCuarto=servidor.getListaUsuariosCuarto(nombreCuarto);
            String[] usuariosCuarto=new String[listaUsuariosCuarto.size()];
            int contador=0;
            for(Usuario usuario : listaUsuariosCuarto){
                usuariosCuarto[contador]=usuario.getNombre();
            }
            mensajeServidor= MensajesServidorCliente.conTipoUsuarios("ROOM_USER_LIST", usuariosCuarto);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        } else if(!(servidor.contieneCuarto(nombreCuarto))){
            mensajeEnviar= String.format("ROOM_USERS %s El cuarto '%s' no existe",nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        } else {
            mensajeEnviar=String.format("ROOM_USERS %s EL usuario no se ha unido al cuarto '%s'", nombreCuarto);
            mensajeServidor= MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        }
        
    }
    
    public void cuartoMensaje(Mensajes mensaje) throws JsonProcessingException, IOException{
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
            mensajeEnviar= String.format("ROOM_MESSAGE %s El cuarto '%s' no existe",nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        } else {
            mensajeEnviar=String.format("ROOM_MESSAGE %s EL usuario no se ha unido al cuarto '%s'", nombreCuarto);
            mensajeServidor= MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        }
    }
    
    public void abandonaCuarto(Mensajes mensaje) throws JsonProcessingException, IOException{
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
            mensajeEnviar=String.format("LEFT_ROOM %s %s ", nombreCuarto, usuarioCliente.getNombre());
            mensajeServidor=MensajesServidorCliente.conTipoNombreCuartoUsuario(mensajeEnviar);
            servidor.transmiteMensajeACuarto(serializaMensaje(mensajeServidor), nombreCuarto);
        }else if(!(servidor.contieneCuarto(nombreCuarto))){
            mensajeEnviar= String.format("LEAVE_ROOM %s El cuarto '%s' no existe",nombreCuarto);
            mensajeServidor=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        } else {
            mensajeEnviar=String.format("LEAVE_ROOM %s EL usuario no se ha unido al cuarto '%s'", nombreCuarto);
            mensajeServidor= MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEnviar,
                    TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensajeServidor));
        }
    }
    
    public void desconectaUsuario() throws JsonProcessingException, IOException{
        String mensajeEnviar=String.format("DISCONNECTED %s", usuarioCliente.getNombre());
        Mensajes mensajeServidor= MensajesServidorCliente.conTipoUsuario(mensajeEnviar);
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
