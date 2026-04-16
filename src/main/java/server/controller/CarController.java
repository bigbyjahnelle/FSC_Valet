package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.model.Car;
import server.service.CarService;

import java.util.List;
import java.util.concurrent.ExecutionException;

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
public class CarController
{
    // TODO (Cobin): inject CarService and implement endpoints
    @Autowired
    private CarService carService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Car car)
    {
        try
        {
            String updateTime = carService.registerCar(car);
            return ResponseEntity.ok("Car registered successfully at " + updateTime);

        }catch (InterruptedException | ExecutionException e) {
            // This catches the specific Firestore errors
            return ResponseEntity.status(500).body("Firestore Error: " + e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(500).body("General Error: " + e.getMessage());
        }
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<List<Car>> getCarsByOwner(@PathVariable String ownerId)
    {
        try {
            List<Car> cars = carService.getCarsByOwner(ownerId);
            return ResponseEntity.ok(cars);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
