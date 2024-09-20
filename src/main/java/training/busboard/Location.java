package training.busboard;

public class Location {
    private Float latitude;
    private Float longitude;

    Location(Float latitude, Float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }
}
