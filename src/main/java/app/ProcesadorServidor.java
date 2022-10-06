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
        this.socketCliente= socketCliente;
        this.entrada = new DataInputStream(socketCliente.getInputStream());
        this.salida = new DataOutputStream(socketCliente.getOutputStream());
    }

    @Override
    public void run() {
        try {
            String identificacion= "{\"type\": \"INFO\",\"message\": \"Identificate\"}";
            salida.writeUTF(identificacion);
            String nombreCliente = entrada.readUTF();
            procesaNuevaConexion(nombreCliente);
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
            Mensajes mensaje = MensajesServidorCliente.conTIpoMensajeOperacionUsuario(mensajeConvertir, TiposMensaje.WARNING);
            salida.writeUTF(serializaMensaje(mensaje));
            return;
        }

        this.servidor.agregaUsuario(usuarioCliente);
        String mensajeConvertir = "IDENTIFY success";
        Mensajes mensaje = MensajesServidorCliente.conTipoMensajeOperacion(mensajeConvertir, TiposMensaje.INFO);
        salida.writeUTF(serializaMensaje(mensaje));
        mensajeConvertir = "NEW_USER " + usuarioCliente.getNombre();
        mensaje = MensajesServidorCliente.conTipoUsuario(mensajeConvertir);
        this.servidor.transmiteMensajeClientes(serializaMensaje(mensaje));
        this.servidor.agregaSocketCliente(this.socketCliente);
    }

}
