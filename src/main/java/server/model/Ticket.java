package server.model;

import java.util.Date;

/**
 * Firestore document: tickets/{ticketId}
 *
 * type:   PARK     - customer requests car be parked by staff
 *         RETRIEVE - customer requests car be brought to the pickup zone
 *
 * status: PENDING     - waiting in queue (ordered by createdAt ASC)
 *         IN_PROGRESS - claimed by a staff member
 *         COMPLETED   - car parked or delivered successfully
 *         CANCELLED   - request cancelled
 */
public class Ticket {
    private String ticketId;
    private String ticketNumber;    // human-readable e.g. "V000001"
    private String customerId;      // ref → users/{uid}
    private String carId;           // ref → cars/{carId}
    private String type;            // PARK, RETRIEVE
    private String status;          // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private String assignedStaffId; // ref → users/{uid}, null until staff claims ticket
    private boolean archived;       // true when COMPLETED or CANCELLED
    private String notes;           // optional
    private Date createdAt;
    private Date completedAt;       // null until resolved

    public Ticket() {}

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCarId() { return carId; }
    public void setCarId(String carId) { this.carId = carId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(String assignedStaffId) { this.assignedStaffId = assignedStaffId; }

    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getCompletedAt() { return completedAt; }
    public void setCompletedAt(Date completedAt) { this.completedAt = completedAt; }
}
