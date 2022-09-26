package app;

public class Mensajes {
    String tipo;
    String mensaje;
    String operacion;
    String nombreUsuario;
    String nombreCuarto;
    String[] nombresUsuarios;

    public Mensajes(){
        this.tipo="";
        this.mensaje="";
        this.operacion="";
        this.nombreUsuario="";
        this.nombresUsuarios=null;
    }

    public String getTipo(){
        return this.tipo;
    }
    public String getMensaje(){
        return this.mensaje;
    }
    public String getOperacion(){
        return this.operacion;
    }
    public String getNombresuario(){
        return this.nombreUsuario;
    }  
    public String[] getNombresUsuarios(){
        return this.nombresUsuarios;
    }

    public void setTipo(String tipo){
        this.tipo=tipo;
    }
    public void setMensaje(String mensaje){
        this.mensaje=mensaje;
    }
    public void setOperacion(String operacion){
        this.operacion=operacion;
    }
    public void setNombreUsuario(String nombreUsuario){
        this.nombreUsuario=nombreUsuario;
    }  
    public void setNombresUsuarios(String[] nombreUsuarios){
        this.nombresUsuarios=nombresUsuarios;
    } 
    
    public boolean estadoValido(){
        return true;
    }
}
