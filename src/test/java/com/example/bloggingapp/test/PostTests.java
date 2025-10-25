package com.example.bloggingapp.test;

import com.example.bloggingapp.dto.CommentDto;
import com.example.bloggingapp.dto.PostDto;
import com.example.bloggingapp.dto.PostPreviewDto;
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
@SuppressWarnings("FieldCanBeLocal")
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
    private final String firstPostUri = "first-users-post-1";
    private final String secondPostUri = "second-users-post-2";
    private final String thirdPostUri = "third-users-post-3";
    private final String fourthPostUri = "new-users-post-4";
    private final String fifthPostUri = "private-users-post-5";

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
    public void getNPosts_ShouldReturnPostDtos() throws Exception {
        MvcResult result = mockMvc.perform(post("/post/")
                        .param("numberOfPosts", "5"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        Set<PostPreviewDto> postPreviewDtos = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        testService.checkAllowViewingPostPreviewDtos(postPreviewDtos, "");
    }

    @Test
    @Order(3)
    @Transactional
    @WithMockUser("new_user")
    public void getNPosts_AsNewUser_ShouldReturnPostDtos() throws Exception {
        MvcResult result = mockMvc.perform(post("/post/")
                        .param("numberOfPosts", "5"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        Set<PostPreviewDto> postPreviewDtos = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        testService.checkAllowViewingPostPreviewDtos(postPreviewDtos, "new_user");
    }

    @Test
    @Order(3)
    public void getComments_WithFirstPost_ShouldReturnCommentDtos() throws Exception {
        MvcResult result = mockMvc.perform(get("/post/" + firstPostUri + "/comments")).andExpect(status().isOk()).andReturn();
        Set<CommentDto> commentDtos = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        testService.checkAllowViewingCommentDtos(commentDtos, "");
    }

    @Test
    @Order(3)
    @WithMockUser("new_user")
    public void getComments_WithFirstPost_AsNewUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/post/" + firstPostUri + "/comments")).andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    public void getUri_WithThirdPost_ShouldReturnUri() throws Exception {
        MvcResult result = mockMvc.perform(post("/post/uri")
                        .param("postId", "3")
                        .param("title", "Third user's post"))
                .andExpect(status().isOk()).andReturn();
        String uri = result.getResponse().getContentAsString();
        if (!uri.equalsIgnoreCase(thirdPostUri)) {
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
        mockMvc.perform(get("/post/" + fourthPostUri))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    @Transactional
    @WithMockUser("first_user")
    public void getPost_WithSecondPost_AsFirstUser_ShouldReturnPostDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/post/" + secondPostUri))
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
        mockMvc.perform(get("/post/" + fifthPostUri))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @WithMockUser("new_user")
    public void hide_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/hide")
                        .param("postUri", fourthPostUri))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    @WithMockUser("fourth_user")
    public void hide_WithSecondPost_AsFourthUser_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/post/hide")
                        .param("postUri", secondPostUri))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @WithMockUser("new_user")
    public void delete_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/delete")
                        .param("postUri", fourthPostUri))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    @WithMockUser("new_user")
    public void unhide_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/unhide")
                        .param("postUri", fourthPostUri))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @WithMockUser("new_user")
    public void like_WithFourthPost_AsNewUSer_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/like")
                        .param("postUri", fourthPostUri))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @WithMockUser("new_user")
    public void dislike_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/dislike")
                        .param("postUri", fourthPostUri))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @WithMockUser("new_user")
    public void dislike_WithPrivateUsersPost_AsNewUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/post/dislike")
                        .param("postUri", fifthPostUri))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    @WithMockUser("new_user")
    public void undelete_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/undelete")
                        .param("postUri", fourthPostUri))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    @WithMockUser("new_user")
    public void changeTitle_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/changeTitle")
                        .param("postUri", fourthPostUri)
                        .param("newTitle", "New Title"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    @WithMockUser("new_user")
    public void changeContent_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/changeContent")
                        .param("postUri", fourthPostUri)
                        .param("newContent", "this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum, this text has to have 100 characters minimum, new content"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(10)
    @WithMockUser("new_user")
    public void permanentlyDelete_WithFourthPost_AsNewUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/post/permanentlyDelete")
                        .param("postUri", fourthPostUri))
                .andExpect(status().isOk());
    }

    @Test
    @Order(11)
    public void getPost_WithFourthPost_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/post/" + fourthPostUri))
                .andExpect(status().isNotFound());
    }
}
