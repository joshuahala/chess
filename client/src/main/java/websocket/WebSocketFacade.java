package websocket;
import javax.websocket.*;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    public static void main(String[] args) throws Exception {
        var ws = new WebSocketFacade();

        ws.send("This is a ws test.");
    }

    public Session session;

    public WebSocketFacade() throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
            }
        });
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}