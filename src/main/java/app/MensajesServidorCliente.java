package app;


public class MensajesServidorCliente {
    
    private static ConstructorMensajes constructor=new ConstructorMensajes();


    public static Mensajes conTIpoMensajeOperacionUsuario(String mensaje, TiposMensaje tipo){
        String[] argumentosMensaje=mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        mensaje=concatenaCadenasMensaje(argumentosMensaje, 2);
        return constructor.conTipo(tipo.toString())
                                    .conOperacion(operacion.toString())
                                    .conMensaje(mensaje)
                                    .conNombreUsuario(argumentosMensaje[1])
                                    .construyeMensaje();
    }
    
    public static Mensajes conTipoUsuario(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                    .conNombreUsuario(argumentosMensaje[1])
                                    .construyeMensaje();

    }

    public static Mensajes conTipoMensajeOperacion(String mensaje, TiposMensaje tipo){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(tipo.toString())
                                    .conMensaje(argumentosMensaje[1])
                                    .conOperacion(operacion.toString())
                                    .construyeMensaje();
        
    }

    public static Mensajes conTipoEstado(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        if(argumentosMensaje.length<2)
            throw new ExcepcionEstadoInvalido();
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        EstadoUsuario estado=convertirCadenaAEstadoUsuario(argumentosMensaje[1]);
        if(estado == EstadoUsuario.NONE)
            throw new ExcepcionEstadoInvalido();
        return constructor.conTipo(operacion.toString())
                                    .conEstado(estado.toString())
                                    .construyeMensaje();
        
    }

    public static Mensajes conTipoUsuarioEstado(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        EstadoUsuario estado=convertirCadenaAEstadoUsuario(argumentosMensaje[2]);
        if(estado== EstadoUsuario.NONE)
            throw new ExcepcionEstadoInvalido();
        return constructor.conTipo(operacion.toString())
                                    .conNombreUsuario(argumentosMensaje[1])
                                    .conEstado(estado.toString())
                                    .construyeMensaje();

    }

    public static Mensajes conTipoMensajeOperacionEstado(String mensaje, TiposMensaje tipo){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        EstadoUsuario estado= convertirCadenaAEstadoUsuario(argumentosMensaje[1]);
        if(estado == EstadoUsuario.NONE)
            throw new ExcepcionEstadoInvalido();
        mensaje=concatenaCadenasMensaje(argumentosMensaje, 2);
        return constructor.conTipo(tipo.toString())
                                    .conMensaje(mensaje)
                                    .conOperacion(operacion.toString())
                                    .conEstado(estado.toString())
                                    .construyeMensaje();
    }

    public static Mensajes conTipo(String mensaje){
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaATipoMensaje(mensaje);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                     .construyeMensaje();
    }

    public static Mensajes conTipoUsuarioMensaje(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        mensaje=concatenaCadenasMensaje(argumentosMensaje, 2);
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
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        mensaje=concatenaCadenasMensaje(argumentosMensaje, 1);
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
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                    .conNombreCuarto(argumentosMensaje[1])
                                    .construyeMensaje();
    }

    public static Mensajes conTipoMensajeUsuarioNombreCuarto(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        mensaje=concatenaCadenasMensaje(argumentosMensaje, 3);
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
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
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
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                     .conNombresUsuarios(usuarios)
                                     .construyeMensaje();
    }

    public static Mensajes conTipoNombreCuartoMensaje(String mensaje){
        String[] argumentosMensaje = mensaje.split(" ");
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        mensaje=concatenaCadenasMensaje(argumentosMensaje, 2);
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
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        mensaje=concatenaCadenasMensaje(argumentosMensaje, 2);
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
        if(argumentosMensaje.length < 3)
            throw new ExcepcionMensajeInvalido("Falta nombre de cuarto o nombres de los usuarios"); 
        constructor.vacia();
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        String[] usuarios=new String[argumentosMensaje.length-2];
        for(int i=2; i < argumentosMensaje.length; i++){
            if(argumentosMensaje[i].contains(","))
                argumentosMensaje[i]=argumentosMensaje[i].replace(",", "");
               usuarios[i-2]=argumentosMensaje[i];
        }
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
        TiposMensaje operacion = convertirCadenaATipoMensaje(argumentosMensaje[0]);
        mensaje=concatenaCadenasMensaje(argumentosMensaje, 3);
        if(operacion == TiposMensaje.INVALID)
            throw new ExcepcionMensajeInvalido();
        return constructor.conTipo(operacion.toString())
                                     .conNombreCuarto(argumentosMensaje[1])
                                     .conNombreUsuario(argumentosMensaje[2])
                                     .conMensaje(mensaje)
                                     .construyeMensaje();
    }
    
    private static String concatenaCadenasMensaje(String[] mensaje, int indice){
        String mensajeConcatenado="";
        for(int i=indice ;i<mensaje.length; i++){
            if(i==mensaje.length-1){
                mensajeConcatenado+=mensaje[i];
                continue;
            }
            mensajeConcatenado+=mensaje[i]+" ";
        }
        return mensajeConcatenado;
    }
    public static TiposMensaje convertirCadenaATipoMensaje(String cadena){
        cadena=cadena.toUpperCase();
        for(TiposMensaje valor : TiposMensaje.values()){
            if(cadena.equals(valor.toString()))
                return valor;
        }
        return TiposMensaje.INVALID;
    }
    
    public static EstadoUsuario convertirCadenaAEstadoUsuario(String cadena){
        cadena=cadena.toUpperCase();
        for(EstadoUsuario estado : EstadoUsuario.values()){
            if(cadena.equals(estado.toString()))
                return estado;
        }
        return EstadoUsuario.NONE;
    }
    
    
}
