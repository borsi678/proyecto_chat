package AppTest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import app.ConstructorMensajes;
import app.Mensajes;
import app.ProcesadorCliente;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import app.MensajesServidorCliente;

public class MensajesServidorClienteTest {

    public MensajesServidorClienteTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of conTIpoMensajeOperacionUsuario method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTIpoMensajeOperacionUsuario() {
    }

    /**
     * Test of conTipoUsuario method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoUsuario() {
        String mensajeEscrito = "IDENTIFY Angel";
        ConstructorMensajes constructor = new ConstructorMensajes();
        Mensajes mensaje = constructor.conTipo("IDENTIFY")
                .conNombreUsuario("Angel")
                .construyeMensaje();
        Mensajes mensajeComparar = MensajesServidorCliente.conTipoUsuario(mensajeEscrito);
        String v= mensajeComparar.toString();


    }

    /**
     * Test of conTipoMensajeOperacion method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoMensajeOperacion() {
    }

    /**
     * Test of conTipoEstado method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoEstado() {
    }

    /**
     * Test of conTipoUsuarioEstado method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoUsuarioEstado() {
    }

    /**
     * Test of conTipoMensajeOperacionEstado method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTipoMensajeOperacionEstado() {
    }

    /**
     * Test of conTipo method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipo() {
    }

    /**
     * Test of conTipoUsuarioMensaje method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoUsuarioMensaje() {
    }

    /**
     * Test of conTipoMensaje method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoMensaje() {
    }

    /**
     * Test of conTipoNombreCuarto method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoNombreCuarto() {
    }

    /**
     * Test of conTipoMensajeUsuarioNombreCuarto method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTipoMensajeUsuarioNombreCuarto() {
    }

    /**
     * Test of conTipoNombreCuartoUsuario method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTipoNombreCuartoUsuario() {
    }

    /**
     * Test of conTipoUsuarios method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoUsuarios() {
    }

    /**
     * Test of conTipoNombreCuartoMensaje method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTipoNombreCuartoMensaje() {
    }

    /**
     * Test of conTipoMensajeOperacionNombreCuarto method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTipoMensajeOperacionNombreCuarto() {
    }

    /**
     * Test of conTipoNombreCuartoUsuarios method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTipoNombreCuartoUsuarios() {
    }

    /**
     * Test of conTipoNombreCuartoUsuarioMensaje method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTipoNombreCuartoUsuarioMensaje() {
    }

    /**
     * Test of convertirCadenaAMensaje method, of class MensajesServidorCliente.
     */
    @Test
    public void testConvertirCadenaAMensaje() {
    }

}
