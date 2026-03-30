package server.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;
import server.model.LoginRequest;
import server.model.LoginResponse;

@Service
public class AuthService {

    // TODO COBIN: Firebase login requires the client to authenticate first and send
    // a Firebase ID token. The flow will be:
    // 1. Client uses Firebase Client SDK to sign in with email/password
    // 2. Client gets back a Firebase ID token
    // 3. Client sends that token to this server
    // 4. Server calls FirebaseAuth.getInstance().verifyIdToken(idToken) to validate it
    // 5. Server returns its own session response

    // TODO COBIN: For create account:
    // Call FirebaseAuth.getInstance().createUser(new UserRecord.CreateRequest()
    //     .setEmail(email)
    //     .setPassword(password)
    //     .setDisplayName(firstName + " " + lastName));

    public LoginResponse authenticate(LoginRequest request) {
        // TEMPORARY — hardcoded credentials until Firebase is connected
        if ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword())) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setMessage("Login successful");
            response.setToken("placeholder-jwt-token");
            return response;
        }

        LoginResponse response = new LoginResponse();
        response.setSuccess(false);
        response.setMessage("Invalid username or password");
        return response;
    }
}
