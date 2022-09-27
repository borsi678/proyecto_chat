
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
    
    public ProcesadorTest() {
    }
        /**
     * Test of serializaMensaje method, of class Procesador.
     */
    @Test
    public void testSerializaMensaje() {
        System.out.println("serializaMensaje");
        Mensajes mensaje = null;
        Procesador instance = new ProcesadorImpl();
        String expResult = "";
        String result = instance.serializaMensaje(mensaje);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deserializaMensaje method, of class Procesador.
     */
    @Test
    public void testDeserializaMensaje() {
        System.out.println("deserializaMensaje");
        String mensaje = "";
        Procesador instance = new ProcesadorImpl();
        Mensajes expResult = null;
        Mensajes result = instance.deserializaMensaje(mensaje);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class ProcesadorImpl extends Procesador {
    }
    
}
