package server.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.stereotype.Service;
import server.model.Car;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class CarService {

    private static final String COLLECTION = "cars";

    private final Firestore firestore;

    public CarService(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Adds a new car for a customer.
     * Generates a carId and sets createdAt before saving.
     */
    public Car addCar(Car car) throws ExecutionException, InterruptedException {
        String carId = UUID.randomUUID().toString();
        car.setCarId(carId);
        car.setCreatedAt(new Date());

        firestore.collection(COLLECTION)
                .document(carId)
                .set(car)
                .get();

        return car;
    }

    /**
     * Returns all cars belonging to a customer.
     */
    public List<Car> getCarsByOwner(String ownerId) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> docs = firestore.collection(COLLECTION)
                .whereEqualTo("ownerId", ownerId)
                .get()
                .get()
                .getDocuments();

        return docs.stream()
                .map(doc -> doc.toObject(Car.class))
                .toList();
    }

    /**
     * Deletes a car document by carId.
     */
    public void deleteCar(String carId) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION)
                .document(carId)
                .delete()
                .get();
    }
}
