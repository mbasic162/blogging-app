package com.example.bloggingapp.test;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class AdminTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(0)
    void contextLoads() {
    }

    @Test
    @Order(1)
    @WithMockUser(value = "admin_user", roles = "ADMIN")
    void disable_WithNewUser_AsAdminUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/admin/disable")
                        .param("username", "new_user"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void getUser_WithNewUser_WhenDisabled_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/user/new_user"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    @WithMockUser(value = "admin_user", roles = "ADMIN")
    void enable_WithNewUser_AsAdminUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/admin/enable")
                        .param("username", "new_user"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    @WithMockUser(value = "admin_user", roles = "ADMIN")
    void deletePost_WithPrivateUsersPost_AsAdminUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/admin/deletePost")
                        .param("postId", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    @WithMockUser(value = "admin_user", roles = "ADMIN")
    void deletePost_WithFirstPost_AsAdminUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/admin/deletePost")
                        .param("postId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void getPost_WithFirstPost_WhenDeleted_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/post/first-users-post-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    @WithMockUser(value = "admin_user", roles = "ADMIN")
    void undeletePost_WithFirstPost_WhenDeleted_AsAdminUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/admin/undeletePost")
                        .param("postId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    @WithMockUser(value = "admin_user", roles = "ADMIN")
    void deleteComment_WithFirstComment_AsAdminUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/admin/deleteComment")
                        .param("commentId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void getComment_WithFirstComment_WhenDeleted_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/comment/comment-on-post-1-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    @WithMockUser(value = "admin_user", roles = "ADMIN")
    void undeleteComment_WithFirstComment_WhenDeleted_AsAdminUser_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/admin/undeleteComment")
                        .param("commentId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    void getComment_WithFirstComment_ShouldReturnFirstCommentDto() throws Exception {
        mockMvc.perform(get("/comment/comment-on-post-1-1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    void disable_WithFirstUser_WithoutPermissions_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/admin/disable")
                        .param("username", "first_user"))
                .andExpect(status().isForbidden());
    }
}
