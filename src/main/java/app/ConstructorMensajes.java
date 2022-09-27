package app;

public class ConstructorMensajes {
    Mensajes mensaje;
    
    public ConstructorMensajes(){
        this.mensaje=new Mensajes();
    }
    
    public ConstructorMensajes conTipo(String tipo){
        this.mensaje.setTipo(tipo);
        return this;
    }
    
    public ConstructorMensajes conMensaje(String mensaje){
        this.mensaje.setMensaje(mensaje);
        return this;
    }

    public ConstructorMensajes conOperacion(String operacion){
        this.mensaje.setOperacion(operacion);
        return this;
    }

    public ConstructorMensajes conNombreUsuario(String nombreUsuario){
        this.mensaje.setNombreUsuario(nombreUsuario);
        return this;
    }
    
    public ConstructorMensajes conNosmbresUsuarios(String[] nombresUsuarios){
        this.mensaje.setNombresUsuarios(nombresUsuarios);
        return this;
    }

    public Mensajes construyeMensaje() throws Exception {
        if(!this.mensaje.estadoValido())
            throw new Exception("Estado de mensaje invalido");
        return this.mensaje;
    }
}
