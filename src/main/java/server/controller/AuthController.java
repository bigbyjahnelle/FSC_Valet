package server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.model.LoginRequest;
import server.model.LoginResponse;
import server.model.RegisterRequest;
import server.service.AuthService;
import server.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
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
    public ResponseEntity<String> register(@RequestBody RegisterRequest request)
    {
        try {
            authService.createAccount(
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName()
            );
            return ResponseEntity.ok("User Created Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/update-profile")
    public ResponseEntity<String> updateProfile(@RequestBody server.model.User user) {
        try {
            // This calls the logic you already added to UserService
            userService.updateUserInfo(user.getUid(), user.getFullName(), user.getPhoneNumber());
            return ResponseEntity.ok("Profile updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Door 2: Updates the Password in Firebase Auth
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody java.util.Map<String, String> request) {
        try {
            String uid = request.get("uid");
            String newPassword = request.get("password");

            // Use the Firebase Admin SDK to change the password
            com.google.firebase.auth.UserRecord.UpdateRequest updateRequest =
                    new com.google.firebase.auth.UserRecord.UpdateRequest(uid)
                            .setPassword(newPassword);

            com.google.firebase.auth.FirebaseAuth.getInstance().updateUser(updateRequest);
            return ResponseEntity.ok("Password updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
