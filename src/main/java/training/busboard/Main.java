package training.busboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
    public static void main(String args[]) {
        try {
            String postcode = readPostcode();
            Location location = getPostcodeLocation(postcode);
            List<BusStop> closestBusStops = getClosestBusStops(location);
            int busStopCounter = 0;
            for (BusStop busStop : closestBusStops) {
                if (busStopCounter++ == 2) {
                    break;
                }
                List<Bus> nextBuses = getNextBuses(busStop.getStopCode());
                printNextFiveBuses(busStop, nextBuses);
                System.out.println();
            }
        } catch (IOException | URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static String readPostcode() throws IOException {
        System.out.print("Enter postcode: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String postcode = br.readLine();
        br.close();
        return postcode;
    }

    private static String getAPIResponse(String url) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(new URI(url)).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }

    private static Location getPostcodeLocation(String postcode)
            throws URISyntaxException, IOException, InterruptedException {
        String responseString = getAPIResponse("https://api.postcodes.io/postcodes/{postcode}"
                .replace("{postcode}", postcode).replace(" ", "%20"));
        JSONObject postcodeData = new JSONObject(responseString).getJSONObject("result");
        return new Location(postcodeData.getFloat("latitude"), postcodeData.getFloat("longitude"));
    }

    private static List<BusStop> getClosestBusStops(Location location)
            throws URISyntaxException, IOException, InterruptedException {
        String responseString = getAPIResponse(("https://api.tfl.gov.uk/StopPoint/?lat={lat}&lon={lon}&"
                + "stopTypes=NaptanBusCoachStation,%20NaptanBusWayPoint,%20NaptanOnstreetBusCoachStopCluster,"
                + "%20NaptanOnstreetBusCoachStopPair,%20NaptanPrivateBusCoachTram,%20NaptanPublicBusCoachTram")
                .replace("{lat}", location.getLatitude().toString())
                .replace("{lon}", location.getLongitude().toString()));
        JSONArray closestBusStopsData = new JSONObject(responseString).getJSONArray("stopPoints");

        List<BusStop> closestBusStops = new LinkedList<>();
        closestBusStopsData.forEach(busStop -> {
            JSONObject busStopData = (JSONObject) busStop;
            closestBusStops.add(new BusStop(busStopData.getString("id"), busStopData.getString("commonName")));
        });
        return closestBusStops;
    }

    private static List<Bus> getNextBuses(String stopCode)
            throws URISyntaxException, IOException, InterruptedException {
        String responseString = getAPIResponse(
                "https://api.tfl.gov.uk/StopPoint/{id}/Arrivals".replace("{id}", stopCode));
        JSONArray nextBusesData = new JSONArray(responseString);

        List<Bus> nextBuses = new LinkedList<>();
        nextBusesData.forEach(bus -> {
            JSONObject busData = (JSONObject) bus;
            nextBuses.add(new Bus(busData.getString("lineName"), busData.getString("destinationName"),
                    busData.getInt("timeToStation")));
        });
        return nextBuses;
    }

    private static void printNextFiveBuses(BusStop busStop, List<Bus> buses) {
        System.out.println(String.format("Next 5 Buses at %s:", busStop.getName()));
        int busCounter = 0;
        for (Bus bus : buses) {
            if (busCounter++ == 5) {
                break;
            }
            System.out.println(
                    String.format("%s to %s in %d minute%s",
                            bus.getLineName(),
                            bus.getDestinationName(),
                            bus.getTimeToStation(),
                            bus.getTimeToStation() == 1 ? "" : "s"));
        }
    }
}