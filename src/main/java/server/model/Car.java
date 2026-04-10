package server.model;

import java.util.Date;

/**
 * Firestore document: cars/{carId}
 * A customer can have multiple cars.
 */
public class Car {
    private String carId;
    private String ownerId;     // ref → users/{uid}
    private String make;
    private String model;
    private int year;
    private String color;
    private String licensePlate;
    private Date createdAt;

    public Car() {}

    public String getCarId() { return carId; }
    public void setCarId(String carId) { this.carId = carId; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
