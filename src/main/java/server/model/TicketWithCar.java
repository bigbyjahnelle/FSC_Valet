package server.model;

import java.util.Date;

public class TicketWithCar
{
    private String ticketId;
    private String ticketNumber;
    private String customerId;
    private String carId;
    private String type;
    private String status;
    private boolean archived;
    private String notes;
    private Date createdAt;
    private Date completedAt;

    // Car fields embedded directly
    private String make;
    private String model;
    private int year;
    private String color;
    private String licensePlate;
}
