package com.syncit.controller;

import com.syncit.DTO.AddEnrollmentRequestDTO;
import com.syncit.model.Enrollment;
import com.syncit.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {
    @Autowired
    EnrollmentService enrollmentService;

    @GetMapping()
    public ResponseEntity<?> getAllEnrollments(){
        return ResponseEntity.ok(enrollmentService.getAllProjectsByUserId());
    }

    @GetMapping("/sharedWith/{id}")
    public ResponseEntity<?> getSharedWithEnrollment(@PathVariable Long id){
        return ResponseEntity.ok(enrollmentService.sharedWithNumber(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEnrollment(@RequestBody AddEnrollmentRequestDTO requestDTO){
        enrollmentService.addEnrollment(requestDTO);
        return ResponseEntity.ok("User added");
    }
}
