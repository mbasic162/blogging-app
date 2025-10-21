package com.example.bloggingapp.test;

import com.example.bloggingapp.dto.CommentDto;
import com.example.bloggingapp.dto.PostDto;
import com.example.bloggingapp.dto.request.CreatePostRequest;
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
public class PostTests {
    /*
        Changes:
        new post is created and deleted (auto_increment moves up)
    */
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    private final ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
    private final TestService testService;

    @Autowired
    public PostTests(TestService testService) {
        this.testService = testService;
    }

    @Test
    @Order(0)
    void contextLoads() {
    }

    @Test
    @Order(1)
    @WithMockUser("new_user")
    public void createPost_AsNewUser_ShouldReturnPostDto() throws Exception {
        CreatePostRequest request = new CreatePostRequest("New user's post", "this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum", false);
        mockMvc.perform(post("/post/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    @WithMockUser("private_user")
    public void createPost_AsPrivateUser_ShouldReturnPostDto() throws Exception {
        CreatePostRequest request = new CreatePostRequest("Private user's post", "this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum", false);
        mockMvc.perform(post("/post/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    @WithMockUser("new_user")
    public void createPost_WithInvalidContent_AsNewUser_ShouldReturnBadRequest() throws Exception {
        CreatePostRequest request = new CreatePostRequest("New user's post", "This text is too short", false);
        mockMvc.perform(post("/post/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @Transactional
    public void getN_ShouldReturnPostDtos() throws Exception {
        MvcResult result = mockMvc.perform(post("/post/")
                        .param("number", "5"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        Set<PostDto> postDtos = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        testService.checkAllowViewingPostDtos(postDtos, "");
    }

    @Test
    @Order(3)
    @Transactional
    @WithMockUser("new_user")
    public void getN_AsNewUser_ShouldReturnPostDtos() throws Exception {
        MvcResult result = mockMvc.perform(post("/post/")
                        .param("number", "5"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        Set<PostDto> postDtos = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        testService.checkAllowViewingPostDtos(postDtos, "new_user");
    }

    @Test
    @Order(3)
    public void getComments_WithFirstPost_ShouldReturnCommentDtos() throws Exception {
        MvcResult result = mockMvc.perform(get("/post/first-users-post-1/comments")).andExpect(status().isOk()).andReturn();
        Set<CommentDto> commentDtos = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        testService.checkAllowViewingCommentDtos(commentDtos, "");
    }

    @Test
    @Order(3)
    @WithMockUser("new_user")
    public void getComments_WithFirstPost_AsNewUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/post/first-users-post-1/comments")).andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    public void getUri_WithThirdPost_ShouldReturnUri() throws Exception {
        MvcResult result = mockMvc.perform(post("/post/uri")
                        .param("postId", "3")
                        .param("title", "Third user's post"))
                .andExpect(status().isOk()).andReturn();
        String uri = result.getResponse().getContentAsString();
        if (!uri.equalsIgnoreCase("third-users-post-3")) {
            throw new RuntimeException("Wrong URI");
        }
    }

    @Test
    @Order(3)
    @WithMockUser("new_user")
    public void getUri_WithThirdPost_AsNewUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/post/uri")
                        .param("postId", "3")
                        .param("title", "Third user's post"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    @WithMockUser("first_user")
    public void getPost_WithNewUsersPost_AsFirstUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/post/new-users-post-4"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    @Transactional
    @WithMockUser("first_user")
    public void getPost_WithSecondPost_AsFirstUser_ShouldReturnPostDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/post/second-users-post-2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        PostDto postDto = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        testService.checkAllowViewingPostDto(postDto, "first_user");
    }

    @Test
    @Order(3)
    public void getPost_WithPrivateUsersPost_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/post/private-users-post-5"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @WithMockUser("new_user")
    public void hide_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/hide")
                        .param("postId", "4"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    @WithMockUser("fourth_user")
    public void hide_WithSecondPost_AsFourthUser_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/post/hide")
                        .param("postId", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @WithMockUser("new_user")
    public void delete_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/delete")
                        .param("postId", "4"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    @WithMockUser("new_user")
    public void unhide_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/unhide")
                        .param("postId", "4"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @WithMockUser("new_user")
    public void like_WithFourthPost_AsNewUSer_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/like")
                        .param("postId", "4"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @WithMockUser("new_user")
    public void dislike_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/dislike")
                        .param("postId", "4"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @WithMockUser("new_user")
    public void dislike_WithPrivateUsersPost_AsNewUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/post/dislike")
                        .param("postId", "5"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    @WithMockUser("new_user")
    public void undelete_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/undelete")
                        .param("postId", "4"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    @WithMockUser("new_user")
    public void changeTitle_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/changeTitle")
                        .param("postId", "4")
                        .param("newTitle", "New Title"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    @WithMockUser("new_user")
    public void changeContent_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/changeContent")
                        .param("postId", "4")
                        .param("newContent", "this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum, new content"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(10)
    @WithMockUser("new_user")
    public void permanentlyDelete_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/permanentlyDelete")
                        .param("postId", "4"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(11)
    public void getPost_WithFourthPost_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/post/new-users-post-4"))
                .andExpect(status().isNotFound());
    }
}
