package app;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcesadorCliente extends Procesador{
    
    private Cliente cliente;
    private Scanner scanner;
    private Socket socket;
    
    public ProcesadorCliente(Cliente cliente){
        this.cliente=cliente;
        this.in=null;
        this.out=null;
        this.socket=null;
        this.scanner=new Scanner(System.in);
    }
    
    public void iniciaConexion(String direccionIP, int puerto){
        try {
            socket=new Socket(direccionIP, puerto);
            in= new DataInputStream(socket.getInputStream());
            out= new DataOutputStream(socket.getOutputStream());
            String cadena=in.readUTF();
            System.out.println(cadena);
            
            String nombre=scanner.nextLine();
            Mensajes mensaje=MensajesServidorCliente.conTipoUsuario("IDENTIFY "+nombre);
            out.writeUTF(serializaMensaje(mensaje));
            System.out.println(in.readUTF());
            this.start();
            
        } catch (IOException ex) {
            Logger.getLogger(ProcesadorCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override public void run(){
        String mensaje="";
        while(!(mensaje.equals("DISCONNECT"))){
            try {
                mensaje=scanner.nextLine();
                if(mensaje.equals("") || mensaje == null)
                    continue;
                menuMensajes(mensaje);
            } catch (IOException ex) {
                System.out.println("No se pudo enviar el mensaje, intentelo de nuevo.");
            }
            
        }
    }
    public void menuMensajes(String mensaje) throws JsonProcessingException, IOException{
        String[] argumentoMensaje=mensaje.split(" ");
        if(argumentoMensaje[0].equals("USERS") ){
            Mensajes mensajeEnviar = MensajesServidorCliente.conTipo(mensaje);
            mensaje=serializaMensaje(mensajeEnviar);
            out.writeUTF(mensaje);
        }
        else if(argumentoMensaje[0].equals("STATUS")){
            Mensajes mensajeEnviar = MensajesServidorCliente.conTipoEstado(mensaje);
            mensaje=serializaMensaje(mensajeEnviar);
            out.writeUTF(mensaje);
        }
        else if(argumentoMensaje[0].equals("MESSAGE")){
            Mensajes mensajeEnviar = MensajesServidorCliente.conTipoUsuarioMensaje(mensaje);
            mensaje=serializaMensaje(mensajeEnviar);
            out.writeUTF(mensaje);
        }
        else if(argumentoMensaje[0].equals("PUBLIC_MESSAGE")){
            Mensajes mensajeEnviar = MensajesServidorCliente.conTipoMensaje(mensaje);
            mensaje=serializaMensaje(mensajeEnviar);
            out.writeUTF(mensaje);
        }
        else if(argumentoMensaje[0].equals("NEW_ROOM") 
                || argumentoMensaje[0].equals("JOIN_ROOM")
                || argumentoMensaje[0].equals("ROOM_USERS")
                || argumentoMensaje[0].equals("LEAVE_ROOM")){
            Mensajes mensajeEnviar = MensajesServidorCliente.conTipoNombreCuarto(mensaje);
            mensaje = serializaMensaje(mensajeEnviar);
            out.writeUTF(mensaje);
            
        }
        else if(argumentoMensaje[0].equals("INVITE")){
            Mensajes mensajeEnviar = MensajesServidorCliente.conTipoNombreCuartoUsuarios(mensaje);
            mensaje=serializaMensaje(mensajeEnviar);
            out.writeUTF(mensaje);
        }
        else if(argumentoMensaje[0].equals("ROOM_MESSAGE")){
            Mensajes mensajeEnviar = MensajesServidorCliente.conTipoNombreCuartoMensaje(mensaje);
            mensaje=serializaMensaje(mensajeEnviar);
            out.writeUTF(mensaje);
        }
            
    }
}
