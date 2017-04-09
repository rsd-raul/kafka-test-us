package com.mapr.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Consumer {

    // ------------------------- ATTRIBUTES --------------------------

    private static boolean extractionCompleted = false;
    private static int dataCounter = 0, shownCounter = 0;
    private static HashMap<String, VehicleLocations> vehicles = new HashMap<>();

    // ------------------------- CONSTRUCTOR -------------------------

    public static void main(String[] args) throws IOException{

        String uniqueSourceID = args[0].equals("all") ? null : args[0];

        // Create Jackson mapper and Kafka customer
        ObjectMapper mapper = new ObjectMapper();
        KafkaConsumer<String, String> consumer;

        try {
            InputStream props = Resources.getResource("consumer.props").openStream();
            Properties properties = new Properties();
            properties.load(props);
            consumer = new KafkaConsumer<>(properties);
        } catch (IllegalArgumentException | IOException ex) {
            System.out.println("Make sure your have a customer.props in your resources folder");
            return;
        }

        // Subscribe the customer to our desired topics
        consumer.subscribe(Arrays.asList("VehicleLocation", "DataSection"));

        // Keep listening while we still need data
        while (!extractionCompleted) {

            // Read records with a 2s timeout and if anything new is found, process it.
            for (ConsumerRecord<String, String> record : consumer.poll(2000)) {

                if(record.topic().equals("DataSection"))
                    dataCounter++;
                else {
                    JsonNode source = mapper.readTree(record.value());
                    JsonNode location = source.get("body").get("Location");

                    // Extract the necessary values from the json
                    String sourceId = source.get("sourceId").asText();
                    BigDecimal longitude = BigDecimal.valueOf(location.get("longitude").asDouble());
                    BigDecimal latitude = BigDecimal.valueOf(location.get("latitude").asDouble());
                    String timeStamp = location.get("timeStamp").asText();

                    // If the particular vehicles matches our filter, process it
                    if (uniqueSourceID == null || uniqueSourceID.equals(sourceId))
                        checkAndAdd(sourceId, new Location(longitude, latitude, timeStamp));
                }
            }
        }
    }

    // -------------------------- USE CASES --------------------------

    private static void checkAndAdd(String sourceId, Location currentLocation) {

        VehicleLocations vehicleLocations = vehicles.get(sourceId);

        // If there is no vehicle with that id, save a new one finish
        if (vehicleLocations == null) {
            vehicles.put(sourceId, new VehicleLocations(sourceId, currentLocation));
            System.out.println("Vehicle added, id: " + sourceId + " total: " + vehicles.size());
            return;
        }

        /* --------------------------- SOLUTION HANDLING ---------------------------- */

        switch (vehicleLocations.getFinalRouteStatus()){
            //If we haven't found both solutions for a vehicle, reset its vehicle location and increase the radius
            case VehicleLocations.INCOMPLETE:
                if(dataCounter > ThreadLocalRandom.current().nextInt(100, 110 + 1)) {
                    vehicleLocations.reset(currentLocation);
                    dataCounter = 0;
                }
                break;

            // If we have found home and work, add the current location to the final route
            case VehicleLocations.IN_PROGRESS:
                vehicleLocations.addToFinalRoute(currentLocation);
                return;

            // If we have the final route, show it on console
            case VehicleLocations.COMPLETED:
                printCSVFormattedRoute(sourceId, vehicleLocations.getFinalRoute());
                vehicleLocations.setFinalRouteStatus(VehicleLocations.SHOWN);
                break;

            // If we have already shown the solution, ignore this location
            case VehicleLocations.SHOWN:
                return;
        }

        /* ----------------------------- MAIN ALGORITHM ----------------------------- */

        // If the current location is close to the last one
        if(currentLocation.isNearby(vehicleLocations.getLastLocation())){
            System.out.println("Close to last");
            // If there is at least a solution,
            if(vehicleLocations.hasAnySolution()){
                // Check if the current value is close to the solution and can improve it
                vehicleLocations.updateSolutionIfImproved(sourceId, currentLocation);
            }
            // If not, discard the "duplicate".

        // If current location is near the first location we have an edge
        } else if(currentLocation.isNearby(vehicleLocations.getFirstLocation())) {
            System.out.println("Close to first");
            // Save previous location as a result, and if both solutions are set, get the full route from A to B
            vehicleLocations.saveResult(currentLocation);

        // If the current location is not a solution nor is close to the previous location
        } else {
            System.out.println("Close to none");
            // Rotate the values
            vehicleLocations.setLastLocation(currentLocation);
        }
    }

    private static void printCSVFormattedRoute(String sourceId, List<Location> locationList){
        System.out.println("Printing formatted route for vehicle: " + sourceId);
        System.out.println("lat,lon,tim");
        for (Location aux : locationList)
            System.out.print(aux.getLatitude() + "," + aux.getLongitude() + "," + aux.getTimestamp());

        shownCounter++;
        if(shownCounter == 5)
            extractionCompleted = true;
    }
}
