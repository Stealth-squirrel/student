package com.example.student.controller;

import com.example.student.StudentController;
import com.example.student.configuration.SecurityConfiguration;
import com.example.student.dto.*;
import com.example.student.exception.EntityNotFoundException;
import com.example.student.security.JwtService;
import com.example.student.service.StudentService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@Import(SecurityConfiguration.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "TEACHER")
    void findAll_shouldReturnListForTeacher() throws Exception {
        StudentResponseForList student = new StudentResponseForList(1L, "Ivan", "Ivanov");
        StudentResponseList responseList = new StudentResponseList(List.of(student), 1);
        when(studentService.findAll()).thenReturn(responseList);

        mockMvc.perform(get("/api/v1/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].name").value("Ivan"))
                .andExpect(jsonPath("$.totalCount").value(1));
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void findAll_shouldReturn403ForSuperAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/student"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void createStudent_shouldReturn403ForTeacher() throws Exception {
        String json = """
                {
                    "name": "Ivan",
                    "surname": "Petrov",
                    "phone_number": "+7900",
                    "email": "ivan@test.com"
                }
                """;

        mockMvc.perform(post("/api/v1/student/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStudent_shouldReturnCreatedStudent() throws Exception {
        StudentResponse response = new StudentResponse(1L, "Ivan", "Petrov");
        when(studentService.save(any(StudentRequest.class))).thenReturn(response);

        String json = """
                {
                    "name": "Ivan",
                    "surname": "Petrov",
                    "phone_number": "+7900",
                    "email": "ivan@test.com"
                }
                """;

        mockMvc.perform(post("/api/v1/student/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ivan"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_shouldReturnFullStudent() throws Exception {
        StudentFullResponse response = new StudentFullResponse(1L, "Ivan", "Petrov", "+7900", "ivan@test.com");
        when(studentService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("+7900"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_shouldReturn404WhenNotFound() throws Exception {
        when(studentService.findById(99L))
                .thenThrow(new EntityNotFoundException("Student with ID 99 not found!"));

        mockMvc.perform(get("/api/v1/student/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateStudent_shouldReturnUpdatedStudent() throws Exception {
        StudentResponse response = new StudentResponse(1L, "Ivan", "Petrov");
        when(studentService.update(eq(1L), any(StudentUpdateRequest.class))).thenReturn(response);

        String json = """
                {
                    "phoneNumber": "+7999",
                    "email": "new@test.com"
                }
                """;

        mockMvc.perform(put("/api/v1/student/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStudent_shouldReturnOk() throws Exception {
        doNothing().when(studentService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/student/1").with(csrf()))
                .andExpect(status().isOk());
    }
}