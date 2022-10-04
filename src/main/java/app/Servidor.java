package app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class Servidor {

    private LinkedList<Usuario> listaUsuarios;
    private LinkedList<Cuarto> listaCuartos;
    private LinkedList<Socket> listaSockets;

    public Servidor() {
        this.listaUsuarios = new LinkedList<Usuario>();
        this.listaCuartos = new LinkedList<Cuarto>();
        this.listaCuartos.add(new Cuarto("General"));
        this.listaSockets= new LinkedList<Socket>();
    }

    public void iniciaServidor() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Introduce el puerto para el servidor");
            int puerto = scanner.nextInt();
            ServerSocket socketServidor = new ServerSocket(puerto);
            System.out.println("Iniciando servidor...");
            Socket socketCliente;
            while (true) {
                socketCliente = socketServidor.accept();
                
                DataInputStream entrada = new DataInputStream(socketCliente.getInputStream());
                DataOutputStream salida = new DataOutputStream(socketCliente.getOutputStream());

                salida.writeUTF("Identificate:");
                String nombreCliente = entrada.readUTF();
                System.out.println(nombreCliente);
                ProcesadorServidor procesadorServidor = new ProcesadorServidor(this, entrada, salida);
                procesadorServidor.procesaNuevaConexion(nombreCliente);
                procesadorServidor.start();

            }
        } catch (IOException ex) {
            System.out.println("No se pudo iniciar el servidor");
        }

    }

    public void agregaUsuario(Usuario usuario) {
        listaUsuarios.add(usuario);
    }
    
    public void agregaCuarto(Cuarto cuarto){
        listaCuartos.add(cuarto);
    }
    public boolean contieneUsuario(Usuario usuario){
        return listaUsuarios.contains(usuario);
    }
    
    public void transmiteMensajeClientes(String mensaje) throws IOException{
        DataOutputStream salida;
        for(Socket clienteConectado : listaSockets){
            salida = new DataOutputStream(clienteConectado.getOutputStream());
            salida.writeUTF(mensaje);
        }
    }
}
