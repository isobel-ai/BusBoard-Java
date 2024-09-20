package training.busboard;

public class Bus {
    private String lineName;
    private String destinationName;
    private int timeToStation;

    Bus(String lineName, String destinationName, int timeToStation) {
        this.lineName = lineName;
        this.destinationName = destinationName;
        this.timeToStation = timeToStation / 60;
    }

    public String getLineName() {
        return lineName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public int getTimeToStation() {
        return timeToStation;
    }
}
