package server.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;
import server.model.LoginRequest;
import server.model.LoginResponse;

@Service
public class AuthService {
    public void createAccount(String email, String password, String firstName, String lastName) throws FirebaseAuthException
    {
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setDisplayName(firstName + " " + lastName);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);
        System.out.println("Successfully Created New User: " + userRecord.getUid());
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

            response.setSuccess(true);
            response.setMessage("Login Successful For: " + email);
            response.setToken(idToken);

            return response;
        } catch (FirebaseAuthException e) {
            //This is if the token was invalid/expired/revoked
            response.setSuccess(false);
            response.setMessage("Authentication Failed: " + e.getMessage());

            return response;
        }
    }
}
