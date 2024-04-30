package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBHelpRequest;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBHelpRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "UCSBHelpRequest")
@RequestMapping("/api/ucsbhelprequest")
@RestController
@Slf4j
public class UCSBHelpRequestController extends ApiController {

    @Autowired
    UCSBHelpRequestRepository ucsbHelpRequestRepository;

    // Get all records in the table and return as a JSON array
    @Operation(summary= "List all ucsb help requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBHelpRequest> allUCSBDates() {
        Iterable<UCSBHelpRequest> helpRequests = ucsbHelpRequestRepository.findAll();
        return helpRequests;
    }

    // Use the data in the input parameters to create a new row in the table and return the data as JSON
    @Operation(summary= "Create a new help request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBHelpRequest postUCSBHelpRequest(
            @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
            @Parameter(name="teamID") @RequestParam String teamID,
            @Parameter(name="tableOrBreakoutRoom") @RequestParam String tableOrBreakoutRoom,
            @Parameter(name="requestTime") @RequestParam("requestTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestTime,
            @Parameter(name="explanation") @RequestParam String explanation,
            @Parameter(name="solved") @RequestParam Boolean solved)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("localDateTime={}", requestTime);

        UCSBHelpRequest ucsbHelpRequest = new UCSBHelpRequest();
        ucsbHelpRequest.setRequesterEmail(requesterEmail);
        ucsbHelpRequest.setTeamID(teamID);
        ucsbHelpRequest.setTableOrBreakoutRoom(tableOrBreakoutRoom);
        ucsbHelpRequest.setRequestTime(requestTime);
        ucsbHelpRequest.setExplanation(explanation);
        ucsbHelpRequest.setSolved(solved);

        UCSBHelpRequest savedUcsbHelpRequest = ucsbHelpRequestRepository.save(ucsbHelpRequest);

        return savedUcsbHelpRequest;
    }
}
