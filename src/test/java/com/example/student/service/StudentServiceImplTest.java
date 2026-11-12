package com.example.student.service;

import com.example.student.dto.*;
import com.example.student.exception.EntityNotFoundException;
import com.example.student.mapper.StudentMapper;
import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import com.example.student.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    void findById_shouldReturnStudent() {
        Student student = new Student();
        StudentFullResponse response = new StudentFullResponse(1L, "Ivan", "Ivanov", "+7900", "ivan@test.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.studentToFullResponse(student)).thenReturn(response);

        StudentFullResponse result = studentService.findById(1L);

        assertNotNull(result);
        assertEquals("Ivan", result.getName());
    }

    @Test
    void findById_shouldThrowExceptionWhenNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> studentService.findById(99L));
    }

    @Test
    void update_shouldUpdatePhoneAndEmail() {
        Student student = new Student();
        student.setPhoneNumber("+7111");
        StudentUpdateRequest request = new StudentUpdateRequest("+7222", "new@test.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        studentService.update(1L, request);

        assertEquals("+7222", student.getPhoneNumber());
        assertEquals("new@test.com", student.getEmail());
        verify(studentRepository).save(student);
    }

    @Test
    void deleteById_shouldCallRepository() {
        studentService.deleteById(1L);
        verify(studentRepository).deleteById(1L);
    }
}