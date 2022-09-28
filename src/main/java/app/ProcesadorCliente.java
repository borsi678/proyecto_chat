package app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcesadorCliente extends Procesador{
    
    private Cliente cliente;
    private Scanner scanner;
    private Socket socket;
    
    public ProcesadorCliente(Cliente cliente){
        this.cliente=cliente;
        this.in=null;
        this.out=null;
        this.socket=null;
        this.scanner=new Scanner(System.in);
    }
    
    public void iniciaConexion(String direccionIP, int puerto){
        try {
            socket=new Socket(direccionIP, puerto);
            in= new DataInputStream(socket.getInputStream());
            out= new DataOutputStream(socket.getOutputStream());
            System.out.println(in.readUTF());
            
            String jsonNombre="{ \"type\": \"IDENTIFY\"," + "  \"username\": \"Kimberly\" }";
            
            out.writeUTF(jsonNombre);
            
            System.out.println(in.readUTF());
            this.start();
            this.run();
            
            
        } catch (IOException ex) {
            Logger.getLogger(ProcesadorCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override public void run(){
        boolean salirCiclo=false;
        String mensaje=null;
        while(!salirCiclo){
            scanner.nextLine();
            if(mensaje.equals("") ||mensaje == null)
                salirCiclo=true;
            
        }
    }
}
