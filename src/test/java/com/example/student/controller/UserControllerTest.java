package com.example.student.controller;

import com.example.student.UserController;
import com.example.student.configuration.SecurityConfiguration;
import com.example.student.dto.user.UserRequest;
import com.example.student.dto.user.UserResponse;
import com.example.student.dto.user.UserResponseList;
import com.example.student.model.Role;
import com.example.student.security.JwtService;
import com.example.student.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfiguration.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void getAllUsers_shouldReturnList() throws Exception {
        UserResponseList responseList = new UserResponseList();
        responseList.setResponses(List.of(new UserResponse(1L, "test@test.com", Set.of(Role.ADMIN))));
        responseList.setTotalCount(1);

        when(userService.findAll()).thenReturn(responseList);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.responses[0].email").value("test@test.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_shouldReturn403ForAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void saveUser_shouldReturnCreatedUser() throws Exception {
        UserResponse response = new UserResponse(1L, "new@test.com", Set.of(Role.TEACHER));
        when(userService.save(any(UserRequest.class))).thenReturn(response);

        String json = """
                {
                    "email": "new@test.com",
                    "password": "password123",
                    "roles": ["TEACHER"]
                }
                """;

        mockMvc.perform(post("/api/v1/users/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void deleteUser_shouldReturnOk() throws Exception {
        doNothing().when(userService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/users/1").with(csrf()))
                .andExpect(status().isOk());
    }
}