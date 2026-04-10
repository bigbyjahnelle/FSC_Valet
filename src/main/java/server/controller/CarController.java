package server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO (Cobin): REST endpoints for car management.
 *
 * Suggested endpoints:
 *   POST   /api/cars           — add a new car for the logged-in customer
 *   GET    /api/cars/{ownerId} — get all cars belonging to a customer
 *   DELETE /api/cars/{carId}   — remove a car
 *
 * Use CarService for all Firestore operations.
 * Validate that the requesting user owns the car before allowing delete.
 */
@RestController
@RequestMapping("/api/cars")
public class CarController {

    // TODO (Cobin): inject CarService and implement endpoints

}
