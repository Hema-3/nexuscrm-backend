package com.nexuscrm.backend.controller;

import com.nexuscrm.backend.model.Invoice;
import com.nexuscrm.backend.repository.InvoiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;

    public InvoiceController(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping
    public ResponseEntity<?> getInvoices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Invoice> invoicePage;

        if (search != null && !search.trim().isEmpty()) {
            invoicePage = invoiceRepository.searchInvoices(search.trim(), pageable);
        } else {
            invoicePage = invoiceRepository.findAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", invoicePage.getContent());
        response.put("count", invoicePage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public Invoice createInvoice(@RequestBody Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoiceDetails) {
        return invoiceRepository.findById(id)
                .map(invoice -> {
                    invoice.setInvoiceNumber(invoiceDetails.getInvoiceNumber());
                    invoice.setAmount(invoiceDetails.getAmount());
                    invoice.setStatus(invoiceDetails.getStatus());
                    invoice.setDueDate(invoiceDetails.getDueDate());
                    invoice.setCustomerId(invoiceDetails.getCustomerId());
                    return ResponseEntity.ok(invoiceRepository.save(invoice));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id) {
        return invoiceRepository.findById(id)
                .map(invoice -> {
                    invoiceRepository.delete(invoice);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
