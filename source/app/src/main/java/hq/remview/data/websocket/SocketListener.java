package hq.remview.data.websocket;

public interface SocketListener {
    void onMessage(SocketEventModel socketEventModel);
    void onTimeout(Message message);
    void onError();
    void onConnected();
}
