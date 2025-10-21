package com.example.bloggingapp.test;

import com.example.bloggingapp.dto.CommentDto;
import com.example.bloggingapp.dto.PostDto;
import com.example.bloggingapp.dto.UserDto;
import com.example.bloggingapp.dto.request.EmailChangeRequest;
import com.example.bloggingapp.dto.request.PasswordChangeRequest;
import com.example.bloggingapp.dto.request.RegisterRequest;
import com.example.bloggingapp.service.TestService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class UserTests {
    /*
    Changes:
    third_user and fourth_user block new_user
    fifth_user follows new_user
    new_user follows admin_user
    test user is created and deleted (auto_increment moves up)
    */
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    private final ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
    private final TestService testService;

    @Autowired
    public UserTests(TestService testService) {
        this.testService = testService;
    }

    @Test
    @Order(0)
    void contextLoads() {
    }

    @Test
    @Order(2)
    @WithMockUser("third_user")
    void block_WithNewUser_AsThirdUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/user/block")
                        .param("username", "new_user"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @WithMockUser("fourth_user")
    void block_WithNewUser_AsFourthUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/user/block")
                        .param("username", "new_user"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @WithMockUser("fifth_user")
    void follow_WithNewUser_AsFifthUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/user/follow")
                        .param("username", "new_user"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    @WithMockUser("new_user")
    void follow_WithFirstUser_AsNewUser_ShouldReturnOK() throws Exception {
        mockMvc.perform(post("/user/follow")
                        .param("username", "first_user"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    @WithMockUser("new_user")
    void unfollow_WithFirstUser_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/user/unfollow")
                        .param("username", "first_user"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    @WithMockUser("new_user")
    void delete_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/user/delete")).andExpect(status().isOk());
    }

    @Test
    @Order(6)
    @WithMockUser("new_user")
    void undelete_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/user/undelete")).andExpect(status().isOk());
    }

    @Test
    @Order(7)
    void getUser_WithFirstUser_ShouldReturnFirstUserDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/first_user")).andExpect(status().isOk()).andDo(print()).andReturn();
        UserDto userDto = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        testService.checkAllowViewingUserDto(userDto, "");
    }

    @Test
    @Order(7)
    @WithMockUser("new_user")
    @Transactional
    void getPosts_WithFirstUser_AsNewUser_ShouldReturnPostDtos() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/first_user/posts")).andExpect(status().isOk()).andDo(print()).andReturn();
        Set<PostDto> postDtos = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        testService.checkAllowViewingPostDtos(postDtos, "new_user");
    }

    @Test
    @Order(7)
    @WithMockUser("new_user")
    @Transactional
    void getComments_WithFirstUser_AsNewUser_ShouldReturnCommentDtos() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/first_user/comments")).andExpect(status().isOk()).andDo(print()).andReturn();
        Set<CommentDto> commentsDto = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        testService.checkAllowViewingCommentDtos(commentsDto, "new_user");
    }

    @Test
    @Order(8)
    @WithMockUser("new_user")
    void block_WithFirstUser_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/user/block")
                        .param("username", "first_user"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    @WithMockUser("new_user")
    void getPosts_WithFirstUser_AsNewUser_WhenFirstUserBlocked_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/user/first_user/posts")).andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @WithMockUser("new_user")
    void getComments_WithFirstUser_AsNewUser_WhenFirstUserBlocked_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/user/first_user/comments")).andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @WithMockUser("first_user")
    void getPosts_WithNewUser_AsFirstUser_WhenBlockedByNewUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/user/new_user/posts")).andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @WithMockUser("first_user")
    void getComment_WithNewUser_AsFirstUser_WhenBlockedByNewUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/user/new_user/comments")).andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @WithMockUser("new_user")
    void follow_WithFirstUser_AsNewUser_WhenFirstUserBlocked_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/user/follow")
                        .param("username", "first_user"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @WithMockUser("first_user")
    void follow_WithNewUser_AsFirstUser_WhenBlockedByNewUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/user/follow")
                        .param("username", "new_user"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @WithMockUser("first_user")
        //Returns a dto with just username so you can unblock users
    void getUser_WithNewUser_AsFirstUser_WhenBlockedByNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/user/new_user")).andExpect(status().isOk()).andDo(print());
    }

    @Test
    @Order(10)
    @WithMockUser("new_user")
    void follow_WithDeletedUser_AsNewUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/user/follow")
                        .param("username", "deleted_user"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    @WithMockUser("new_user")
    void follow_WithDisabledUser_AsNewUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/user/follow")
                        .param("username", "disabled_user"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    @WithMockUser("new_user")
    void follow_WithPrivateUser_AsNewUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/user/follow")
                        .param("username", "private_user"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    @WithMockUser("new_user")
    void follow_WithAdminUser_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/user/follow")
                        .param("username", "admin_user"))
                .andExpect(status().isOk());
    }


    @Test
    @Order(11)
    void getUser_WithAdminUser_ShouldReturnUserDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/user/admin_user")).andExpect(status().isOk()).andDo(print()).andReturn();
        UserDto userDto = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        testService.checkAllowViewingUserDto(userDto, "");
    }

    @Test
    @Order(11)
    void getUser_WithPrivateUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/user/private_user")).andExpect(status().isNotFound());
    }

    @Test
    @Order(12)
    void signup_WithTestUser_ShouldReturnUserDto() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("test_user", "test@example.com", "testUser", "Test User", true);
        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @Order(13)
    @WithMockUser("test_user")
    void goPublic_AsTestUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/user/goPublic")).andExpect(status().isOk());
    }

    @Test
    @Order(13)
    @WithMockUser("test_user")
    void delete_AsTestUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/user/delete")).andExpect(status().isOk());
    }

    @Test
    @Order(14)
    @WithMockUser("test_user")
    void goPrivate_AsTestUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/user/goPrivate")).andExpect(status().isOk());
    }

    @Test
    @Order(15)
    @WithMockUser("test_user")
    void changeDescription_AsTestUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/user/changeDescription")
                        .param("newDescription", "new description"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(15)
    @WithMockUser("test_user")
    void changeEmail_AsTestUser_ShouldReturnOk() throws Exception {
        EmailChangeRequest request = new EmailChangeRequest("test2@test.com", "testUser");
        mockMvc.perform(post("/user/changeEmail").contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(15)
    @WithMockUser("test_user")
    void changePassword_WithIncorrectPassword_AsTestUser_ShouldReturnUnauthorized() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest("test_User", "test_User2");
        mockMvc.perform(post("/user/changePassword").contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(16)
    @WithMockUser("test_user")
    void changePassword_AsTestUser_ShouldReturnOk() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest("testUser", "testUser2");
        mockMvc.perform(post("/user/changePassword").contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(request)))
                .andExpect(status().isOk());
    }


    @Test
    @Order(17)
    @WithMockUser("test_user")
    void changeUsername_AsTestUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/user/changeUsername")
                        .param("newUsername", "test_user2"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(18)
    @WithMockUser("test_user2")
    void permanentlyDelete_AsTestUser2_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/user/permanentlyDelete")
                .param("password", "testUser2")).andExpect(status().isOk());
    }
}
