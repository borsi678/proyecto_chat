
package AppTest;

import app.Mensajes;
import app.Procesador;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author angel
 */
public class ProcesadorTest {
    
    Mensajes mensaje;
    Procesador procesador;

    public ProcesadorTest() {
    }
     /**
     * Test of serializaMensaje method, of class Procesador.
     */
    @Test
    public void testSerializaMensaje() {
        ConstructorMensajes mensajeC = new ConstructorMensajes();
        procesador= new Procesador();
        Mensaje mensaje=mensajeC.conTipo("IDENIFY")
                                .conNombreUsuario("Angel")
                                .construyeMensaje();
        String mensajePrueba="{\"tipo\": \"IDENTIFY\", \"nombreUsuario\": \"Angel\"}";
        String mensajeComparar= procesador.serializaMensaje(); 
        assertEquals(mensajePrueba, mensajeComparar);                       

    }

    /**
     * Test of deserializaMensaje method, of class Procesador.
     */
    @Test
    public void testDeserializaMensaje() {
        String mensajePrueba="{\"tipo\": \"IDENTIFY\", \"nombreUsuario\": \"Angel\"}";
        ConstructorMensajes mensajeC = new ConstructorMensajes();
        procesador= new Procesador();
        Mensaje mensaje=mensajeC.conTipo("IDENIFY")
                                .conNombreUsuario("Angel")
                                .construyeMensaje();
        Mensaje mensajeComparar = procesador.deserializaMensaje();
        assertEquals(mensaje, mensajeComparar);            
    }


    public class ProcesadorImpl extends Procesador {
    }
    
}
