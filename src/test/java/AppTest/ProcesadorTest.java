
package AppTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import static org.junit.Assert.*;
import app.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;

public class ProcesadorTest {
    
    Mensajes mensaje;
    ProcesadorCliente procesador;

    public ProcesadorTest() {
        this.procesador=new ProcesadorCliente();
    }
     /**
     * Test of serializaMensaje method, of class Procesador.
     */
    @Test
    public void testSerializaMensaje() throws ExcepcionSerializa  {
        ConstructorMensajes mensajeC = new ConstructorMensajes();
        procesador= new ProcesadorCliente();
        String[] nombres={"Uno", "Dos", "Tres"};
        Mensajes mensaje=mensajeC.conTipo(" IDENTIFY")
                                .conNombreUsuario(" Angel")
                                .conNombresUsuarios(nombres)
                                .construyeMensaje();
        String mensajePrueba="{\"type\":\" IDENTIFY\",\"username\":\" Angel\",\"usernames\":[\"Uno\",\"Dos\",\"Tres\"]}";
        String mensajeComparar= procesador.serializaMensaje(mensaje); 
        assertEquals(mensajePrueba, mensajeComparar);                       

    }

    /**
     * Test of deserializaMensaje method, of class Procesador.
     */
    @Test
    public void testDeserializaMensaje() throws ExcepcionDeserializa  {
        String mensajePrueba="{\"type\":\"IDENTIFY\",\"username\":\"Angel\"}";
        ConstructorMensajes mensajeC = new ConstructorMensajes();
        procesador= new ProcesadorCliente();
        Mensajes mensaje=mensajeC.conTipo("IDENTIFY")
                                .conNombreUsuario("Angel")
                                .construyeMensaje();
        Mensajes mensajeComparar = procesador.deserializaMensaje(mensajePrueba);
        assertEquals(mensaje, mensajeComparar);            
        mensajePrueba="{\"type\":\"IDENTIFY\",\"username\":\"Angel\",\"usernames\":[ \"Uno\",\"Dos\",\"Tres\"]}";
        String[] usuarios={"Uno", "Dos", "Tres"};
        mensaje.setNombresUsuarios(usuarios);
        mensajeComparar=procesador.deserializaMensaje(mensajePrueba);
        assertEquals(mensaje, mensajeComparar);
        mensajePrueba="{\"type\":\"IDENTIFY\",\"username\":\"Angel\",\"usernames\":[ \"Uno\",\"Dos\",\"Tres\"],"+
                "\"status\":\"ACTIVE\"}";
        mensaje.setEstado("ACTIVE");
        mensajeComparar=procesador.deserializaMensaje(mensajePrueba);
        mensaje=MensajesServidorCliente.conTipoMensajeOperacion("STATUS success", TiposMensaje.INFO);
        try {
            String mensajeSerializado=procesador.serializaMensaje(mensaje);
            mensajeComparar=procesador.deserializaMensaje(mensajeSerializado);
            Assert.assertTrue(mensaje.equals(mensajeComparar));
        } catch (ExcepcionSerializa ex) {
            Assert.fail( );
        } catch(ExcepcionDeserializa ex){
            Assert.fail( );
        }
        
        
    }

    
}
