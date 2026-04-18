package server.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;
import server.model.LoginRequest;
import server.model.LoginResponse;
import server.model.User;

import java.util.Date;

@Service
public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public void createAccount(String email, String password, String firstName, String lastName) throws Exception
    {
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setDisplayName(firstName + " " + lastName);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);
        System.out.println("Successfully Created New User: " + userRecord.getUid());

        // Save user profile to Firestore
        User user = new User();
        user.setUid(userRecord.getUid());
        user.setEmail(email);
        user.setFullName(firstName + " " + lastName);
        user.setRole("CUSTOMER");
        user.setCreatedAt(new Date());
        userService.saveUser(user);
    }

    public LoginResponse authenticate(LoginRequest request) {
        LoginResponse response = new LoginResponse();

        try {
            //This is where it will validate the token from the client
            String idToken = request.getToken();
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            //Then gets the users details if it succeeded
            String uId = decodedToken.getUid();
            String email = decodedToken.getEmail();

            UserRecord userRecord = FirebaseAuth.getInstance().getUser(uId);
            String fullName = userRecord.getDisplayName();
            if (fullName == null) fullName = "";

            response.setSuccess(true);
            response.setMessage("Login Successful For: " + email);
            response.setToken(idToken);

            response.setFullName(fullName);

            return response;
        } catch (FirebaseAuthException e) {
            //This is if the token was invalid/expired/revoked
            response.setSuccess(false);
            response.setMessage("Authentication Failed: " + e.getMessage());

            return response;
        }
    }
}
