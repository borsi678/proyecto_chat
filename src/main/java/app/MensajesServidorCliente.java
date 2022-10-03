package app;


public class MensajesServidorCliente {
    
    private static ConstructorMensajes constructor=new ConstructorMensajes();


    public static Mensajes conTIpoMensajeOperacionUsuaro(String mensaje){
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(mensaje);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        
        return constructor.conTipo(operacion.toString())
                .construyeMensaje();
        
    }
    
    public Mensajes conTipoUsuario(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                    .conNombreUsuario(argumentosMensaje[1])
                                    .construyeMensaje();

    }

    public Mensajes conTipoMensajeOperacion(String mensaje, TiposMensaje tipo){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        mensaje=mensaje.replace("/"+operacion.toString(), "");
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(tipo.toString())
                                    .conMensaje(mensaje)
                                    .conOperacion(operacion.toString())
                                    .construyeMensaje();
        
    }

    public Mensajes conTipoEstado(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                    .conEstado(argumentosMensaje[1])
                                    .construyeMensaje();
        
    }

    public Mensajes conTipoUsuarioEstado(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                    .conNombreUsuario(argumentosMensaje[1])
                                    .conEstado(argumentosMensaje[2])
                                    .construyeMensaje();

    }

    public Mensajes conTipoMensajeOperacionEstado(String mensaje, TiposMensaje tipo){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        for(int i=2; i < argumentosMensaje.length; i++)
            mensaje+=argumentosMensaje[i];
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(tipo.toString())
                                    .conMensaje(mensaje)
                                    .conOperacion(operacion.toString())
                                    .conEstado(argumentosMensaje[1])
                                    .construyeMensaje();
    }

    public Mensajes conTipo(String mensaje){
        constructor.vacia();
        return constructor.conTipo(mensaje)
                                     .construyeMensaje();
    }

    public Mensajes conTipoUsuarioMensaje(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        for(int i=2; i < argumentosMensaje.length; i++)
            mensaje+=argumentosMensaje[i];
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                    .conNombreUsuario(argumentosMensaje[1])
                                    .conMensaje(mensaje)
                                    .construyeMensaje();
    }

    public Mensajes conTipoMensaje(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        for (int i = 1; i < argumentosMensaje.length; i++) {
            mensaje += argumentosMensaje[i];
        }
        if (operacion == TiposMensaje.INVALID) {
            throw new ExcepcionMensajeInvalido();
        }
        return constructor.conTipo(operacion.toString())
                                    .conMensaje(mensaje)
                                    .construyeMensaje();
    }

    public Mensajes conTipoNombreSala(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                    .conNombreCuarto(argumentosMensaje[1])
                                    .construyeMensaje();
    }

    public Mensajes conTipoMensajeUsuarioNombreSala(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        for(int i=3; i < argumentosMensaje.length; i++)
            mensaje+=argumentosMensaje[i];
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                     .conMensaje(mensaje)
                                     .conNombreUsuario(argumentosMensaje[1])
                                     .conNombreCuarto(argumentosMensaje[2])
                                     .construyeMensaje();
    }

    public Mensajes conTipoNombreSalaUsuario(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        if (operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                     .conNombreCuarto(argumentosMensaje[1])
                                     .conNombreUsuario(argumentosMensaje[2])
                                     .construyeMensaje();
        
    }

    public Mensajes conTipoUsuarios(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        String[] usuarios=new String[argumentosMensaje.length-1];
        for(int i=1; i < argumentosMensaje.length; i++){
            usuarios[i]=argumentosMensaje[i];
        }
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                     .conNombresUsuarios(usuarios)
                                     .construyeMensaje();
    }

    public Mensajes conTipoNombreSalaMensaje(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        for(int i=2; i < argumentosMensaje.length; i++)
            mensaje+=argumentosMensaje[i];
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                     .conNombreCuarto(argumentosMensaje[1])
                                     .conMensaje(mensaje)
                                     .construyeMensaje();
    }

    public Mensajes conTipoMensajeOperacionNombreSala(String mensaje, TiposMensaje tipo){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        for(int i=2; i < argumentosMensaje.length; i++)
            mensaje+=argumentosMensaje[i];
        if(operacion == TiposMensaje.INVALID || tipo == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(tipo.toString())
                                     .conMensaje(mensaje)
                                     .conOperacion(operacion.toString())
                                     .conNombreCuarto(argumentosMensaje[1])
                                     .construyeMensaje();
    }

    public Mensajes conTipoNombreSalaUsuarios(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        String[] usuarios=new String[argumentosMensaje.length];
        for(int i=2; i < argumentosMensaje.length; i++)
               usuarios[i]=argumentosMensaje[i];
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                     .conNombreCuarto(argumentosMensaje[1])
                                     .conNombresUsuarios(usuarios)
                                     .construyeMensaje();
    }

    public Mensajes conTipoNombreSalaUsuarioMensaje(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        for(int i=3; i < argumentosMensaje.length; i++)
            mensaje+=argumentosMensaje[i];
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                     .conNombreCuarto(argumentosMensaje[1])
                                     .conNombreUsuario(argumentosMensaje[2])
                                     .conMensaje(mensaje)
                                     .construyeMensaje();
    }

    public static TiposMensaje convertirCadenaAMensaje(String cadena){
        for(TiposMensaje valores : TiposMensaje.values()){
            if(cadena.equals(valores.toString()))
                return valores;
        }
        return TiposMensaje.INVALID;
    }
    
}
