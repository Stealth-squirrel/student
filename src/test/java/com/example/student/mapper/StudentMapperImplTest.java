package com.example.student.mapper;

import com.example.student.dto.StudentFullResponse;
import com.example.student.dto.StudentRequest;
import com.example.student.dto.StudentResponse;
import com.example.student.model.Student;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentMapperImplTest {
    private final StudentMapperImpl mapper = new StudentMapperImpl();

    @Test
    void requestToStudent_shouldMapCorrectly() {
        StudentRequest request = new StudentRequest("Ivan", "Ivanov", "+7900", "ivan@test.com");
        Student result = mapper.requestToStudent(request);

        assertEquals("Ivan", result.getName());
        assertEquals("Ivanov", result.getSurname());
        assertEquals("+7900", result.getPhoneNumber());
        assertEquals("ivan@test.com", result.getEmail());
    }

    @Test
    void studentToFullResponse_shouldMapCorrectly() {
        Student student = new Student();
        student.setId(1L);
        student.setName("Petr");

        StudentFullResponse result = mapper.studentToFullResponse(student);

        assertEquals(1L, result.getId());
        assertEquals("Petr", result.getName());
    }

    @Test
    void studentToResponse_shouldMapCorrectly() {
        Student student = new Student();
        student.setId(2L);
        StudentResponse result = mapper.studentToResponse(student);
        assertEquals(2L, result.getId());
    }
}