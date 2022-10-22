package app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class Servidor {

    private LinkedList<Cuarto> listaCuartos;
    private HashMap<Usuario,Socket> diccSocketsUsuarios;

    public Servidor() {
        this.listaCuartos = new LinkedList<Cuarto>();
        this.listaCuartos.add(new Cuarto("General"));
        this.diccSocketsUsuarios = new HashMap<Usuario,Socket>();
    }

    public void iniciaServidor() {
        try {
            ServerSocket socketServidor = new ServerSocket(8999);
            System.out.println("Iniciando servidor...");
            estableceConexionCliente(socketServidor);
        } catch (IOException ex) {
            System.out.println("No se pudo iniciar el servidor.\n Saliendo...");
        }
    }

    public void estableceConexionCliente(ServerSocket socketServidor) {
        Socket socketCliente;
        while (true) {
            try {
                socketCliente = socketServidor.accept();

                ProcesadorServidor procesadorServidor = new ProcesadorServidor(this, socketCliente);
                procesadorServidor.start();

            }
            catch (IOException ex) {
                System.out.println("No se pudo establecer conexion con el cliente\n Reintentando...");
                estableceConexionCliente(socketServidor);
            }
        }
    }


    public void agregaCuarto(Cuarto cuarto) {
        listaCuartos.add(cuarto);
    }

    public void agregaSocketCliente(Usuario usuario,Socket socketCliente) {
        this.diccSocketsUsuarios.put(usuario, socketCliente);
    }

    public boolean contieneUsuario(Usuario usuario) {
        for (Map.Entry<Usuario,Socket> entrada : diccSocketsUsuarios.entrySet()) {
            if(entrada.getKey().equals(usuario))
                return true;
        }
        return false;
    }
    
    public boolean contieneUsuario(String nombreUsuario) {
        for (Map.Entry<Usuario,Socket> entrada : diccSocketsUsuarios.entrySet()) {
            if(entrada.getKey().getNombre().equals(nombreUsuario))
                return true;
        }
        return false;
    }

    public void transmiteMensajeClientes(String mensaje) throws IOException {
        DataOutputStream salida;
        Socket clienteConectado;
        for (Map.Entry<Usuario,Socket> entrada : diccSocketsUsuarios.entrySet()) {
            clienteConectado=entrada.getValue();
            salida = new DataOutputStream(clienteConectado.getOutputStream());
            salida.writeUTF(mensaje);
        }
    }
    
    public void transmiteMensajeClientes(String mensaje, String nombreUsuario) throws IOException {
        DataOutputStream salida;
        Socket clienteConectado;
        Usuario usuarioConectado;
        for (Map.Entry<Usuario,Socket> entrada : diccSocketsUsuarios.entrySet()) {
            usuarioConectado=entrada.getKey();
            if(usuarioConectado.getNombre().equals(nombreUsuario))
                continue;
            clienteConectado=entrada.getValue();
            salida = new DataOutputStream(clienteConectado.getOutputStream());
            salida.writeUTF(mensaje);
        }
    }
    
    public void transmiteMensajeACuarto(String mensaje, String nombreCuarto) throws IOException{
        DataOutputStream salida;
        Socket clienteConectado;
        for(Cuarto cuarto : listaCuartos){
            if(cuarto.getNombre().equals(nombreCuarto)){
                LinkedList<Usuario> usuariosUnidos=cuarto.getUsuariosUnidos();
                for(Usuario usuarioConectado : usuariosUnidos){
                    transmiteMensajePrivado(mensaje, usuarioConectado);
                }
                return;
            }
        }
    }

    public void transmiteMensajePrivado(String mensaje, Usuario usuario) throws IOException{
        Socket clienteConectado = diccSocketsUsuarios.get(usuario);
        try{

            DataOutputStream salida=new DataOutputStream(clienteConectado.getOutputStream());
            salida.writeUTF(mensaje);
        }catch(SocketException ex){
            diccSocketsUsuarios.remove(usuario, clienteConectado);
            
        }
    }
    
    public void cambiaEstadoUsuario(String nombreUsuario, EstadoUsuario estado){
        Usuario usuario;
        for (Map.Entry<Usuario,Socket> entrada : diccSocketsUsuarios.entrySet()) {
            usuario=entrada.getKey();
            if(usuario.getNombre().equals(nombreUsuario))
                usuario.setEstado(estado);
        }
    }
    
    public Usuario getUsuario(Socket socketCliente){
        for (Map.Entry<Usuario,Socket> entrada : diccSocketsUsuarios.entrySet()) {
            if(entrada.getValue().equals(socketCliente))
                return entrada.getKey();
        }
        return null;
    }
    
    public Usuario getUsuario(String nombreUsuario){
        for (Map.Entry<Usuario,Socket> entrada : diccSocketsUsuarios.entrySet()) {
            if(entrada.getKey().getNombre().equals(nombreUsuario))
                return entrada.getKey();
        }
        return null;
    }
    
    public Socket getSocketCliente(Usuario usuario){
        return diccSocketsUsuarios.get(usuario);
    }
    
    public String[] getListaUsuariosConectados(){
        String[] lista=new String[diccSocketsUsuarios.size()];
        int contador=0;
        for (Map.Entry<Usuario,Socket> entrada : diccSocketsUsuarios.entrySet()) {
            lista[contador++]=entrada.getKey().getNombre();
        }
        return lista;
    }
    
    public void creaCuarto(String nombreCuarto, Usuario usuarioCreador){
        Cuarto cuarto= new Cuarto(nombreCuarto, usuarioCreador);
        listaCuartos.add(cuarto);
    }
    
    public boolean contieneCuarto(String nombreCuartor){
        for(Cuarto cuarto : listaCuartos){
            if(cuarto.getNombre().equals(nombreCuartor))
                return true;
        }
        return false;
    }
    
    public void agregaUsuarioInvitadoACuarto(Usuario usuario, String nombreCuarto){
        for(Cuarto cuarto : listaCuartos){
            if(cuarto.getNombre().equals(nombreCuarto)){
                cuarto.agregaUsuarioInvitado(usuario);
            }
        }
    }
    
    public void agregaUsuarioUnidoACuarto(Usuario usuario, String nombreCuarto){
        for(Cuarto cuarto : listaCuartos){
            if(cuarto.getNombre().equals(nombreCuarto)){
                cuarto.agregaUsuarioUnido(usuario);
            }
        }
    }
    
    public void eliminaUsuarioInvitadoACuarto(Usuario usuario, String nombreCuarto){
        for(Cuarto cuarto : listaCuartos){
            if(cuarto.getNombre().equals(nombreCuarto)){
                cuarto.eliminaUsuarioInvitado(usuario);
            }
        }
    }
    
    public void eliminaUsuarioUnidoACuarto(Usuario usuario, String nombreCuarto){
        for(Cuarto cuarto : listaCuartos){
            if(cuarto.getNombre().equals(nombreCuarto)){
                cuarto.eliminaUsuarioUnido(usuario);
            }
        }
    }
    
    public boolean estaUsuarioInvitadoACuarto(Usuario usuario, String nombreCuarto){
        for(Cuarto cuarto : listaCuartos){
            if(cuarto.getNombre().equals(nombreCuarto)){
                return cuarto.estaUsuarioInvitado(usuario);
            }
        }
        return false;
    }
    
    public boolean estaUsuarioUnidoACuarto(Usuario usuario, String nombreCuarto){
        for(Cuarto cuarto : listaCuartos){
            if(cuarto.getNombre().equals(nombreCuarto)){
                return cuarto.estaUsuarioUnido(usuario);
            }
        }
        return false;
    }
    
    public LinkedList<Usuario> getListaUsuariosCuarto(String nombreCuarto){
        for(Cuarto cuarto : listaCuartos){
            if(cuarto.getNombre().equals(nombreCuarto)){
                return cuarto.getUsuariosUnidos();
            }
        }
        return null;
    }
    
    public LinkedList<Cuarto> getListaCuartosServidor(){
        return listaCuartos;
    }

    public void eliminaConexion(Usuario usuario, Socket socketCliente){
        this.diccSocketsUsuarios.remove(usuario, socketCliente);
    }

    public void eliminaUsuarioCuartos(Usuario usuario){
        for(Cuarto cuarto : listaCuartos){
            if(cuarto.estaUsuarioInvitado(usuario))
                cuarto.eliminaUsuarioInvitado(usuario);
            if(cuarto.estaUsuarioUnido(usuario)) 
                cuarto.eliminaUsuarioUnido(usuario);
        }
    }
}
