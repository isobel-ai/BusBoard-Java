package training.busboard;

public class BusStop {
    private String stopCode;
    private String name;

    BusStop(String stopCode, String name) {
        this.stopCode = stopCode;
        this.name = name;
    }

    public String getStopCode() {
        return stopCode;
    }

    public String getName() {
        return name;
    }
}
