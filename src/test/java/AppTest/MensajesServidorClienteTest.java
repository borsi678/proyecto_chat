package AppTest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import app.ConstructorMensajes;
import app.EstadoUsuario;
import app.ExcepcionEstadoInvalido;
import app.ExcepcionMensajeInvalido;
import app.Mensajes;
import app.ProcesadorCliente;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import app.MensajesServidorCliente;
import app.TiposMensaje;
import org.junit.Assert;

public class MensajesServidorClienteTest {
    
    private Mensajes mensaje;
    private Mensajes mensajeComparar;
    private ConstructorMensajes constructor;

    public MensajesServidorClienteTest() {
        constructor=new ConstructorMensajes();
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
        constructor.vacia();
        String mensajeEscrito = "IDENTIFY Usuario";
        mensaje = constructor.conTipo("IDENTIFY")
                .conNombreUsuario("Usuario")
                .construyeMensaje();
        mensajeComparar = MensajesServidorCliente.conTipoUsuario(mensajeEscrito);
        assertEquals(mensaje, mensajeComparar);
        try{
            mensajeEscrito="L Angel";
            mensajeComparar=MensajesServidorCliente.conTipoUsuario(mensajeEscrito);
            Assert.fail();
        }catch(ExcepcionMensajeInvalido ex){}

    }

    /**
     * Test of conTipoMensajeOperacion method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoMensajeOperacion() {
        constructor.vacia();        
        String mensajeEscrito = "USER_LIST Mensaje";
        mensaje = constructor.conTipo("INFO")
                .conMensaje("Mensaje")
                .conOperacion("USER_LIST")
                .construyeMensaje();
        mensajeComparar = MensajesServidorCliente.conTipoMensajeOperacion(mensajeEscrito, TiposMensaje.INFO);
        assertEquals(mensaje, mensajeComparar);
        try{
            mensajeEscrito="L Mensaje";
            mensajeComparar=MensajesServidorCliente.conTipoMensajeOperacion(mensajeEscrito, TiposMensaje.INFO);
            Assert.fail();
        }catch(ExcepcionMensajeInvalido ex){}

    }

    /**
     * Test of conTipoEstado method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoEstado() {
        constructor.vacia();        
        String mensajeEscrito = "STATUS AWAY";
        mensaje = constructor.conTipo("STATUS")
                .conEstado("AWAY")
                .construyeMensaje();
        mensajeComparar = MensajesServidorCliente.conTipoEstado(mensajeEscrito);
        assertEquals(mensaje, mensajeComparar);
        try{
            mensajeEscrito="L AWAY";
            mensajeComparar=MensajesServidorCliente.conTipoEstado(mensajeEscrito);
            Assert.fail();
        }catch(ExcepcionMensajeInvalido ex){}
        try{
            mensajeEscrito="STATUS L";
            mensajeComparar=MensajesServidorCliente.conTipoEstado(mensajeEscrito);
            Assert.fail();
        }catch(ExcepcionEstadoInvalido ex){}        
    }

    /**
     * Test of conTipoUsuarioEstado method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoUsuarioEstado() {
        constructor.vacia();        
        String mensajeEscrito = "STATUS Usuario AWAY";
        mensaje = constructor.conTipo("STATUS")
                .conNombreUsuario("Usuario")
                .conEstado("AWAY")
                .construyeMensaje();
        mensajeComparar = MensajesServidorCliente.conTipoUsuarioEstado(mensajeEscrito);
        assertEquals(mensaje, mensajeComparar);
        try{
            mensajeEscrito="L Usuario AWAY";
            mensajeComparar=MensajesServidorCliente.conTipoUsuarioEstado(mensajeEscrito);
            Assert.fail();
        }catch(ExcepcionMensajeInvalido ex){}
        try{
            mensajeEscrito="STATUS Usuario L";
            mensajeComparar=MensajesServidorCliente.conTipoUsuarioEstado(mensajeEscrito);
            Assert.fail();
        }catch(ExcepcionEstadoInvalido ex){}  
    }

    /**
     * Test of conTipoMensajeOperacionEstado method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTipoMensajeOperacionEstado() {
        constructor.vacia();        
        String mensajeEscrito = "STATUS BUSY Mensaje";
        mensaje = constructor.conTipo("INFO")
                .conEstado("BUSY")
                .conMensaje("Mensaje")
                .conOperacion("STATUS")
                .construyeMensaje();
        mensajeComparar = MensajesServidorCliente.conTipoMensajeOperacionEstado(mensajeEscrito,
                TiposMensaje.INFO);
        assertEquals(mensaje, mensajeComparar);
        try{
            mensajeEscrito="L Busy Mensaje";
            mensajeComparar=MensajesServidorCliente.conTipoMensajeOperacionEstado(mensajeEscrito, TiposMensaje.INFO);
            Assert.fail();
        }catch(ExcepcionMensajeInvalido ex){}
        try{
            mensajeEscrito="STATUS M Usuario";
            mensajeComparar=MensajesServidorCliente.conTipoMensajeOperacionEstado(mensajeEscrito,
                    TiposMensaje.INFO);
            Assert.fail();
        }catch(ExcepcionEstadoInvalido ex){}  
    }

    /**
     * Test of conTipo method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipo() {
        for(TiposMensaje tipo : TiposMensaje.values()){
            constructor.vacia();
            if(tipo == TiposMensaje.INVALID )
                continue;
            String mensajeEscrito =String.format("%s", tipo.toString());
            mensaje = constructor.conTipo(tipo.toString())
                                                .construyeMensaje();
            mensajeComparar = MensajesServidorCliente.conTipo(mensajeEscrito);
            assertEquals(mensaje, mensajeComparar);
        }
        try{
            String mensajeEscrito="L";
            mensajeComparar=MensajesServidorCliente.conTipo(mensajeEscrito);
            Assert.fail();
        }catch(ExcepcionMensajeInvalido ex){}
    }

    /**
     * Test of conTipoUsuarioMensaje method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoUsuarioMensaje() {
        constructor.vacia();
        constructor.conMensaje("Mensaje")
                                           .conNombreUsuario("Usuario");
        for(TiposMensaje tipo : TiposMensaje.values()){
            if(tipo == TiposMensaje.INVALID )
                continue;

            String mensajeEscrito =String.format("%s Usuario Mensaje", tipo.toString());
            mensaje = constructor.conTipo(tipo.toString())
                                                .construyeMensaje();
            mensajeComparar = MensajesServidorCliente.conTipoUsuarioMensaje(mensajeEscrito);
            assertEquals(mensaje, mensajeComparar);
        }
        try{
            String mensajeEscrito="L Usuario Mensaje";
            mensajeComparar=MensajesServidorCliente.conTipoUsuarioMensaje(mensajeEscrito);
            Assert.fail();
        }catch(ExcepcionMensajeInvalido ex){}
    }

    /**
     * Test of conTipoMensaje method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoMensaje() {
        constructor.vacia();
        constructor.conMensaje("Mensaje Uno Dos Tres");
        for(TiposMensaje tipo : TiposMensaje.values()){
            if(tipo == TiposMensaje.INVALID )
                continue;

            String mensajeEscrito =String.format("%s Mensaje Uno Dos Tres", tipo.toString());
            mensaje = constructor.conTipo(tipo.toString())
                                                .construyeMensaje();
            mensajeComparar = MensajesServidorCliente.conTipoMensaje(mensajeEscrito);
            assertEquals(mensaje, mensajeComparar);
        }
    }

    /**
     * Test of conTipoNombreCuarto method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoNombreCuarto() {
        constructor.vacia();
        constructor.conNombreCuarto("Sala1");
        for(TiposMensaje tipo : TiposMensaje.values()){
            if(tipo == TiposMensaje.INVALID )
                continue;

            String mensajeEscrito =String.format("%s Sala1", tipo.toString());
            mensaje = constructor.conTipo(tipo.toString())
                                                .construyeMensaje();
            mensajeComparar = MensajesServidorCliente.conTipoNombreCuarto(mensajeEscrito);
            assertEquals(mensaje, mensajeComparar);
        }
    }

    /**
     * Test of conTipoMensajeUsuarioNombreCuarto method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTipoMensajeUsuarioNombreCuarto() {
        constructor.vacia();        
        String mensajeEscrito = "NEW_ROOM Usuario Sala1 Mensaje";
        mensaje = constructor.conTipo("NEW_ROOM")
                .conNombreUsuario("Usuario")
                .conNombreCuarto("Sala1")
                .conMensaje("Mensaje")
                .construyeMensaje();
        mensajeComparar = MensajesServidorCliente.conTipoMensajeUsuarioNombreCuarto(mensajeEscrito);
        assertEquals(mensaje, mensajeComparar);
        try{
            mensajeEscrito="L Usuario Sala1 Mensaje";
            mensajeComparar=MensajesServidorCliente.conTipoMensajeUsuarioNombreCuarto(mensajeEscrito);
            Assert.fail();
        }catch(ExcepcionMensajeInvalido ex){}
    }


    /**
     * Test of conTipoNombreCuartoUsuario method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTipoNombreCuartoUsuario() {
        constructor.vacia();
        constructor.conNombreCuarto("Sala1")
                          .conNombreUsuario("Usuario");
        for(TiposMensaje tipo : TiposMensaje.values()){
            if(tipo == TiposMensaje.INVALID)
                continue;
            String mensajeEscrito=String.format("%s Sala1 Usuario", tipo.toString());
            mensajeComparar=MensajesServidorCliente.conTipoNombreCuartoUsuario(mensajeEscrito);
            mensaje=constructor.conTipo(tipo.toString()).construyeMensaje();
            assertEquals(mensaje, mensajeComparar);                                      
        }
        try{
            String mensajeEscrito="L Sala1 Usuario";
            mensajeComparar=MensajesServidorCliente.conTipoNombreCuartoUsuario(mensajeEscrito);
            Assert.fail();
        }catch(ExcepcionMensajeInvalido ex){}
               
    }

    /**
     * Test of conTipoUsuarios method, of class MensajesServidorCliente.
     */
    @Test
    public void testConTipoUsuarios() {
        constructor.vacia();
        String[] usuarios={"1", "2", "3","4"};
        constructor.conNombresUsuarios(usuarios);
       for(TiposMensaje tipo : TiposMensaje.values()){
            if(tipo == TiposMensaje.INVALID)
                continue;
            String mensajeEscrito=String.format("%s", tipo.toString());
            mensajeComparar=MensajesServidorCliente.conTipoUsuarios(mensajeEscrito, usuarios);
            mensaje=constructor.conTipo(tipo.toString()).construyeMensaje();
            assertEquals(mensaje, mensajeComparar);                                      
        }
        try{
            mensajeComparar=MensajesServidorCliente.conTipoUsuarios("L", usuarios);
            Assert.fail();
        }catch(ExcepcionMensajeInvalido ex){}
    }

    /**
     * Test of conTipoNombreCuartoMensaje method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTipoNombreCuartoMensaje() {
        constructor.vacia();        
        String mensajeEscrito = "NEW_ROOM Sala1 Mensaje";
        mensaje = constructor.conTipo("NEW_ROOM")
                .conNombreCuarto("Sala1")
                .conMensaje("Mensaje")
                .construyeMensaje();
        mensajeComparar = MensajesServidorCliente.conTipoNombreCuartoMensaje(mensajeEscrito);
        assertEquals(mensaje, mensajeComparar);
        try{
            mensajeEscrito="L Sala1 Mensaje";
            mensajeComparar=MensajesServidorCliente.conTipoNombreCuartoMensaje(mensajeEscrito);
            Assert.fail();
        }catch(ExcepcionMensajeInvalido ex){}
    }

    /**
     * Test of conTipoMensajeOperacionNombreCuarto method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTipoMensajeOperacionNombreCuarto() {
        constructor.vacia();
        constructor.conNombreCuarto("Sala1").conMensaje("Mensaje");
       for(TiposMensaje tipo : TiposMensaje.values()){
            if(tipo == TiposMensaje.INVALID)
                continue;
            String mensajeEscrito=String.format("%s Sala1 Mensaje ", tipo.toString());
            mensajeComparar=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto(mensajeEscrito,
                    tipo);
            mensaje=constructor.conTipo(tipo.toString()).conOperacion(tipo.toString()).construyeMensaje();
            assertEquals(mensaje, mensajeComparar);                                      
        }
        try{
            mensajeComparar=MensajesServidorCliente.conTipoMensajeOperacionNombreCuarto("L Sala1 Mensaje", 
                    TiposMensaje.INFO);
            Assert.fail();
        }catch(ExcepcionMensajeInvalido ex){}
    }

    /**
     * Test of conTipoNombreCuartoUsuarios method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTipoNombreCuartoUsuarios() {
        constructor.vacia();
        String[] usuarios={"1","2","3","4","5"};
        constructor.conNombreCuarto("Sala1").conNombresUsuarios(usuarios);
       for(TiposMensaje tipo : TiposMensaje.values()){
            if(tipo == TiposMensaje.INVALID)
                continue;
            String mensajeEscrito=String.format("%s Sala1 1, 2, 3, 4, 5", tipo.toString());
            mensajeComparar=MensajesServidorCliente.conTipoNombreCuartoUsuarios(mensajeEscrito);
            mensaje=constructor.conTipo(tipo.toString()).construyeMensaje();
            Assert.assertTrue(mensaje.equals(mensajeComparar));                                      
        }
        try{
            mensajeComparar=MensajesServidorCliente.conTipoNombreCuartoUsuarios("L Sala1 1, 2, 3, 4");
            Assert.fail();
        }catch(ExcepcionMensajeInvalido ex){}
    }

    /**
     * Test of conTipoNombreCuartoUsuarioMensaje method, of class
     * MensajesServidorCliente.
     */
    @Test
    public void testConTipoNombreCuartoUsuarioMensaje() {
        constructor.vacia();
        constructor.conNombreCuarto("Sala1").conNombreUsuario("Usuario").conMensaje("Mensaje");
       for(TiposMensaje tipo : TiposMensaje.values()){
            if(tipo == TiposMensaje.INVALID)
                continue;
            String mensajeEscrito=String.format("%s Sala1 Usuario Mensaje", tipo.toString());
            mensajeComparar=MensajesServidorCliente.conTipoNombreCuartoUsuarioMensaje(mensajeEscrito);
            mensaje=constructor.conTipo(tipo.toString()).construyeMensaje();
            Assert.assertTrue(mensaje.equals(mensajeComparar));                                      
        }
        try{
            mensajeComparar=MensajesServidorCliente.conTipoNombreCuartoUsuarioMensaje("L Sala1 Usuario Mensaje");
            Assert.fail();
        }catch(ExcepcionMensajeInvalido ex){}
    }

    /**
     * Test of convertirCadenaAMensaje method, of class MensajesServidorCliente.
     */
    @Test
    public void testConvertirCadenaATipoMensaje() {
        for(TiposMensaje tipo : TiposMensaje.values()){
            TiposMensaje tipoComparar=MensajesServidorCliente.
                    convertirCadenaATipoMensaje(tipo.toString().toLowerCase());
            Assert.assertTrue(tipo.equals(tipoComparar));
        }
    }

    @Test
    public void testConvertirCadenaAEstadoUsuario(){
        for(EstadoUsuario estado : EstadoUsuario.values()){
            EstadoUsuario estadoComparar=MensajesServidorCliente
                    .convertirCadenaAEstadoUsuario(estado.toString().toLowerCase());
            Assert.assertTrue(estado.equals(estadoComparar));
        }
    }
}
