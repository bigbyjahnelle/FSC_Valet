package server.service;

import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Service;
import server.model.User;

import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private static final String COLLECTION = "users";

    private final Firestore firestore;

    public UserService(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Saves a new user document to Firestore under users/{uid}.
     * Called after Firebase Auth account creation.
     */
    public void saveUser(User user) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION)
                .document(user.getUid())
                .set(user)
                .get();
    }

    /**
     * Retrieves a user document by Firebase Auth UID.
     * Returns null if no document exists.
     */
    public User getUserById(String uid) throws ExecutionException, InterruptedException {
        return firestore.collection(COLLECTION)
                .document(uid)
                .get()
                .get()
                .toObject(User.class);
    }

    public void updateUserInfo(String uid, String name, String phone) throws Exception {
        firestore.collection("users")
                .document(uid)
                .update("fullName", name, "phone", phone)
                .get();
    }
}
