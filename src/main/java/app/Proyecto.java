package app;

public class Proyecto {
    public static void main(String[] args) {
        if(args[0].equals("-s"))
            servidor();
        else if(args[0].equals("-c"))
            cliente();
        else
            uso();
    }
    
    public static void servidor(){
        Servidor servidor= new Servidor();
        servidor.iniciaServidor();
    }
    
    public static void cliente(){
        Cliente cliente= new Cliente();
        cliente.iniciaCliente();
    }
    
    public static void uso(){
        System.out.println("Usa proyecto -s para inciar el servidor ");
        System.out.println("Usa proyecto -c para iniciar el cliente");
    }
}
