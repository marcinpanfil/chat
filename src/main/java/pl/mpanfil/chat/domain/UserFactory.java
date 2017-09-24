package pl.mpanfil.chat.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.mpanfil.chat.domain.dto.AddUserFormDTO;

@Component
public class UserFactory {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserFactory(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public long addUser(AddUserFormDTO userFormDTO) {
        User user = new User();
        user.setEmail(userFormDTO.getEmail());
        user.setUsername(userFormDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userFormDTO.getPassword()));
        user = userRepository.save(user);
        return user.getUserId();
    }

    public User loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
