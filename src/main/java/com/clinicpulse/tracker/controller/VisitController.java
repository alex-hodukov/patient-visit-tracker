package com.clinicpulse.tracker.controller;

import com.clinicpulse.tracker.dto.CreateVisitRequest;
import com.clinicpulse.tracker.dto.CreateVisitResponse;
import com.clinicpulse.tracker.service.VisitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/visits")
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateVisitResponse createVisit(@Valid @RequestBody CreateVisitRequest request) {
        return visitService.createVisit(request);
    }
}
