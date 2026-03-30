package server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.model.DashboardResponse;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final int TOTAL_CAPACITY = 50;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        // TODO: replace with real data from Firebase/database
        DashboardResponse response = new DashboardResponse(0, 50, TOTAL_CAPACITY, 0);
        return ResponseEntity.ok(response);
    }
}
