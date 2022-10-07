package app;

public enum EstadoUsuario {
    ACTIVE, AWAY, BUSY,NONE;
    
    @Override public String toString(){
        switch(this){
            case ACTIVE: return "ACTIVE";
            case AWAY: return "AWAY";
            case BUSY: return "BUSY";
            default: return "";
        }
    }
}
