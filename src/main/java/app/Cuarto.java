package app;

import java.util.LinkedList;

public class Cuarto {
    private String nombre;
    private LinkedList<Usuario> usuariosUnidos;
    private LinkedList<Usuario> usuariosInvitados;
    private Usuario usuarioCreador;
    
    public Cuarto(String nombre){
        this.nombre =nombre;
        this.usuarioCreador = new Usuario("Servidor");        
        this.usuariosUnidos = new LinkedList<Usuario>();
        this.usuariosInvitados = new LinkedList<Usuario>();
    }

    public Cuarto(String nombre, Usuario usuarioCreador) {
        this.nombre = nombre;
        this.usuarioCreador = usuarioCreador;        
        this.usuariosUnidos = new LinkedList<Usuario>();
        this.usuariosInvitados = new LinkedList<Usuario>();
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LinkedList<Usuario> getUsuariosUnidos() {
        return usuariosUnidos;
    }

    public void setUsuariosUnidos(LinkedList<Usuario> usuariosUnidos) {
        this.usuariosUnidos = usuariosUnidos;
    }

    public LinkedList<Usuario> getUsuariosInvitados() {
        return usuariosInvitados;
    }

    public void setUsuariosInvitados(LinkedList<Usuario> usuariosInvitados) {
        this.usuariosInvitados = usuariosInvitados;
    }

    public Usuario getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(Usuario usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Cuarto)) {
            return false;
        }
        final Cuarto cuartoComparar = (Cuarto) obj;
        if (!(this.nombre.equals(cuartoComparar.nombre))) {
            return false;
        }
        if (!(this.usuarioCreador.equals(cuartoComparar.usuarioCreador))) {
            return false;
        }
        if (!(this.usuariosUnidos.equals(cuartoComparar.usuariosUnidos))) {
            return false;
        }       
        return this.usuariosInvitados.equals(cuartoComparar.usuariosInvitados);
    }
    
    public boolean estaUsuarioUnido(Usuario usuarioEncontrar){
        return usuariosUnidos.contains(usuarioEncontrar);
    }
    
    public boolean estaUsuarioInvitado(Usuario usuarioEncontrar){
        return usuariosInvitados.contains(usuarioEncontrar);
    }
    
    public void agregaUsuarioInvitado(Usuario invitado){
        this.usuariosInvitados.add(invitado);
    }
    
    public void agregaUsuarioUnido(Usuario invitado){
        this.usuariosUnidos.add(invitado);
    }
    
    public void eliminaUsuarioUnido(Usuario usuarioEliminado){
        this.usuariosUnidos.remove(usuarioEliminado);
    }
    
    public void eliminaUsuarioInvitado(Usuario usuarioEliminado){
        this.usuariosInvitados.remove(usuarioEliminado);
    }
    
    public  boolean esVacia(){
        return usuariosUnidos.isEmpty();
    }
    
    public int cuantosUsuariosHayUnidos(){
        return usuariosUnidos.size();
    }
    
    public int cuantosUsuariosHayInvitados(){
        return usuariosInvitados.size();
    }
}
