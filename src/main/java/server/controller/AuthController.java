package server.controller;

import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.model.LoginRequest;
import server.model.LoginResponse;
import server.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.authenticate(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body(response);
    }

    //Method to help create the account on the createAccount page
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequest request)
    {
        try {
            /*
                Calls the createAccount method in AuthService that communicates to the firebase
                    to hold the information of the new account that was created
            */
            authService.createAccount(request.getUsername(), request.getPassword(), "First", "Last");

            return ResponseEntity.ok("User Created Successfully");
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
    }
}
