
package app;

public class ExcepcionEstadoInvalido extends IllegalArgumentException{
    
    public ExcepcionEstadoInvalido(){}
    
    public ExcepcionEstadoInvalido(String mensaje){
        super(mensaje);
    }
}
