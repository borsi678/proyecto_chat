package app;

import com.alibaba.fastjson.JSON;

public abstract class Procesador {
    
    public String serializaMensaje(Mensajes mensaje){
        return JSON.toJSONString(mensaje);
    }
    
    public Mensajes deserializaMensaje(String mensaje){
        return JSON.parseObject(mensaje, Mensajes.class );
    }
}
