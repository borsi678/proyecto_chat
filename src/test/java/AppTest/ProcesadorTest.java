
package AppTest;

import app.Mensajes;
import app.Procesador;
import app.ConstructorMensajes;
import app.ProcesadorCliente;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProcesadorTest {
    
    Mensajes mensaje;
    Procesador procesador;

    public ProcesadorTest() {
    }
     /**
     * Test of serializaMensaje method, of class Procesador.
     */
    @Test
    public void testSerializaMensaje() throws JsonProcessingException  {
        ConstructorMensajes mensajeC = new ConstructorMensajes();
        procesador= new ProcesadorCliente();
        String[] nombres={"Uno", "Dos", "tres"};
        Mensajes mensaje=mensajeC.conTipo(" IDENTIFY")
                                .conNombreUsuario(" Angel")
                                .conNombresUsuarios(nombres)
                                .construyeMensaje();
        String mensajePrueba="{\"type\": \"IDENTIFY\", \"username\": \"Angel\"}";
        String mensajeComparar= procesador.serializaMensaje(mensaje); 
//        assertEquals(mensajePrueba, mensajeComparar);                       

    }

    /**
     * Test of deserializaMensaje method, of class Procesador.
     */
    @Test
    public void testDeserializaMensaje() throws JsonProcessingException  {
        String mensajePrueba="{\"type\": \"IDENTIFY\", \"username\": \"Angel\"}";
        ConstructorMensajes mensajeC = new ConstructorMensajes();
        procesador= new ProcesadorCliente();
        Mensajes mensaje=mensajeC.conTipo("IDENTIFY")
                                .conNombreUsuario("Angel")
                                .construyeMensaje();
        Mensajes mensajeComparar = procesador.deserializaMensaje(mensajePrueba);
//        assertEquals(mensaje, mensajeComparar);            
    }


    public class ProcesadorImpl extends Procesador {
    }
    
}
