package shared.util;

public class TicketEditData {

    private static String ticketId    = "";
    private static String ticketNumber = "";
    private static String type        = "";
    private static String status      = "";
    private static String customerId  = "";
    private static String customerName = "";
    private static String parkingSpace = "";
    private static String notes       = "";

    public static void set(String ticketId, String ticketNumber, String type,
                           String status, String customerId, String customerName,
                           String parkingSpace, String notes) {
        TicketEditData.ticketId     = orEmpty(ticketId);
        TicketEditData.ticketNumber = orEmpty(ticketNumber);
        TicketEditData.type         = orEmpty(type);
        TicketEditData.status       = orEmpty(status);
        TicketEditData.customerId   = orEmpty(customerId);
        TicketEditData.customerName = orEmpty(customerName);
        TicketEditData.parkingSpace = orEmpty(parkingSpace);
        TicketEditData.notes        = orEmpty(notes);
    }

    public static String getTicketId()     { return ticketId; }
    public static String getTicketNumber() { return ticketNumber; }
    public static String getType()         { return type; }
    public static String getStatus()       { return status; }
    public static String getCustomerId()   { return customerId; }
    public static String getCustomerName() { return customerName; }
    public static String getParkingSpace() { return parkingSpace; }
    public static String getNotes()        { return notes; }

    public static void clear() {
        ticketId = ""; ticketNumber = ""; type = ""; status = "";
        customerId = ""; customerName = ""; parkingSpace = ""; notes = "";
    }

    private static String orEmpty(String s) { return s != null ? s : ""; }
}
