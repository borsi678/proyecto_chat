package app;


public class MensajesServidorCliente {
    
    private static ConstructorMensajes constructor=new ConstructorMensajes();


    public static Mensajes conTIpoMensajeOperacionUsuario(String mensaje, TiposMensaje tipo){
        String[] argumentosMensaje=mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        mensaje="";
        for(int i = 2; i<argumentosMensaje.length; i++)
            mensaje+=argumentosMensaje[i];
        return constructor.conTipo(tipo.toString())
                                    .conOperacion(operacion.toString())
                                    .conMensaje(mensaje)
                                    .conNombreUsuario(argumentosMensaje[1])
                                    .construyeMensaje();
    }
    
    public static Mensajes conTipoUsuario(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                    .conNombreUsuario(argumentosMensaje[1])
                                    .construyeMensaje();

    }

    public static Mensajes conTipoMensajeOperacion(String mensaje, TiposMensaje tipo){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        mensaje=mensaje.replace(operacion.toString(), "");
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(tipo.toString())
                                    .conMensaje(mensaje)
                                    .conOperacion(operacion.toString())
                                    .construyeMensaje();
        
    }

    public static Mensajes conTipoEstado(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                    .conEstado(argumentosMensaje[1])
                                    .construyeMensaje();
        
    }

    public static Mensajes conTipoUsuarioEstado(String mensaje){
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

    public static Mensajes conTipoMensajeOperacionEstado(String mensaje, TiposMensaje tipo){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        mensaje="";
        for(int i=2; i < argumentosMensaje.length; i++)
                mensaje+=argumentosMensaje[i]+" ";
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(tipo.toString())
                                    .conMensaje(mensaje)
                                    .conOperacion(operacion.toString())
                                    .conEstado(argumentosMensaje[1])
                                    .construyeMensaje();
    }

    public static Mensajes conTipo(String mensaje){
        constructor.vacia();
        return constructor.conTipo(mensaje)
                                     .construyeMensaje();
    }

    public static Mensajes conTipoUsuarioMensaje(String mensaje){
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

    public static Mensajes conTipoMensaje(String mensaje){
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

    public static Mensajes conTipoNombreCuarto(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                    .conNombreCuarto(argumentosMensaje[1])
                                    .construyeMensaje();
    }

    public static Mensajes conTipoMensajeUsuarioNombreCuarto(String mensaje){
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

    public static Mensajes conTipoNombreCuartoUsuario(String mensaje){
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

    public static Mensajes conTipoUsuarios(String mensaje, String[] usuarios){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaAMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                     .conNombresUsuarios(usuarios)
                                     .construyeMensaje();
    }

    public static Mensajes conTipoNombreCuartoMensaje(String mensaje){
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

    public static Mensajes conTipoMensajeOperacionNombreCuarto(String mensaje, TiposMensaje tipo){
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

    public static Mensajes conTipoNombreCuartoUsuarios(String mensaje){
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

    public static Mensajes conTipoNombreCuartoUsuarioMensaje(String mensaje){
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
        for(TiposMensaje valor : TiposMensaje.values()){
            if(cadena.equals(valor.toString()))
                return valor;
        }
        return TiposMensaje.INVALID;
    }
    
    public static EstadoUsuario convertirCadenaAEstadoUsuario(String cadena){
        for(EstadoUsuario estado : EstadoUsuario.values()){
            if(cadena.equals(estado.toString()))
                return estado;
        }
        return EstadoUsuario.NONE;
    }
    
}
