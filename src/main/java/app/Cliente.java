package app;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {

    private Usuario usuario;
    private ProcesadorCliente procesador;
    private Scanner scanner;

    public Cliente() {
        this.usuario=new Usuario("");
        this.scanner= new Scanner(System.in);
    }

    public void iniciaCliente() {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Introduce la direccion IP del servidor");
//        String direccionIP = scanner.nextLine();
//        System.out.println("Introduce puerto del servidor");
//        int puerto = scanner.nextInt();
//        estableceConexionServidor(direccionIP, puerto);
        estableceConexionServidor("localhost", 8999);
        System.out.println("¡Bienvenido al chat!");
        escrituraMensajesUsuario();
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
        String mensaje="";
        while(!(mensaje.equals("DISCONNECT")) || !(mensaje.equals("^C"))){
            
                System.out.print("Escribe mensaje:");
                mensaje=scanner.nextLine();
                if(mensaje.equals("") || mensaje == null)
                    continue;
                procesador.menuMensajes(mensaje);
            
        }
        
            procesador.menuMensajes(mensaje);
            iniciaCliente();
        
    }
    
    public void setNombreUsuario(String nombre){
        usuario.setNombre(nombre);
    }
    
    public void imprimeMensaje(String mensaje){
        System.out.println(mensaje);
    }
    
    public void cambiaEstadoUsuario(EstadoUsuario estado ){
        usuario.setEstado(estado);
    }
}
