package edu.ucsb.cs156.example.controllers;


import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.entities.UCSBRecommendationRequest;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBRecommendationRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import liquibase.pro.packaged.as;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.time.LocalDateTime;

@Tag(name = "UCSBRecommendationRequest")
@RequestMapping("/api/ucsbrecommendationrequest")
@RestController
@Slf4j

public class UCSBRecommendationRequestController {
    @Autowired
    UCSBRecommendationRequestRepository ucsbRecommendationRequestRepository;

    @Operation(summary= "List all ucsb recommendation requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBRecommendationRequest> allUCSBRecommendationRequests() {
        Iterable<UCSBRecommendationRequest> recommendation_requests = ucsbRecommendationRequestRepository.findAll();
        return recommendation_requests;
    }
    

    @Operation(summary= "Get a single recommendation request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBRecommendationRequest getById(
            @Parameter(name="id") @RequestParam Long id) {
                UCSBRecommendationRequest ucsbRecommendationRequest = ucsbRecommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBRecommendationRequest.class, id));

        return ucsbRecommendationRequest;
    }

    @Operation(summary= "Create a new recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBRecommendationRequest postUCSBRecommendationRequest
    (
        @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
        @Parameter(name="professorEmail") @RequestParam String professorEmail,
        @Parameter(name="explanation") @RequestParam String explanation,
        @Parameter(name = "dateRequested") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateRequested,
        @Parameter(name = "dateNeeded") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateNeeded,
        @Parameter(name = "done") @RequestParam boolean done)  
        
        throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters


        UCSBRecommendationRequest ucsbRecommendationRequest = new UCSBRecommendationRequest();
        ucsbRecommendationRequest.setRequesterEmail(requesterEmail);
        ucsbRecommendationRequest.setProfessorEmail(professorEmail);
        ucsbRecommendationRequest.setDateNeeded(dateNeeded);
        ucsbRecommendationRequest.setDateRequested(dateRequested);
        ucsbRecommendationRequest.setDone(done);

        UCSBRecommendationRequest savedUcsbRecommendationRequest = ucsbRecommendationRequestRepository.save(ucsbRecommendationRequest);

        return savedUcsbRecommendationRequest;
    }



}
