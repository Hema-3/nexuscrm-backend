package com.nexuscrm.backend.controller;

import com.nexuscrm.backend.repository.CustomerRepository;
import com.nexuscrm.backend.repository.InvoiceRepository;
import com.nexuscrm.backend.repository.LeadRepository;
import com.nexuscrm.backend.model.Invoice;
import com.nexuscrm.backend.model.Lead;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final LeadRepository leadRepository;

    public DashboardController(CustomerRepository customerRepository, InvoiceRepository invoiceRepository, LeadRepository leadRepository) {
        this.customerRepository = customerRepository;
        this.invoiceRepository = invoiceRepository;
        this.leadRepository = leadRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        long totalCustomers = customerRepository.count();
        
        List<Invoice> invoices = invoiceRepository.findAll();
        double totalRevenue = invoices.stream()
                .mapToDouble(Invoice::getAmount)
                .sum();
                
        long activeLeads = leadRepository.findAll().stream()
                .filter(l -> !"Lost".equalsIgnoreCase(l.getStatus()))
                .count();
                
        List<Lead> recentLeads = leadRepository.findAll(PageRequest.of(0, 5, Sort.by("createdAt").descending())).getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("totalCustomers", totalCustomers);
        response.put("totalRevenue", totalRevenue);
        response.put("activeLeads", activeLeads);
        response.put("recentLeads", recentLeads);

        return ResponseEntity.ok(response);
    }
}
