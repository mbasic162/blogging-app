package com.example.bloggingapp.test;

import com.example.bloggingapp.config.FileStorageConfig;
import com.example.bloggingapp.dto.request.LoginRequest;
import com.example.bloggingapp.dto.request.RegisterRequest;
import com.example.bloggingapp.service.ImageTestService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class AuthTests {
    /*
    Changes:
    new_user is created with id 10
     */
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
    private final ImageTestService imageTestService;

    @Autowired
    public AuthTests(ImageTestService imageTestService) {
        this.imageTestService = imageTestService;
    }


    @Test
    @Order(0)
    void contextLoads() {
    }

    @Test
    @Order(1)
    void signup_WithNewUser_ShouldReturnNewUserDto() throws Exception {
        MultipartFile profilePicture = imageTestService.getImage(Paths.get(FileStorageConfig.PROFILE_PICTURE_DIR, "test.png"));
        RegisterRequest registerRequest = new RegisterRequest("new_user", "new@example.com", "newUser", "New User", profilePicture, false);
        mockMvc.perform(multipart("/auth/signup")
                        .file(new MockMultipartFile(registerRequest.profilePicture().getName(), registerRequest.profilePicture().getOriginalFilename(), registerRequest.profilePicture().getContentType(), registerRequest.profilePicture().getBytes()))
                        .param("username", registerRequest.username())
                        .param("email", registerRequest.email())
                        .param("password", registerRequest.password())
                        .param("description", registerRequest.description())
                        .param("isPrivate", String.valueOf(registerRequest.isPrivate()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    void login_WithFirstUser_ShouldReturnToken() throws Exception {
        LoginRequest firstUserLoginRequest = new LoginRequest("first_user", "firstUser");
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectWriter.writeValueAsString(firstUserLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void login_WithNewUser_ShouldReturnToken() throws Exception {
        LoginRequest newUserLoginRequest = new LoginRequest("new_user", "newUser");
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectWriter.writeValueAsString(newUserLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void login_WithPrivateUser_ShouldReturnToken() throws Exception {
        LoginRequest disabledUserLoginRequest = new LoginRequest("private_user", "privateUser");
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectWriter.writeValueAsString(disabledUserLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void login_WithDisabledUser_ShouldReturnForbidden() throws Exception {
        LoginRequest disabledUserLoginRequest = new LoginRequest("disabled_user", "disabledUser");
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectWriter.writeValueAsString(disabledUserLoginRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    void login_WithNonExistentUser_ShouldReturnNotFound() throws Exception {
        LoginRequest disabledUserLoginRequest = new LoginRequest("non_existent_user", "nonExistentUser");
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectWriter.writeValueAsString(disabledUserLoginRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    void login_WithWrongPassword_ShouldReturnUnauthorized() throws Exception {
        LoginRequest disabledUserLoginRequest = new LoginRequest("first_user", "first user");
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectWriter.writeValueAsString(disabledUserLoginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
