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

    public TicketWithCar() {}

    public TicketWithCar(Ticket ticket, Car car)
    {
        this.ticketId     = ticket.getTicketId();
        this.ticketNumber = ticket.getTicketNumber();
        this.customerId   = ticket.getCustomerId();
        this.carId        = ticket.getCarId();
        this.type         = ticket.getType();
        this.status       = ticket.getStatus();
        this.archived     = ticket.isArchived();
        this.notes        = ticket.getNotes();
        this.createdAt    = ticket.getCreatedAt();
        this.completedAt  = ticket.getCompletedAt();

        if (car != null)
        {
            this.make         = car.getMake();
            this.model        = car.getModel();
            this.year         = car.getYear();
            this.color        = car.getColor();
            this.licensePlate = car.getLicensePlate();
        }
    }
}
