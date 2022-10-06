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
import javax.imageio.IIOException;

public class ProcesadorCliente extends Procesador{
    
    private Cliente cliente;
    private Socket socket;
    
    public ProcesadorCliente(){
        this.cliente=null;
        this.entrada=null;
        this.salida=null;
        this.socket=null;
    }
    
    public ProcesadorCliente(Cliente cliente, Socket socket) throws IOException, ExcepcionMensajeInvalido{
        this.cliente=cliente;
        this.socket=socket;
        this.entrada=new DataInputStream(socket.getInputStream());
        this.salida=new DataOutputStream(socket.getOutputStream());
    }
    
    public void iniciaConexion() throws IOException{
        try {
            String mensajeServidor=entrada.readUTF();
            Mensajes mensaje=deserializaMensaje(mensajeServidor);
            imprimeMensajeCliente(mensaje);
            Scanner scanner = new Scanner(System.in);
            String nombre=scanner.nextLine();
            Mensajes mensajeCliente=MensajesServidorCliente.conTipoUsuario(String.format("IDENTIFY %s", nombre));
            String serializada = serializaMensaje(mensajeCliente);
            salida.writeUTF(serializada);
            mensajeServidor=entrada.readUTF();
            mensaje=deserializaMensaje(mensajeServidor);
            if(mensaje.getTipo().equals("INFO") 
                    && mensaje.getOperacion().equals("IDENTIFY")){
                imprimeMensajeCliente(mensaje);
                return;
            }
            else if(mensaje.getTipo().equals("WARNING") 
                    && mensaje.getOperacion().equals("IDENTIFY") && mensaje.getNombreUsuario().equals(nombre)){
                this.cliente.imprimeMensaje(mensaje.getTipo()+" "+mensaje.getMensaje());
                this.cliente.imprimeMensaje("Intentalo de nuevo");
                iniciaConexion();
            }else
                throw new IOException();
        } catch(NullPointerException ex){
            throw new ExcepcionMensajeInvalido("Mensaje Invalido");
        } catch (IOException ex) {
            throw new IOException("No se pudo realizar la conexion");
        }
    }

    @Override public void run(){
        String mensajeServidor="";
        Mensajes mensaje;
        while(true){
            try {
                mensajeServidor=entrada.readUTF();
                mensaje=deserializaMensaje(mensajeServidor);
            } catch (IOException ex) {
                Logger.getLogger(ProcesadorCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void imprimeMensajeCliente(Mensajes mensaje){
        String mensajeCliente="";
        if(mensaje.getTipo() != null )
            mensajeCliente+=mensaje.getTipo().toUpperCase()+" ";
        if(mensaje.getNombreCuarto()!= null)
            mensajeCliente+=mensaje.getNombreCuarto()+" ";
        if(mensaje.getNombreUsuario() != null)
            mensajeCliente+=mensaje.getNombreUsuario()+" ";
        if(mensaje.getMensaje() != null)
            mensajeCliente+=mensaje.getMensaje()+" ";
        if(mensaje.getNombresUsuarios()!= null)
            mensajeCliente+=mensaje.getNombresUsuarios().toString()+" ";
        this.cliente.imprimeMensaje(mensajeCliente);
    }
    
    public void enviaMensajeServidor(Mensajes mensaje){
        try {
            String mensajeSerializado=serializaMensaje(mensaje);
        } catch (JsonProcessingException ex) {
            System.out.println("No se pudo enviar el mensaje");
        }
        
    }
    
    public void menuMensajes(String mensaje) throws JsonProcessingException, IOException{
        String[] argumentoMensaje=mensaje.split(" ");
        if(argumentoMensaje[0].equals("USERS") ){
            Mensajes mensajeEnviar = MensajesServidorCliente.conTipo(mensaje);
            mensaje=serializaMensaje(mensajeEnviar);
            salida.writeUTF(mensaje);
        }
        else if(argumentoMensaje[0].equals("STATUS")){
            Mensajes mensajeEnviar = MensajesServidorCliente.conTipoEstado(mensaje);
            mensaje=serializaMensaje(mensajeEnviar);
            salida.writeUTF(mensaje);
        }
        else if(argumentoMensaje[0].equals("MESSAGE")){
            Mensajes mensajeEnviar = MensajesServidorCliente.conTipoUsuarioMensaje(mensaje);
            mensaje=serializaMensaje(mensajeEnviar);
            salida.writeUTF(mensaje);
        }
        else if(argumentoMensaje[0].equals("PUBLIC_MESSAGE")){
            Mensajes mensajeEnviar = MensajesServidorCliente.conTipoMensaje(mensaje);
            mensaje=serializaMensaje(mensajeEnviar);
            salida.writeUTF(mensaje);
        }
        else if(argumentoMensaje[0].equals("NEW_ROOM") 
                || argumentoMensaje[0].equals("JOIN_ROOM")
                || argumentoMensaje[0].equals("ROOM_USERS")
                || argumentoMensaje[0].equals("LEAVE_ROOM")){
            Mensajes mensajeEnviar = MensajesServidorCliente.conTipoNombreCuarto(mensaje);
            mensaje = serializaMensaje(mensajeEnviar);
            salida.writeUTF(mensaje);
            
        }
        else if(argumentoMensaje[0].equals("INVITE")){
            Mensajes mensajeEnviar = MensajesServidorCliente.conTipoNombreCuartoUsuarios(mensaje);
            mensaje=serializaMensaje(mensajeEnviar);
            salida.writeUTF(mensaje);
        }
        else if(argumentoMensaje[0].equals("ROOM_MESSAGE")){
            Mensajes mensajeEnviar = MensajesServidorCliente.conTipoNombreCuartoMensaje(mensaje);
            mensaje=serializaMensaje(mensajeEnviar);
            salida.writeUTF(mensaje);
        } else if(argumentoMensaje[0].equals("DISCONNECT")){
            
        }
            
    }
}
