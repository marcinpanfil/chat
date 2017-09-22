package pl.mpanfil.chat;

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
