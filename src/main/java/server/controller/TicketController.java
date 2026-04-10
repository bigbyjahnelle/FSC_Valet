package server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO (Cobin): REST endpoints for ticket (valet request) management.
 *
 * Suggested endpoints:
 *   POST  /api/tickets                      — create a new PARK or RETRIEVE request
 *   GET   /api/tickets/customer/{customerId} — get all tickets for a customer
 *   GET   /api/tickets/queue                 — get the pending staff queue (staff only)
 *   PATCH /api/tickets/{ticketId}/status     — update ticket status (claim, complete, cancel)
 *
 * Use TicketService for all Firestore operations.
 * Staff queue endpoint should only be accessible to STAFF and ADMIN roles.
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    // TODO (Cobin): inject TicketService and implement endpoints

}
