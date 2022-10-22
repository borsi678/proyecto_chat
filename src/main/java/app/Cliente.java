package app;

import java.io.IOException;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {

    private static Usuario usuario;
    private ProcesadorCliente procesador;
    private Scanner scanner;
    private static boolean conexion;

    public Cliente() {
        this.usuario=new Usuario("");
        this.scanner= new Scanner(System.in);
        Cliente.conexion=false;
    }

    public void iniciaCliente() {
        Scanner scanner = new Scanner(System.in);
        String direccionIP="";
        int puerto=0;
        try{
            System.out.println("Introduce la direccion IP del servidor");
            direccionIP = scanner.nextLine();
            System.out.println("Introduce puerto del servidor");
            puerto = scanner.nextInt();
        }catch(InputMismatchException ex){
            System.out.println("Introduce un puerto valido");
            iniciaCliente();
            return;
            }
        estableceConexionServidor(direccionIP, puerto);
        if(!Cliente.getConexion()){
            iniciaCliente();
            return;
        }
        System.out.println("¡Bienvenido al chat!");
        escrituraMensajesUsuario();
        iniciaCliente();
    }
    public void estableceConexionServidor(String direccionIP, int puerto){
        try {
            Socket socket=new Socket(direccionIP, puerto);
            procesador=new ProcesadorCliente(this, socket);
            procesador.iniciaConexion();
            procesador.start();
        } catch (IOException ex) {
            System.out.println(ex.toString());
            iniciaCliente();
        }
        
    }
    
    public void escrituraMensajesUsuario(){
        System.out.print("Escribe operacion:");
        String mensaje="";
        while(Cliente.getConexion()){
                mensaje=scanner.nextLine();
                if(mensaje.toUpperCase().equals("DISCONNECT")){
                    procesador.menuMensajes(mensaje);
                    Cliente.cambiaEstadoConexion(false);
                    return;
                }

                if(Cliente.getConexion()==false)
                    return;
                if(mensaje.equals("") || mensaje == null)
                    continue;
                procesador.menuMensajes(mensaje);
        }
    }
    
    public static void setNombreUsuario(String nombre){
        usuario.setNombre(nombre);
    }
    
    public static String getNombreUsuario(){
        return usuario.getNombre();  
    }
    
    public void imprimeMensaje(String mensaje){
        System.out.println(mensaje);
    }
    
    public void cambiaEstadoUsuario(EstadoUsuario estado ){
        usuario.setEstado(estado);
    }
    public static void cambiaEstadoConexion(boolean estado){
        conexion=estado;
    }
    
    public static boolean getConexion(){
        return Cliente.conexion;
    }
}
