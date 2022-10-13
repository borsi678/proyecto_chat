package app;

public  enum TiposMensaje {
    IDENTIFY, STATUS, NEW_STATUS, USERS, MESSAGE, PUBLIC_MESSAGE, INVITE, 
    JOIN_ROOM, ROOM_USERS, ROOM_MESSAGE, LEAVE_ROOM, DISCONNECT, NEW_USER, USER_LIST, MESSAGE_FROM,
    PUBLIC_MESSAGE_FROM, ROOM_USER_LIST, ROOM_MESSAGE_FROM,
    LEFT_ROOM,  DISCONNECTED, NEW_ROOM, INVITATION, JOINED_ROOM, 
    
    ERROR, WARNING,  INFO, INVALID;
    
    @Override public String toString(){
        switch(this){
            case ERROR: return "ERROR";
            case WARNING: return "WARNING";
            case INFO: return "INFO";
            case IDENTIFY: return "IDENTIFY";
            case NEW_USER: return "NEW_USER";
            case STATUS: return "STATUS";
            case NEW_STATUS: return "NEW_STATUS";
            case USERS: return "USERS";
            case USER_LIST: return "USER_LIST";
            case MESSAGE: return "MESSAGE";
            case MESSAGE_FROM: return "MESSAGE_FROM";
            case PUBLIC_MESSAGE: return "PUBLIC_MESSAGE";
            case PUBLIC_MESSAGE_FROM: return "PUBLIC_MESSAGE_FROM";
            case NEW_ROOM: return "NEW_ROOM";
            case INVITE: return "INVITE";
            case INVITATION: return "INVITATION";
            case JOIN_ROOM: return "JOIN_ROOM";
            case JOINED_ROOM: return "JOINED_ROOM";
            case ROOM_USERS: return "ROOM_USERS";
            case ROOM_USER_LIST: return "ROOM_USER_LIST";
            case ROOM_MESSAGE: return "ROOM_MESSAGE";
            case ROOM_MESSAGE_FROM:  return "ROOM_MESSAGE_FROM";            
            case LEAVE_ROOM: return "LEAVE_ROOM";
            case LEFT_ROOM: return "LEFT_ROOM";
            case DISCONNECTED: return "DISCONNECTED";
            case DISCONNECT: return "DISCONNECT";
            case INVALID: return "INVALID";
            default: return "";
            
        }
    }
}
