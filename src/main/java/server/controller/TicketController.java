package server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.model.Ticket;
import server.service.TicketService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // POST /api/tickets
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        try {
            Ticket created = ticketService.createTicket(ticket);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // GET /api/tickets/customer/{customerId}
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Ticket>> getTicketsByCustomer(@PathVariable String customerId) {
        try {
            List<Ticket> tickets = ticketService.getTicketsByCustomer(customerId);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // GET /api/tickets/queue  (staff only)
    @GetMapping("/queue")
    public ResponseEntity<List<Ticket>> getPendingQueue() {
        try {
            List<Ticket> queue = ticketService.getPendingQueue();
            return ResponseEntity.ok(queue);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // PATCH /api/tickets/{ticketId}/status
    // Request body: { "status": "IN_PROGRESS", "staffId": "uid123" }
    // staffId is optional — only needed when claiming a ticket
    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable String ticketId,
            @RequestBody Map<String, String> body) {
        try {
            String status  = body.get("status");
            String staffId = body.get("staffId"); // may be null
            ticketService.updateStatus(ticketId, status, staffId);
            return ResponseEntity.ok("Status updated.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
