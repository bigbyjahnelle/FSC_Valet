package server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.model.Car;
import server.service.CarService;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    // POST /api/cars
    @PostMapping
    public ResponseEntity<Car> addCar(@RequestBody Car car) {
        try {
            Car created = carService.addCar(car);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // GET /api/cars/{ownerId}
    @GetMapping("/{ownerId}")
    public ResponseEntity<List<Car>> getCarsByOwner(@PathVariable String ownerId) {
        try {
            List<Car> cars = carService.getCarsByOwner(ownerId);
            return ResponseEntity.ok(cars);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // DELETE /api/cars/{carId}
    @DeleteMapping("/{carId}")
    public ResponseEntity<String> deleteCar(@PathVariable String carId, String Uid) {
        try {
            carService.deleteCar(carId, Uid);
            return ResponseEntity.ok("Car deleted.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}