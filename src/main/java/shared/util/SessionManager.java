package shared.util;

/**
 * Holds data for the currently logged-in user.
 * Populated after a successful Firebase login and read by any screen that needs it.
 */
public class SessionManager {

    private static String fullName  = "";
    private static String firstName = "";
    private static String uid       = "";
    private static String email     = "";

    public static void setUser(String uid, String email, String fullName) {
        SessionManager.uid      = uid;
        SessionManager.email    = email;
        SessionManager.fullName = fullName;
        SessionManager.firstName = fullName.contains(" ")
                ? fullName.substring(0, fullName.indexOf(" "))
                : fullName;
    }

    public static String getFirstName() { return firstName; }
    public static String getFullName()  { return fullName;  }
    public static String getUid()       { return uid;       }
    public static String getEmail()     { return email;     }

    public static void clear() {
        uid = ""; email = ""; fullName = ""; firstName = "";
    }
}
