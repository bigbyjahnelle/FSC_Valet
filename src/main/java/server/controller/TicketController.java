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
    public ResponseEntity<String> getTicketsByCustomer(@PathVariable String customerId) {
        try {
            List<Ticket> tickets = ticketService.getTicketsByCustomer(customerId);

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < tickets.size(); i++) {
                Ticket t = tickets.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                        .append("\"ticketId\":\"").append(t.getTicketId()).append("\",")
                        .append("\"ticketNumber\":\"").append(t.getTicketNumber()).append("\",")
                        .append("\"customerId\":\"").append(t.getCustomerId()).append("\",")
                        .append("\"carId\":\"").append(t.getCarId()).append("\",")
                        .append("\"type\":\"").append(t.getType()).append("\",")
                        .append("\"status\":\"").append(t.getStatus()).append("\"")
                        .append("}");
            }
            json.append("]");

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(json.toString());

        } catch (Exception e) {
            System.out.println("ERROR in getTicketsByCustomer: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/customer/{customerId}/details")
    public ResponseEntity<String> getTicketsWithCar(@PathVariable String customerId) {
        try {
            List<server.model.TicketWithCar> tickets = ticketService.getTicketsWithCarByCustomer(customerId);

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < tickets.size(); i++) {
                server.model.TicketWithCar t = tickets.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                        .append("\"ticketNumber\":\"").append(t.getTicketNumber()).append("\",")
                        .append("\"type\":\"").append(t.getType()).append("\",")
                        .append("\"status\":\"").append(t.getStatus()).append("\",")
                        .append("\"make\":\"").append(t.getMake() != null ? t.getMake() : "").append("\",")
                        .append("\"model\":\"").append(t.getModel() != null ? t.getModel() : "").append("\",")
                        .append("\"year\":").append(t.getYear()).append(",")
                        .append("\"color\":\"").append(t.getColor() != null ? t.getColor() : "").append("\",")
                        .append("\"licensePlate\":\"").append(t.getLicensePlate() != null ? t.getLicensePlate() : "").append("\"")
                        .append("}");
            }
            json.append("]");

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(json.toString());

        } catch (Exception e) {
            System.out.println("ERROR in getTicketsWithCar: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
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
