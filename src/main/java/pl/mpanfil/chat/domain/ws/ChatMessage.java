package pl.mpanfil.chat.domain.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ChatMessage implements Serializable {

    private String msg;

}
