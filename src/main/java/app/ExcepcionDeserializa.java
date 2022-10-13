package app;

import java.io.IOException;

public class ExcepcionDeserializa extends IOException{
    
    public ExcepcionDeserializa(){}
    
    public ExcepcionDeserializa(String mensaje){
        super(mensaje);
    }
}
