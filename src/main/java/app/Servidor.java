package app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

    private LinkedList<Usuario> listaUsuarios;
    private LinkedList<Cuarto> listaCuartos;
    private LinkedList<Socket> listaSockets;

    public Servidor() {
        this.listaUsuarios = new LinkedList<Usuario>();
        this.listaCuartos = new LinkedList<Cuarto>();
        this.listaCuartos.add(new Cuarto("General"));
        this.listaSockets = new LinkedList<Socket>();
    }

    public void iniciaServidor() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Introduce el puerto para el servidor");
            int puerto = scanner.nextInt();
            ServerSocket socketServidor = new ServerSocket(puerto);
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

            } catch (IOException ex) {
                System.out.println("No se pudo establecer conexion con el cliente\n Reintentando...");
                estableceConexionCliente(socketServidor);
            }
        }
    }


    public void agregaUsuario(Usuario usuario) {
        listaUsuarios.add(usuario);
    }

    public void agregaCuarto(Cuarto cuarto) {
        listaCuartos.add(cuarto);
    }

    public void agregaSocketCliente(Socket socketCliente) {
        this.listaSockets.add(socketCliente);
    }

    public boolean contieneUsuario(Usuario usuario) {
        for (Usuario usuarioServidor : listaUsuarios) {
            if (usuarioServidor.getNombre().equals(usuario.getNombre())) {
                return true;
            }
        }
        return false;
    }

    public void transmiteMensajeClientes(String mensaje) throws IOException {
        DataOutputStream salida;
        for (Socket clienteConectado : listaSockets) {
            salida = new DataOutputStream(clienteConectado.getOutputStream());
            salida.writeUTF(mensaje);
        }
    }
}
