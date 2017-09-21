package pl.mpanfil.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ChatMessage implements Serializable {

    private String msg;

}
