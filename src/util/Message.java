package util;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = -2723363051271966964L;

    public static enum Type {
        LoadGame,
        SaveGame,
        GameListingResponse,
        GameInformationResponse
    }

    private Type type;
    private Object data;

    public Message(Type type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
