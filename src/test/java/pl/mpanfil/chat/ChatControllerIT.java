package pl.mpanfil.chat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerIT {

    private static Logger LOG = LoggerFactory.getLogger(ChatControllerIT.class);

    @Value("${local.server.port}")
    private int port;

    private String URL;
    private CompletableFuture<Message> completableFuture;

    @Autowired
    private WebSocketEventListener eventListener;

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
        stompSession.send("/app/message", new ChatMessage("test", "test"));

        Message message = completableFuture.get(10, SECONDS);
        assertNotNull(message);
        assertEquals(message.getMsg(), "test");
    }

    @Test
    public void testAddingUser() throws InterruptedException, ExecutionException, TimeoutException {
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL,
                new StompSessionHandlerAdapter() {
                }).get(1, SECONDS);

        stompSession.subscribe("/topic/public", createHandler());
        stompSession.send("/app/chat.addUser.public", new ChatMessage("login", "testuser"));

        Message message = completableFuture.get(10, SECONDS);
        assertNotNull(message);
        assertEquals(message.getMsg(), "login");
        assertEquals(message.getUserName(), "testuser");
        assertEquals(1, eventListener.getSessionIds().size());
    }

    private StompFrameHandler createHandler() {
        return new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                LOG.info(headers.toString());
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                LOG.info(payload.toString());
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