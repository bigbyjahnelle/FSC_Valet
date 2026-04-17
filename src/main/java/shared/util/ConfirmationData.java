package shared.util;

public class ConfirmationData {

    private static String ticketNumber = "";
    private static String type         = "";
    private static String date         = "";

    public static void set(String ticketNumber, String type, String date) {
        ConfirmationData.ticketNumber = ticketNumber;
        ConfirmationData.type         = type;
        ConfirmationData.date         = date;
    }

    public static String getTicketNumber() { return ticketNumber; }
    public static String getType()         { return type; }
    public static String getDate()         { return date; }

    public static void clear() {
        ticketNumber = ""; type = ""; date = "";
    }
}
