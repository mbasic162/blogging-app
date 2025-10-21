package com.example.bloggingapp.test;

import com.example.bloggingapp.dto.CommentDto;
import com.example.bloggingapp.dto.request.CreateCommentRequest;
import com.example.bloggingapp.service.TestService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
public class CommentTests {
    /*
        Changes:
        new comment is created and deleted (auto_increment moves up)
    */
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    private final ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
    private final TestService testService;

    @Autowired
    public CommentTests(TestService testService) {
        this.testService = testService;
    }

    @Test
    @Order(0)
    void contextLoads() {
    }

    @Test
    @Order(1)
    @WithMockUser("new_user")
    //ID:14
    public void createComment_OnSecondPost_AsNewUser_ShouldReturnCommentDto() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest(2L, null, "This is a comment on second post");
        mockMvc.perform(post("/comment/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    @WithMockUser("new_user")
    public void createComment_OnFourteenthComment_AsNewUser_ShouldReturnCommentDto() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest(null, 14L, "This is a comment on fourteenth comment");
        mockMvc.perform(post("/comment/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @Order(3)
    @WithMockUser("private_user")
    public void createComment_OnFourteenthComment_AsPrivateUser_ShouldReturnCommentDto() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest(null, 14L, "This is a comment on fourteenth comment");
        mockMvc.perform(post("/comment/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @Order(3)
    @WithMockUser("new_user")
    public void createComment_WithInvalidParent_AsNewUser_ShouldReturnBadRequest() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest(null, null, "This comment is invalid");
        mockMvc.perform(post("/comment/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    public void getComments_OnSecondComment_ShouldReturnCommentDtos() throws Exception {
        MvcResult result = mockMvc.perform(get("/comment/comment-on-post-2-2/comments")).andExpect(status().isOk()).andDo(print()).andReturn();
        Set<CommentDto> commentsDto = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        testService.checkAllowViewingCommentDtos(commentsDto, "");
    }

    @Test
    @Order(4)
    @WithMockUser("new_user")
    public void getComments_OnFirstComment_AsNewUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/comment/comment-on-post-1/comments")).andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    public void getUri_WithFirstComment_ShouldReturnUri() throws Exception {
        MvcResult result = mockMvc.perform(post("/comment/uri")
                        .param("commentId", "1")
                        .param("content", "Comment on post 1"))
                .andExpect(status().isOk()).andDo(print()).andReturn();
        String uri = result.getResponse().getContentAsString();
        if (!uri.equalsIgnoreCase("comment-on-post-1-1")) {
            throw new RuntimeException("Wrong URI");
        }
    }

    @Test
    @Order(4)
    @WithMockUser("new_user")
    public void getUri_WithFirstComment_AsNewUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/comment/uri")
                        .param("commentId", "1")
                        .param("content", "Comment on post 1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @WithMockUser("first_user")
    public void getComment_WithNewUsersComment_AsFirstUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/comment/this-is-a-comment-on-second-post-14"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @WithMockUser("first_user")
    public void getComment_WithSecondComment_AsFirstUser_ShouldReturnCommentDto() throws Exception {
        mockMvc.perform(get("/comment/comment-on-post-2-2"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void getComment_WithPrivateUsersComment_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/comment/this-is-a-comment-on-fourteenth-comment-16"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    @WithMockUser("new_user")
    public void hide_WithFourteenthComment_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/comment/hide")
                        .param("commentId", "14"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    @WithMockUser("fourth_user")
    public void hide_WithSecondComment_AsFourthUser_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/comment/hide")
                        .param("commentId", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    @WithMockUser("new_user")
    public void delete_WithFourteenthComment_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/comment/delete")
                        .param("commentId", "14"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    @WithMockUser("new_user")
    public void unhide_WithFourteenthComment_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/comment/unhide")
                        .param("commentId", "14"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @WithMockUser("new_user")
    public void like_WithFourteenthComment_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/comment/like")
                        .param("commentId", "14"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @WithMockUser("new_user")
    public void dislike_WithFourteenthComment_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/comment/dislike")
                        .param("commentId", "14"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @WithMockUser("new_user")
    public void dislike_WithPrivateUsersComment_AsNewUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/comment/dislike")
                        .param("commentId", "16"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    @WithMockUser("new_user")
    public void undelete_WithFourteenthComment_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/comment/undelete")
                        .param("commentId", "14"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    @WithMockUser("new_user")
    public void changeContent_WithFourteenthComment_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/comment/changeContent")
                        .param("commentId", "14")
                        .param("newContent", "This is a comment on second post2"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    @WithMockUser("new_user")
    public void permanentlyDelete_WithFourteenthComment_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/comment/permanentlyDelete")
                        .param("commentId", "14"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(10)
    public void getComment_WithFourteenthComment_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/comment/this-is-a-comment-on-second-post-14"))
                .andExpect(status().isNotFound());
    }
}
