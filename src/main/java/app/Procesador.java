package app;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

public abstract class Procesador {
    
    public Procesador(){}
    
    public String serializaMensaje(Mensajes mensaje) throws JsonProcessingException{
        ObjectMapper mapeador = new ObjectMapper();
//        SimpleModule modulo= new SimpleModule();
//        modulo.addSerializer(Mensajes.class, new MensajeSerializado());
//        mapeador.registerModule(modulo);
//        
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
}
