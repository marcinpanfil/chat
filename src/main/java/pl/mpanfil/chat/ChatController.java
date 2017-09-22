package pl.mpanfil.chat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    @MessageMapping("/message")
    @SendTo("/topic")
    public Message sendMsg(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor,
                           Principal principal) {
        headerAccessor.getSessionAttributes().put("username", principal.getName());
        return new Message(chatMessage.getMsg(), principal.getName());
    }

    @SubscribeMapping("/topic/{topic}")
    @SendTo("/topic/{topic}")
    public Message addUser(@Payload ChatMessage chatMessage, @DestinationVariable("topic") String topic,
                           SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        headerAccessor.getSessionAttributes().put("username", principal.getName());
        return new Message(chatMessage.getMsg(), principal.getName());
    }

}
