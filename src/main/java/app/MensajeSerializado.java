package app;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

public class MensajeSerializado extends StdSerializer<Mensajes>{
    
    public MensajeSerializado(){
        this(null);
    }
    
    public MensajeSerializado(Class<Mensajes> t){
        super(t);
    }

    @Override
    public void serialize(Mensajes mensaje, JsonGenerator jgen, SerializerProvider provider) 
            throws IOException, JsonProcessingException {
        
        
        jgen.writeStartObject();
        jgen.writeStringField("type: ", mensaje.getTipo());
        jgen.writeStringField("message: ", mensaje.getMensaje());
        jgen.writeStringField("username: ", mensaje.getNombreUsuario());
        jgen.writeStringField("roomname: ", mensaje.getNombreCuarto());
        jgen.writeStringField("usernames: ", mensaje.getNombresUsuarios().toString());
        jgen.writeStringField("state: ", mensaje.getEstado());
        jgen.writeStringField("operation: ", mensaje.getOperacion());
        jgen.writeEndObject();
    }
    
    
    
}
