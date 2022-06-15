package hq.remview.data.websocket;

import androidx.annotation.Nullable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import hq.remview.BuildConfig;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import timber.log.Timber;

public class WebSocketLiveData implements Runnable{
    private static final int SOCKET_STATE_NONE = 0;
    private static final int SOCKET_STATE_CONNECTING = 1;
    private static final int SOCKET_STATE_CONNECTED = 2;
    private static final int SOCKET_STATE_WAITING_RESPONSE = 3;

    @Setter
    private SocketListener socketListener;
    private BlockingQueue<Message> requests;
    private ScheduledExecutorService executor;

    private static WebSocketLiveData instance = new WebSocketLiveData();
    private WebSocket webSocket;
    private int socketState = SOCKET_STATE_NONE;

    private OkHttpClient okHttpClient;

    @Setter
    private boolean isAppOnline = false;


    private long lastTimeCall = System.currentTimeMillis();
    private Message lastMessage;

    @Setter
    private String session;

    private WebSocketLiveData() {
        okHttpClient = new OkHttpClient.Builder() .build();
        executor = Executors.newSingleThreadScheduledExecutor();
        requests = new LinkedBlockingQueue<>();
    }

    public static WebSocketLiveData getInstance() {
        return instance;
    }

    public void startSocket(){
        System.out.println("========> vao startSocket");
        isRunning = true;
        requests.clear();
        new Thread(this).start();
    }

    public void stopSocket(){
        System.out.println("========> vao stopSocket");
        try {
            isRunning = false;
            webSocket.close(1000,null);
            socketState = SOCKET_STATE_NONE;
            webSocket = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void postValue(SocketEventModel value) {
        if(isAppOnline && socketListener !=null){
            socketListener.onMessage(value);
        }

    }

    private synchronized void connect() {
        if(socketState == SOCKET_STATE_NONE){
            socketState = SOCKET_STATE_CONNECTING;
            System.out.println("========> vao connect");
            try {
                Request request = new Request.Builder().url(BuildConfig.WS_URL)
                        .addHeader("deviceId", "Android").build();
                webSocket = okHttpClient.newWebSocket(request, webSocketListener);
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public boolean sendEvent(Message message) {
        if (webSocket == null) {
            return false;
        }

        if(socketState != SOCKET_STATE_NONE){
            requests.add(message);
            return true;
        }

        return false;
    }

    private void sendMessage(){
        lastTimeCall = System.currentTimeMillis();
        socketState = SOCKET_STATE_WAITING_RESPONSE;

        Timber.d(lastMessage.getPayload());
        webSocket.send(lastMessage.getPayload());
    }

    private void sendPing(){
        if(session!=null && socketState == SOCKET_STATE_CONNECTED){
            lastTimeCall = System.currentTimeMillis();
            socketState = SOCKET_STATE_WAITING_RESPONSE;
            String cmd = "{ \"cmd\": \"CLIENT_PING\", \"platform\": 1, \"token\": \""+session+"\" }";
            webSocket.send(cmd);
            Timber.d("Ping socket: " + session);
        }
    }

    private void doVerifyToken(){
        lastTimeCall = System.currentTimeMillis();
        socketState = SOCKET_STATE_WAITING_RESPONSE;
        String cmd = "{ \"cmd\": \"VERIFY_TOKEN_CLIENT\", \"platform\": 1, \"token\": \""+session+"\" }";
        webSocket.send(cmd);
    }

    private WebSocketListener webSocketListener = new WebSocketListener() {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            socketState = SOCKET_STATE_CONNECTED;

            if(isAppOnline && socketListener != null){
                socketListener.onConnected();
            }

            System.out.println("========> vao onOpen");
            if(session!=null){
                doVerifyToken();
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            System.out.println("========> vao handleEvent "+ text);
            socketState = SOCKET_STATE_CONNECTED;
            lastTimeCall = -1;

            try {
                SocketEventModel eventModel = new SocketEventModel(null);
                Message message = Message.fromJson(text, Message.class);
                if(!message.getCmd().equals(Command.COMMAND_PING)){
                    if(lastMessage!=null){
                        message.setScreen(lastMessage.getScreen());
                    }
                    eventModel.setEvent(SocketEventModel.EVENT_MESSAGE);
                    eventModel.setMessage(message);

                    lastMessage = null;
                    postValue(eventModel);
                }
            }catch (Exception ex) {
                ex.printStackTrace();
                if(isAppOnline && socketListener!=null){
                    socketListener.onError();
                }
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            System.out.println("========> vao onClosed");
            socketState = SOCKET_STATE_NONE;
            lastTimeCall = -1;

            SocketEventModel eventModel = new SocketEventModel(null);
            eventModel.setEvent(SocketEventModel.EVENT_OFFLINE);
            postValue(eventModel);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            System.out.println("========> vao onFailure");
            socketState = SOCKET_STATE_NONE;
            lastTimeCall = -1;

            if(isAppOnline && socketListener!=null){
                socketListener.onError();
            }
        }
    };


    private synchronized void postTimeout(){
        System.out.println("========> vao postTimeout ");
       if(isAppOnline && socketListener!=null){
           socketListener.onTimeout(lastMessage);
       }
    }


    private boolean isRunning = true;
    private long lastPingTime = System.currentTimeMillis();
    @Override
    public void  run() {
        synchronized (this){
            while(isRunning){
                try {
                    if(socketState == SOCKET_STATE_NONE){
                        connect();
                    }else if(socketState == SOCKET_STATE_CONNECTED){
                        if(requests.size() > 0){
                            lastMessage = requests.take();
                        }
                        if(lastMessage!=null){
                            sendMessage();
                        }else if(System.currentTimeMillis() - lastPingTime > 25000){
                            //do ping to server if < 30 second
                            sendPing();
                            lastPingTime = System.currentTimeMillis();
                        }

                    }else if(socketState == SOCKET_STATE_WAITING_RESPONSE){
                        if(System.currentTimeMillis() - lastTimeCall > 30000){
                            // maximum 30s
                            postTimeout();
                            lastTimeCall = -1;
                            lastMessage = null;
                            socketState = SOCKET_STATE_CONNECTED;
                        }
                    }

                    wait(150L);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


}
