package pl.mpanfil.chat.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.mpanfil.chat.domain.dto.AddUserFormDTO;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserFactoryTest {

    private UserFactory userFactory;
    @MockBean
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Before
    public void setup() {
        passwordEncoder = new BCryptPasswordEncoder();
        userFactory = new UserFactory(userRepository, passwordEncoder);
    }

    @Test
    public void addUser() throws Exception {
        User user = new User(1, "test", "test", "test@test.pl");
        when(userRepository.save(any(User.class))).thenReturn(user);
        AddUserFormDTO userFormDTO = new AddUserFormDTO("test", "test", "test@test.pl");
        long id = userFactory.addUser(userFormDTO);
        assertEquals(1, id);
    }

}