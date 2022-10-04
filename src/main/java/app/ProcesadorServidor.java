package app;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import javax.sound.midi.Transmitter;

public class ProcesadorServidor extends Procesador{
    
    private Servidor servidor;
    private DataInputStream entrada;
    private DataOutputStream salida;
    
    public ProcesadorServidor(Servidor servidor, DataInputStream entrada, DataOutputStream salida){
        this.servidor=servidor;
        this.entrada=entrada;
        this.salida=salida;
    }
    
    @Override public void run(){
        
    }
    
    public void procesaNuevaConexion(String nombreCliente) throws JsonProcessingException, IOException{
        if(!(nombreCliente.startsWith("{") && nombreCliente.endsWith("}")))
            throw new ExcepcionMensajeInvalido("Mensaje con el formato incorrecto");
        Mensajes mensajeCliente=deserializaMensaje(nombreCliente);
        Usuario usuarioCliente=new Usuario(mensajeCliente.getNombreUsuario());
        if(servidor.contieneUsuario(usuarioCliente)){
            String mensajeConvertir="IDENTIFY "+usuarioCliente.getNombre()+" El usuario \'"+
                    usuarioCliente.getNombre()+"\' ya existe";
            Mensajes mensaje= MensajesServidorCliente.conTIpoMensajeOperacionUsuario(mensajeConvertir, TiposMensaje.WARNING);
            this.servidor.transmiteMensajeClientes(serializaMensaje(mensaje));
        }
            
        this.servidor.agregaUsuario(usuarioCliente);
        String mensajeConvertir="IDENTIFY success";
        Mensajes mensaje=MensajesServidorCliente.conTipoMensajeOperacion(mensajeConvertir, TiposMensaje.INFO);
        salida.writeUTF(serializaMensaje(mensaje));
        mensajeConvertir="NEW_USER "+usuarioCliente.getNombre();
        mensaje=MensajesServidorCliente.conTipoUsuario(mensajeConvertir);
        this.servidor.transmiteMensajeClientes(serializaMensaje(mensaje));
    }
    
}
