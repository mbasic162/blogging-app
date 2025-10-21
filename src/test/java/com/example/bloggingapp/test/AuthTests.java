package com.example.bloggingapp.test;

import com.example.bloggingapp.dto.request.LoginRequest;
import com.example.bloggingapp.dto.request.RegisterRequest;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    @Order(0)
    void contextLoads() {
    }

    @Test
    @Order(1)
    void signup_WithNewUser_ShouldReturnNewUserDto() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("new_user", "new@example.com", "newUser", "New User", false);
        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(registerRequest)))
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
