package server.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Reads the path to the service account JSON from an environment variable.
        // Set FIREBASE_CREDENTIALS in your IntelliJ run configuration locally.
        // On Railway, set it in the environment variables dashboard.
        String credentialsPath = System.getenv("FIREBASE_CREDENTIALS");

        if (credentialsPath == null || credentialsPath.isEmpty()) {
            throw new IllegalStateException(
                "FIREBASE_CREDENTIALS environment variable is not set. " +
                "Point it to your firebase-service-account.json file."
            );
        }

        FileInputStream serviceAccount = new FileInputStream(credentialsPath);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }

        return FirebaseApp.getInstance();
    }
}
