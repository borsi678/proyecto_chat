package app;

public class ExcepcionMensajeInvalido extends IllegalArgumentException{
    
    public ExcepcionMensajeInvalido(){}
    
    public ExcepcionMensajeInvalido(String mensaje){
        super(mensaje);
    }
    
}
