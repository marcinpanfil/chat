package pl.mpanfil.chat.domain.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class Message {

    private String msg;
    private String userName;

}
