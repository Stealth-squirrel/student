package com.example.student.service;

import com.example.student.dto.user.UserRequest;
import com.example.student.dto.user.UserResponse;
import com.example.student.dto.user.UserResponseList;
import com.example.student.exception.EmailAlreadyExistsException;
import com.example.student.exception.EntityNotFoundException;
import com.example.student.mapper.UserMapper;
import com.example.student.model.Role;
import com.example.student.model.User;
import com.example.student.repository.UserRepository;
import com.example.student.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserServiceImpl userService;

    @Test
    void existsByEmail_shouldReturnBoolean() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);
        assertTrue(userService.existsByEmail("test@test.com"));
    }

    @Test
    void register_shouldReturnFalseIfEmailExists() {
        UserRequest request = new UserRequest("test@test.com", "123", Set.of(Role.ADMIN));

        User mockUser = new User();
        mockUser.setEmail("test@test.com");

        when(userMapper.toEntity(request)).thenReturn(mockUser);
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertFalse(userService.register(request));
    }

    @Test
    void register_shouldReturnTrueAndSave() {
        UserRequest request = new UserRequest("test@test.com", "123", Set.of(Role.ADMIN));
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("123");

        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("hashed");

        assertTrue(userService.register(request));
        assertEquals("hashed", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void findByEmail_shouldThrowExceptionIfNotFound() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findByEmail("test@test.com"));
    }

    @Test
    void save_shouldThrowExceptionIfEmailExists() {
        UserRequest request = new UserRequest("test@test.com", "123", Set.of(Role.ADMIN));
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.save(request));
    }

    @Test
    void save_shouldEncodePasswordAndSave() {
        UserRequest request = new UserRequest("test@test.com", "123", Set.of(Role.ADMIN));
        User user = new User();

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(any())).thenReturn(new UserResponse());

        userService.save(request);
        verify(passwordEncoder).encode("123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateRolesAndReturnNull() {
        UserRequest request = new UserRequest("test@test.com", "123", Set.of(Role.SUPER_ADMIN));
        User existingUser = new User();
        existingUser.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(existingUser));

        UserResponse response = userService.updateUser(request);

        assertNull(response);
        assertTrue(existingUser.getRoles().contains(Role.SUPER_ADMIN));
    }

    @Test
    void findAll_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(new User()));
        when(userMapper.toListResponse(any())).thenReturn(new UserResponseList());
        assertNotNull(userService.findAll());
    }

    @Test
    void deleteById_shouldCallRepository() {
        userService.deleteById(1L);
        verify(userRepository).deleteById(1L);
    }
}