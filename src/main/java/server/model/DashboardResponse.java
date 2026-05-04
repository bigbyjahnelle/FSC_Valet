package server.model;

import java.util.List;

public class DashboardResponse {
    private int activeVehicles;
    private int availableSpots;
    private int totalCapacity;
    private int todayCheckins;
    private List<Ticket> recentTickets;

    public DashboardResponse(int activeVehicles, int availableSpots, int totalCapacity,
                             int todayCheckins, List<Ticket> recentTickets) {
        this.activeVehicles  = activeVehicles;
        this.availableSpots  = availableSpots;
        this.totalCapacity   = totalCapacity;
        this.todayCheckins   = todayCheckins;
        this.recentTickets   = recentTickets;
    }

    public int getActiveVehicles()        { return activeVehicles; }
    public int getAvailableSpots()        { return availableSpots; }
    public int getTotalCapacity()         { return totalCapacity; }
    public int getTodayCheckins()         { return todayCheckins; }
    public List<Ticket> getRecentTickets(){ return recentTickets; }
}
