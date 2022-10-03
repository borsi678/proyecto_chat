package app;

import com.fasterxml.jackson.annotation.*;
import app.ExcepcionMensajeInvalido;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;

@JsonSerialize(include = Inclusion.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Mensajes {
    @JsonAlias({ "type", "message", "operation", "username", "roomname", "status", "usernames"})
    String tipo;
    String mensaje;
    String operacion;
    String nombreUsuario;
    String nombreCuarto;
    String estado;
    String[] nombresUsuarios;

    public Mensajes(){
        this.tipo=null;
        this.mensaje=null;
        this.operacion=null;
        this.nombreUsuario=null;
        this.nombresUsuarios=null;
        this.nombreCuarto=null;
        this.estado=null;
    }
    
    @JsonGetter("type")
    public String getTipo(){
        return this.tipo;
    }
    
    @JsonGetter("message")
    public String getMensaje(){
        return this.mensaje;
    }
    
    @JsonGetter("operation")
    public String getOperacion(){
        return this.operacion;
    }
    
    @JsonGetter("username")
    public String getNombresuario(){
        return this.nombreUsuario;
    } 
    
    @JsonGetter("usernames")
    public String[] getNombresUsuarios(){
        return this.nombresUsuarios;
    }
    
    @JsonGetter("status")
    public String getEstado(){
        return this.estado;
    }
    
    @JsonGetter("roomname")
    public String getnombreCuarto(){
        return this.nombreCuarto;
    }
 
    
    @JsonSetter("type")
    public void setTipo(String tipo){
        this.tipo=tipo;
    }
    
    @JsonSetter("message")
    public void setMensaje(String mensaje){
        this.mensaje=mensaje;
    }
    
    @JsonSetter("operation")
    public void setOperacion(String operacion){
        this.operacion=operacion;
    }
    
    @JsonSetter("username")
    public void setNombreUsuario(String nombreUsuario){
        this.nombreUsuario=nombreUsuario;
    } 
    
    @JsonSetter("usernames")
    public void setNombresUsuarios(String[] nombresUsuarios){
        this.nombresUsuarios=nombresUsuarios;
    } 
    
    @JsonSetter("status")
    public void setEstado(String estado){
        this.estado=estado;
    }
    
    @JsonSetter("roomname")
    public void setNombreCuarto(String nombreCuarto){
        this.nombreCuarto=nombreCuarto;
    }
    
    public boolean estadoValido() throws ExcepcionMensajeInvalido{
        if(this.tipo == null || this.tipo.equals(""))
            throw new ExcepcionMensajeInvalido("Mensaje no valido");
        return true;
    }
    
    public void vacia(){
        this.tipo=null;
        this.mensaje=null;
        this.operacion=null;
        this.nombreUsuario=null;
        this.nombresUsuarios=null;
        this.nombreCuarto=null;
        this.estado=null;
    }
}
