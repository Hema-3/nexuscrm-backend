package com.nexuscrm.backend.controller;

import com.nexuscrm.backend.model.Ticket;
import com.nexuscrm.backend.repository.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    public ResponseEntity<?> getTickets(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Ticket> ticketPage;

        if (search != null && !search.trim().isEmpty()) {
            ticketPage = ticketRepository.searchTickets(search.trim(), pageable);
        } else {
            ticketPage = ticketRepository.findAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", ticketPage.getContent());
        response.put("count", ticketPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public Ticket createTicket(@RequestBody Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @RequestBody Ticket ticketDetails) {
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setTitle(ticketDetails.getTitle());
                    ticket.setStatus(ticketDetails.getStatus());
                    ticket.setPriority(ticketDetails.getPriority());
                    ticket.setCustomerId(ticketDetails.getCustomerId());
                    ticket.setDescription(ticketDetails.getDescription());
                    return ResponseEntity.ok(ticketRepository.save(ticket));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable Long id) {
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticketRepository.delete(ticket);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
