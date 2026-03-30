package server.model;

public class DashboardResponse {
    private int activeVehicles;
    private int availableSpots;
    private int totalCapacity;
    private int todayCheckins;

    public DashboardResponse(int activeVehicles, int availableSpots, int totalCapacity, int todayCheckins) {
        this.activeVehicles = activeVehicles;
        this.availableSpots = availableSpots;
        this.totalCapacity = totalCapacity;
        this.todayCheckins = todayCheckins;
    }

    public int getActiveVehicles() { return activeVehicles; }
    public int getAvailableSpots() { return availableSpots; }
    public int getTotalCapacity() { return totalCapacity; }
    public int getTodayCheckins() { return todayCheckins; }
}
