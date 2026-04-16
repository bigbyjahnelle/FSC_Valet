package server.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;
import server.model.Car;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
        Firestore db = FirestoreClient.getFirestore();

        //Uses the ownerId var to filter the cars collection
        ApiFuture<QuerySnapshot> future = db.collection("cars")
                .whereEqualTo("ownerUid", ownerId)
                .get();

        return future.get().getDocuments().stream()
                .map(doc -> doc.toObject(Car.class))
                .collect(Collectors.toList());

        //Not getting rid of the logic, just trying out the new logic using the ApiFuture with the database
        /*
        List<QueryDocumentSnapshot> docs = firestore.collection(COLLECTION)
                .whereEqualTo("ownerId", ownerId)
                .get()
                .get()
                .getDocuments();

        return docs.stream()
                .map(doc -> doc.toObject(Car.class))
                .toList();
         */
    }

    /**
     * Deletes a car document by carId.
     */
    public boolean deleteCar(String carId, String requesterUid) throws ExecutionException, InterruptedException
    {
        Firestore db = FirestoreClient.getFirestore();

        //Use the license plate to find the exact document to delete
        var docRef = db.collection("cars").document(carId);

        var snapshot = docRef.get().get();

        if(snapshot.exists())
        {
            String owner = snapshot.getString("ownerUid");

            //Deletes if the person that is requesting is the owner
            if(owner != null && owner.equals(requesterUid))
            {
                docRef.delete().get();
                return true;
            }
        }

        return false;


        /*
        firestore.collection(COLLECTION)
                .document(carId)
                .delete()
                .get();
         */
    }

    /*
        Using the license plate as a unique Document to ensure
            there are no duplicate registrations for the same vehicle
    */
    public String registerCar(Car car) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> collectionsApiFuture = db.collection("cars")
                .document(car.getLicensePlate())
                .set(car);

        return collectionsApiFuture.get().getUpdateTime().toString();
    }
}
