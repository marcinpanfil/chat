package pl.mpanfil.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/message")
    @SendTo("/topic")
    public Message sendMsg(ChatMessage chatMessage) {
        return new Message(chatMessage.getMsg());
    }

}
