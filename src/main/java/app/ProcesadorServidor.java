package app;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.Transmitter;

public class ProcesadorServidor extends Procesador {

    private Servidor servidor;
    private Socket socketCliente;

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
        Usuario usuarioCliente = new Usuario(mensajeCliente.getNombreUsuario());
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
        while (true) {
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
        } catch (IOException ex) {
            Logger.getLogger(ProcesadorServidor.class.getName()).log(Level.SEVERE, null, ex);
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
}
