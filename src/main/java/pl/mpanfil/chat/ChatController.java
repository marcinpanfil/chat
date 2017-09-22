package pl.mpanfil.chat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/message")
    @SendTo("/topic")
    public Message sendMsg(ChatMessage chatMessage) {
        return new Message(chatMessage.getMsg(), "test");
    }

    @MessageMapping("/chat.addUser.{topic}")
    @SendTo("/topic/{topic}")
    public Message addUser(@Payload ChatMessage chatMessage, @DestinationVariable("topic") String topic,
                               SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getUserName());
        return new Message(chatMessage.getMsg(), chatMessage.getUserName());
    }

}
