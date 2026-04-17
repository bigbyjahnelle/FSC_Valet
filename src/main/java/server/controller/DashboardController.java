package server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.model.DashboardResponse;
import server.model.Ticket;
import server.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final int TOTAL_CAPACITY = 50;

    private final TicketService ticketService;

    public DashboardController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        try {
            List<Ticket> pending    = ticketService.getPendingQueue();
            List<Ticket> inProgress = ticketService.getTicketsByStatus("IN_PROGRESS");

            int activeVehicles = pending.size() + inProgress.size();
            int availableSpots = TOTAL_CAPACITY - activeVehicles;
            int todayCheckins  = ticketService.getTodayCheckinCount();

            DashboardResponse response = new DashboardResponse(
                    activeVehicles,
                    Math.max(availableSpots, 0),
                    TOTAL_CAPACITY,
                    todayCheckins
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Fall back to zeros if Firestore is unavailable
            return ResponseEntity.ok(new DashboardResponse(0, TOTAL_CAPACITY, TOTAL_CAPACITY, 0));
        }
    }
}
