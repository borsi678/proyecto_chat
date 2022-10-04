package app;

public class Usuario {
    
    private String nombre;
    private EstadoUsuario estado;
    
    public Usuario(String nombre){
        this.nombre=nombre;
        this.estado=EstadoUsuario.ACTIVE;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public void setNombre(String nombre){
        this.nombre=nombre;
    }
    
    public EstadoUsuario getEstado(){
        return estado;
    }
    
    public void setEstado(EstadoUsuario estado){
        this.estado=estado;
    }
    
    @Override public String toString(){
        return "Nombre: "+nombre+" Estado: "+estado.toString();
    }
    
    @Override public boolean equals(Object objeto){
        if(objeto == null)
            return false;
        if(!(objeto instanceof Usuario))
            return false;
        Usuario usuarioComparar=(Usuario) objeto;
        if(this.nombre.equals(usuarioComparar.nombre) )
            return true;
        return false;
    }
}
