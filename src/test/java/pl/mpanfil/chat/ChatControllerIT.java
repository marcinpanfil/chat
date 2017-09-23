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
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import pl.mpanfil.chat.domain.ws.ChatMessage;
import pl.mpanfil.chat.domain.ws.Message;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

        StompSession stompSession = connectToServer(stompClient);

        stompSession.subscribe("/topic", createHandler());
        stompSession.send("/app/message", new ChatMessage("test"));

        Message message = completableFuture.get(60000, SECONDS);
        assertNotNull(message);
        assertEquals(message.getMsg(), "test");

        stompSession.disconnect();
    }

    @Test
    public void testAddingUser() throws InterruptedException, ExecutionException, TimeoutException {
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = connectToServer(stompClient);

        stompSession.subscribe("/topic/public", createHandler());
        stompSession.send("/app/topic/public", new ChatMessage("login"));

        Message message = completableFuture.get(60000, SECONDS);
        assertNotNull(message);
        assertEquals(message.getMsg(), "login");
        assertEquals(message.getUserName(), "test");
        assertEquals(1, eventListener.getSessionIds().size());

        stompSession.disconnect();
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

    private StompSession connectToServer(WebSocketStompClient stompClient) throws InterruptedException,
            ExecutionException, TimeoutException {
        return stompClient
                .connect(URL, createHttpHeaders(), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);
    }

    private WebSocketHttpHeaders createHttpHeaders() {
        WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + getOAuth2AccessToken().getValue());
        return httpHeaders;
    }

    private OAuth2AccessToken getOAuth2AccessToken() {
        ResourceOwnerPasswordResourceDetails resourceDetails = createResourceDetails();

        DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();

        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails, clientContext);
        return restTemplate.getAccessToken();
    }

    private ResourceOwnerPasswordResourceDetails createResourceDetails() {
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
        resourceDetails.setUsername("test");
        resourceDetails.setPassword("test");
        resourceDetails.setAccessTokenUri(String.format("http://localhost:%d/oauth/token", port));
        resourceDetails.setClientId("chat");
        resourceDetails.setClientSecret("secret");
        resourceDetails.setGrantType("password");
        resourceDetails.setScope(asList("read", "write"));
        return resourceDetails;
    }

}