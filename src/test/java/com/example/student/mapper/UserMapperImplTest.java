package com.example.student.mapper;

import com.example.student.dto.user.UserRequest;
import com.example.student.dto.user.UserResponse;
import com.example.student.dto.user.UserResponseList;
import com.example.student.model.Role;
import com.example.student.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperImplTest {

    private final UserMapperImpl mapper = new UserMapperImpl();

    @Test
    void toResponse_shouldMapUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setRoles(Set.of(Role.ADMIN));

        UserResponse response = mapper.toResponse(user);

        assertEquals(1L, response.getId());
        assertEquals("test@test.com", response.getEmail());
        assertTrue(response.getRoles().contains(Role.ADMIN));
    }

    @Test
    void toEntity_shouldMapRequest() {
        UserRequest request = new UserRequest("test@test.com", "pass", Set.of(Role.ADMIN));
        User user = mapper.toEntity(request);

        assertEquals("test@test.com", user.getEmail());
        assertEquals("pass", user.getPassword());
        assertTrue(user.getRoles().contains(Role.ADMIN));
    }

    @Test
    void toListResponse_shouldWrapList() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        UserResponseList result = mapper.toListResponse(java.util.List.of(user));

        assertEquals(1, result.getTotalCount());
        assertEquals(1, result.getResponses().size());
    }}