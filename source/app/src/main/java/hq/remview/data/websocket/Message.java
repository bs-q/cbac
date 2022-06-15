package hq.remview.data.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import lombok.Data;

@Data
public class Message {
    static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                if (src == src.longValue()) {
                    return new JsonPrimitive(src.longValue());
                }
                return new JsonPrimitive(src);
            })
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();

    private Integer platform = 1;
    private String cmd;
    private Object data;
    private String screen;
    private Integer responseCode;
    private String msg;
    private String token;


    public String getPayload() {
        return GSON.toJson(this);
    }


    public <T> T getDataObject(Class<T> classOfT) {
        if (data == null) {
            return null;
        } else {
            T target = GSON.fromJson(GSON.toJsonTree(data), classOfT);
            return target;
        }
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (json == null || "".equals(json) ) {
            return null;
        } else {
            T target = null;
            try {
                target = GSON.fromJson(json, classOfT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return target;
        }
    }
}
