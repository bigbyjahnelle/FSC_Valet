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

    public static void setFirstName(String firstName)
    {
        SessionManager.firstName = firstName;
    }

    /*
        TODO Need to make it not only display "User" on the dashboard.
             We might need to refactor how the firstName is gotten, but we need to discuss
    */
    public static String getFirstName() { return (firstName != null && !firstName.isEmpty()) ? firstName : "User"; }
    public static String getFullName()  { return fullName;  }
    public static String getUid()       { return uid;       }
    public static String getEmail()     { return email;     }

    public static void clear() {
        uid = ""; email = ""; fullName = ""; firstName = "";
    }
}
