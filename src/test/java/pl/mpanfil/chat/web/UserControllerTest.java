package pl.mpanfil.chat.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.mpanfil.chat.domain.UserFactory;
import pl.mpanfil.chat.domain.dto.AddUserFormDTO;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest {

    private MockMvc mockMvc;
    private UserController userController;
    @MockBean
    private UserFactory userFactory;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        userController = new UserController(userFactory);
        this.mockMvc = standaloneSetup(userController).build();
        when(userFactory.addUser(any())).thenReturn(1L);
    }

    @Test
    public void add() throws Exception {
        AddUserFormDTO addUserFormDTO = new AddUserFormDTO("test", "testpass", "test@test.pl");
        String json = objectMapper.writeValueAsString(addUserFormDTO);
        MvcResult mvcResult = mockMvc.perform(
                post("/api/user").accept(MediaType.APPLICATION_JSON_UTF8)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated()).andDo(print()).andReturn();
        String location = mvcResult.getResponse().getHeader("Location");
        assertEquals(location, "/api/user/1");
    }

    @Test
    public void incorrectMail() throws Exception {
        AddUserFormDTO addUserFormDTO = new AddUserFormDTO("test", "testpass", "test");
        String json = objectMapper.writeValueAsString(addUserFormDTO);
        mockMvc.perform(
                post("/api/user").accept(MediaType.APPLICATION_JSON_UTF8)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void incorrectUsername() throws Exception {
        AddUserFormDTO addUserFormDTO = new AddUserFormDTO("", "testpass", "test@test.pl");
        String json = objectMapper.writeValueAsString(addUserFormDTO);
        mockMvc.perform(
                post("/api/user").accept(MediaType.APPLICATION_JSON_UTF8)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

}