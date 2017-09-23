package pl.mpanfil.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddUserFormDTO implements Serializable {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @Email
    private String email;

}
