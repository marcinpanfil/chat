package pl.mpanfil.chat.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.mpanfil.chat.domain.UserFactory;
import pl.mpanfil.chat.domain.dto.AddUserFormDTO;

import javax.validation.Valid;
import java.net.URI;

@RestController
public class UserController {

    private UserFactory userFactory;

    @Autowired
    public UserController(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    @RequestMapping(value = "/api/user", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Long> add(@RequestBody @Valid AddUserFormDTO addUserFormDTO) {
        long userId = userFactory.addUser(addUserFormDTO);
        return ResponseEntity.created(URI.create("/api/user/" + userId)).build();
    }

}
