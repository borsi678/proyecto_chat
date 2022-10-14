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
    private String tipo;
    private String mensaje;
    private String operacion;
    private String nombreUsuario;
    private String nombreCuarto;
    private String estado;
    private String[] nombresUsuarios;

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
    public String getNombreUsuario(){
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
    public String getNombreCuarto(){
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
    
    public String usuariosToString(){
        String arregloCadena= "[";
        if(nombresUsuarios == null || nombresUsuarios.length==0){
            return "[]";
        }
        for(int i=0; i<nombresUsuarios.length; i++){
            if(i==nombresUsuarios.length-1){
                arregloCadena+=String.format("%s]", nombresUsuarios[i]);
                continue;
            }
            arregloCadena+=String.format("%s, ",nombresUsuarios[i]);
        }
        return arregloCadena;
    }
    
    @Override
    public String toString(){
        return String.format("TIpo : %s,Operacion: %s, Usuario: %s , Nombre Sala : %s, Usuarios: %s Estado: %s, Mensaje: %s", 
                this.tipo, this.operacion, this.nombreUsuario, this.nombreCuarto, usuariosToString() , this.estado, this.mensaje );
    }
    public boolean estadoValido() throws ExcepcionMensajeInvalido{
        if(this.tipo == null || this.tipo.equals(""))
            throw new ExcepcionMensajeInvalido("Mensaje no valido");
        return true;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj==null)
            return false;
        if(this==obj )
            return true;
        if(!(obj instanceof Mensajes))
            return false;
        
        Mensajes mensajeComparar=(Mensajes) obj;
        if((this.tipo== null && mensajeComparar.tipo!=null) 
                || (this.tipo!= null && mensajeComparar.tipo==null))
            return false;
        if(this.tipo != null && mensajeComparar.tipo != null){
           if(!(this.tipo.equals(mensajeComparar.tipo)))
               return false;
        }
        
        if((this.mensaje== null && mensajeComparar.mensaje!=null)
                || (this.mensaje!= null && mensajeComparar.mensaje==null))
            return false;
        if(this.mensaje != null && mensajeComparar.mensaje != null ){
                   if(!(this.mensaje.equals(mensajeComparar.mensaje)))
                       return false;
        }
        
        if((this.operacion== null && mensajeComparar.operacion!=null) 
                || (this.operacion!= null && mensajeComparar.operacion==null))
            return false;
        if(this.operacion != null && mensajeComparar.operacion != null){
           if(!(this.operacion.equals(mensajeComparar.operacion)))
               return false;
        }
        
        if((this.estado== null && mensajeComparar.estado!=null)
                || (this.estado!= null && mensajeComparar.estado==null))
            return false;
        if(this.estado != null && mensajeComparar.estado != null){
           if(!(this.estado.equals(mensajeComparar.estado)))
               return false;
        }
        
        if((this.nombreUsuario== null && mensajeComparar.nombreUsuario!=null) 
                || (this.nombreUsuario!= null && mensajeComparar.nombreUsuario==null))
            return false;
        if(this.nombreUsuario != null && mensajeComparar.nombreUsuario != null){
           if(!(this.nombreUsuario.equals(mensajeComparar.nombreUsuario)))
               return false;
        }
        
        if((this.nombresUsuarios== null && mensajeComparar.nombresUsuarios!=null)
                || (this.nombresUsuarios!= null && mensajeComparar.nombresUsuarios==null))
            return false;
        if(this.nombresUsuarios != null && mensajeComparar.nombresUsuarios != null){
           if(!(this.usuariosToString().equals(mensajeComparar.usuariosToString())))
               return false;
        }
        
        if((this.nombreCuarto== null && mensajeComparar.nombreCuarto!=null) 
                || (this.nombreCuarto!= null && mensajeComparar.nombreCuarto==null))
            return false;
        if(this.nombreCuarto != null && mensajeComparar.nombreCuarto != null){
           if(!(this.nombreCuarto.equals(mensajeComparar.nombreCuarto)))
               return false;
        }
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
