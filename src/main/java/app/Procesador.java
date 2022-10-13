package app;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

public abstract class Procesador extends Thread {
    
    protected DataInputStream entrada;
    protected DataOutputStream salida;
    
    public String serializaMensaje(Mensajes mensaje) throws ExcepcionSerializa{
        try{
            ObjectMapper mapeador = new ObjectMapper();
            return mapeador.writeValueAsString(mensaje);
        }catch(JsonProcessingException ex){
            throw new ExcepcionSerializa("Error al serializar mensaje: "+mensaje.toString());
        }
    }
    
    public Mensajes deserializaMensaje(String mensaje) throws ExcepcionDeserializa{
        String[] contenidoMensaje={"type", "message", "operation", "username", "roomname", "status", 
            "usernames"};
        for(String elemento : contenidoMensaje){
            if(!(mensaje.contains(elemento))){
                mensaje+=String.format("%s: \"\"",elemento);
            }
        }
        try{
            return new ObjectMapper().readValue(mensaje, Mensajes.class);
        }catch(JsonProcessingException ex){
            throw new ExcepcionDeserializa("Error al deserailizar mensaje "+mensaje);
        }
    }
    
    public void menuMensajes(String mensaje) throws IOException {}
    
    public void mensajesRecibidos(Mensajes mensaje){}
}
