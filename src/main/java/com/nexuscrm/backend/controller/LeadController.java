package com.nexuscrm.backend.controller;

import com.nexuscrm.backend.model.Lead;
import com.nexuscrm.backend.repository.LeadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadRepository leadRepository;

    public LeadController(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    @GetMapping
    public ResponseEntity<?> getLeads(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Lead> leadPage;

        if (search != null && !search.trim().isEmpty()) {
            leadPage = leadRepository.searchLeads(search.trim(), pageable);
        } else {
            leadPage = leadRepository.findAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", leadPage.getContent());
        response.put("count", leadPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public Lead createLead(@RequestBody Lead lead) {
        return leadRepository.save(lead);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lead> updateLead(@PathVariable Long id, @RequestBody Lead leadDetails) {
        return leadRepository.findById(id)
                .map(lead -> {
                    lead.setName(leadDetails.getName());
                    lead.setValue(leadDetails.getValue());
                    lead.setStatus(leadDetails.getStatus());
                    lead.setCustomerId(leadDetails.getCustomerId());
                    return ResponseEntity.ok(leadRepository.save(lead));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLead(@PathVariable Long id) {
        return leadRepository.findById(id)
                .map(lead -> {
                    leadRepository.delete(lead);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
