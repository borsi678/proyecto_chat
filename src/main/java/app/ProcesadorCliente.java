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
    
    
    @Override
    public void menuMensajes(String mensaje) throws JsonProcessingException, IOException{
        String[] argumentoMensaje=mensaje.split(" ");
        if(argumentoMensaje[0].equals("USERS") ){
            recibeListaUsuariosServidor(argumentoMensaje[0]);
        }
        else if(argumentoMensaje[0].equals("STATUS")){
            cambiaEstado(mensaje);
        }
        else if(argumentoMensaje[0].equals("MESSAGE")){
            enviaMensajePrivado(mensaje);
        }
        else if(argumentoMensaje[0].equals("PUBLIC_MESSAGE")){
            enviaMensajePublico(mensaje);
        }
        else if(argumentoMensaje[0].equals("NEW_ROOM")){
            creaNuevoCuarto(mensaje);
        }
        else if( argumentoMensaje[0].equals("JOIN_ROOM")){
            unirseCuarto(mensaje);
        }
        else if(argumentoMensaje[0].equals("ROOM_USERS")){
            recibeUsuariosCuarto(mensaje);
        }
        else if(argumentoMensaje[0].equals("LEAVE_ROOM")){
            abandonaCuarto(mensaje);
            
        }
        else if(argumentoMensaje[0].equals("INVITE")){
            invitaUsuariosCuarto(mensaje);
        }
        else if(argumentoMensaje[0].equals("ROOM_MESSAGE")){
            enviaMensajeCuarto(mensaje);
        } else if(argumentoMensaje[0].equals("DISCONNECT")){
            Mensajes mensajeCliente=MensajesServidorCliente.conTipo("DISCONNECT");
            salida.writeUTF(serializaMensaje(mensajeCliente));
        }
            
    }
    
    public void recibeListaUsuariosServidor(String tipo) throws JsonProcessingException, IOException{
        Mensajes mensajeCliente= MensajesServidorCliente.conTipo(tipo);
        salida.writeUTF(serializaMensaje(mensajeCliente));
        Mensajes mensajeServidor=deserializaMensaje(entrada.readUTF());
        if(mensajeServidor.getTipo().equals("USER_LIST") && mensajeServidor.getNombresUsuarios() != null){
            cliente.imprimeMensaje(mensajeServidor.getNombresUsuarios().toString());
        }
    }
    
    public void cambiaEstado(String mensaje) throws IOException, ExcepcionEstadoInvalido{
        Mensajes mensajeCliente=MensajesServidorCliente.conTipoEstado(mensaje);
        salida.writeUTF(serializaMensaje(mensajeCliente));
        Mensajes mensajeServidor=deserializaMensaje(entrada.readUTF());
        String estado=mensajeCliente.getEstado();
        if(mensajeServidor.getTipo().equals("INFO") && mensajeServidor.getOperacion().equals("STATUS")
                && mensajeServidor.getMensaje().equals("success")){
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
        throw new ExcepcionMensajeInvalido("Estado invalido");
    }
    
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
    
    public void enviaMensajePublico(String mensaje) throws JsonProcessingException, IOException{
        Mensajes mensajeServidor=MensajesServidorCliente.conTipoMensaje(mensaje);
        salida.writeUTF(serializaMensaje(mensajeServidor));
    }
    
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
