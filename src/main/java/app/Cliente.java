package app;

import java.util.Scanner;

public class Cliente {

    private Usuario usuario;

    public Cliente() {
        this.usuario=new Usuario("");
    }

    public void iniciaCliente() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce la direccion IP del servidor");
        String direccionIP = scanner.nextLine();
        System.out.println("Introduce puerto del servidor");
        int puerto = scanner.nextInt();
        ProcesadorCliente procesador = new ProcesadorCliente(this);
        procesador.iniciaConexion(direccionIP, puerto);
    }
    
    public void setNombreUsuario(String nombre){
        usuario.setNombre(nombre);
    }
}
