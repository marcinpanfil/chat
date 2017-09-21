package pl.mpanfil.chat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerIT {

    @Value("${local.server.port}")
    private int port;

    private String URL;
    private CompletableFuture<Message> completableFuture;

    @Before
    public void setup() {
        completableFuture = new CompletableFuture<>();
        URL = "ws://localhost:" + port + "/websocket";
    }

    @Test
    public void testSendingMessage() throws InterruptedException, ExecutionException, TimeoutException {
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL,
                new StompSessionHandlerAdapter() {
                }).get(1, SECONDS);

        stompSession.subscribe("/topic", createHandler());
        stompSession.send("/app/message", new ChatMessage("test"));

        Message message = completableFuture.get(10, SECONDS);
        assertNotNull(message);
        assertEquals(message.getMsg(), "test");
    }

    private StompFrameHandler createHandler() {
        return new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                System.out.print(headers.toString());
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println((Message) payload);
                completableFuture.complete((Message) payload);
            }
        };
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

}