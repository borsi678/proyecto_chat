package app;

import java.io.IOException;

public class ExcepcionSerializa extends IOException{
    
    public ExcepcionSerializa(){}
    
    public ExcepcionSerializa(String mensaje){
        super(mensaje);
    }
}
