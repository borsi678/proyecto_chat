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
    
    public String serializaMensaje(Mensajes mensaje) throws JsonProcessingException{
        ObjectMapper mapeador = new ObjectMapper();
        return mapeador.writeValueAsString(mensaje);
    }
    
    public Mensajes deserializaMensaje(String mensaje) throws JsonProcessingException{
        String[] contenidoMensaje={"type", "message", "operation", "username", "roomname", "status", 
            "usernames"};
        for(String elemento : contenidoMensaje){
            if(!(mensaje.contains(elemento))){
                mensaje+=String.format("%s: \"\"",elemento);
            }
        }
        return new ObjectMapper().readValue(mensaje, Mensajes.class);
    }
    
    public void menuMensajes(String mensaje) throws IOException {}
}
