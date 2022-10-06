/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package AppTest;

import app.ProcesadorCliente;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import app.ConstructorMensajes;
import app.Mensajes;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author angel
 */
public class ProcesadorClienteTest {
    
    ProcesadorCliente procesador;
    ConstructorMensajes constructor;
    
    public ProcesadorClienteTest() {
        procesador=new ProcesadorCliente();
        this.constructor= new ConstructorMensajes();
    }
    
    @Test
    public void testSerializa(){
        String serializada = "{\"type\":\"IDENTIFY\",\"username\":\"Angel\"}";
        Mensajes mensaje= constructor.conTipo("IDENTIFY")
                                                         .conNombreUsuario("Angel")
                                                         .construyeMensaje();
        try {
            String serializadaComparar= procesador.serializaMensaje(mensaje);
            assertEquals(serializada , serializadaComparar);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ProcesadorClienteTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Test
    public void testIniciaConexion() {
    }

    /**
     * Test of run method, of class ProcesadorCliente.
     */
    @Test
    public void testRun() {
    }

    /**
     * Test of menuMensajes method, of class ProcesadorCliente.
     */
    @Test
    public void testMenuMensajes() throws Exception {
    }

}
