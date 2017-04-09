package com.mapr.examples;

import java.util.ArrayList;
import java.util.List;

class VehicleLocations {

    // ------------------------- ATTRIBUTES --------------------------

    static int NEARBY_DISTANCE = 75;    // Measured in meters
    private String sourceId;
    private Location firstLocation = null, middleLocation = null;
    private Location firstSolution = null, secondSolution = null;

    private List<Location> finalRoute = new ArrayList<>();
    static final int SHOWN = 2, COMPLETED = 1, IN_PROGRESS = 0, INCOMPLETE = -1;
    private int finalRouteStatus = INCOMPLETE;

    // ------------------------- CONSTRUCTOR -------------------------

    VehicleLocations(String sourceId, Location lastLocation) {
        this.sourceId = sourceId;
        this.middleLocation = lastLocation;
    }

    // ---------------------- GETTERS & SETTERS ----------------------

    Location getFirstLocation() {
        return firstLocation;
    }

    Location getLastLocation() {
        return middleLocation;
    }
    void setLastLocation(Location lastLocation) {
        firstLocation = this.middleLocation;
        this.middleLocation = lastLocation;
    }

    int getFinalRouteStatus() {
        return finalRouteStatus;
    }
    void setFinalRouteStatus(int finalRouteStatus) {
        this.finalRouteStatus = finalRouteStatus;
    }

    List<Location> getFinalRoute() {
        return finalRoute;
    }

    // -------------------------- USE CASES --------------------------

    /**
     * Reset all values forcing the algorithm to retry from a different starting point.
     *
     * @param currentLocation Current location of the vehicle.
     */
    void reset(Location currentLocation){
        firstLocation = null;
        firstSolution = null;
        secondSolution = null;
        middleLocation = currentLocation;
        NEARBY_DISTANCE += 5;
    }

    /**
     * Add the current location to the final route, if we are near the end, make sure to add the edge and to set
     * the route status to completed, that will prevent extra locations to be added.
     *
     * @param currentLocation Current location of the vehicle.
     */
    void addToFinalRoute (Location currentLocation){

        // Add the location to the route
        finalRoute.add(currentLocation);

        // If we are have arrived to the first solution coming from the first, we have a complete route
        if(currentLocation.isNearby(firstSolution)){
            if(!currentLocation.equals(firstSolution))
                finalRoute.add(firstSolution);
            finalRouteStatus = COMPLETED;
        }
    }

    /**
     * Check whether the vehicle has at least one valid solution.
     *
     * @return true if it has a valid solution, false otherwise.
     */
    boolean hasAnySolution(){
        return firstSolution != null || secondSolution != null;
    }

    /**
     * Compare the current location with the solutions, if the location is close to one of the solutions, select
     * the one that's farthest away from the source (firstLocation), that way each iteration will improve the chances
     * of obtaining the best solution possible.
     *
     * @param currentLocation Current location of the vehicle.
     */
    void updateSolutionIfImproved(String sourceId, Location currentLocation){
        double distanceFirst = 0;
        double distanceSecond = 0;

        if(currentLocation.isNearby(firstSolution))
            distanceFirst = firstSolution.getDistance(firstLocation);
        else if(currentLocation.isNearby(secondSolution))
            distanceSecond = secondSolution.getDistance(firstLocation);
        else
            return;

        System.out.println("Edge detected: " + currentLocation);
        double distanceCurrent = currentLocation.getDistance(firstLocation);

        // The solution who's distance with the source is bigger, is a more precise edge (work/home)

        if(distanceCurrent > distanceFirst) {
            System.out.println(sourceId);
            System.out.println("Rotating solution: " + firstSolution + " to: " + currentLocation);
            firstSolution = currentLocation;
        }else if(distanceCurrent > distanceSecond) {
            System.out.println(sourceId);
            System.out.println("Rotating solution: " + secondSolution + " to: " + currentLocation);
            secondSolution = currentLocation;
        }
    }


    /**
     * Save the route edge in one of the two available positions and rotate the coordinates,
     * Once both edges have been found, start to recollect data to form a route from edge to edge.
     *
     * @param currentLocation Current location of the vehicle.
     */
    void saveResult (Location currentLocation) {
        System.out.println("Saving result for vehicle: " + sourceId);

        // Save in the first solution spot if empty
        if (firstSolution == null) {
            System.out.println("Saving F:");
            System.out.println("A: " + firstLocation);
            System.out.println("B: " + middleLocation);
            System.out.println("C: " + currentLocation);
            firstSolution = middleLocation;

        // Save in the second solution spot if empty
        } else if (secondSolution == null) {
            System.out.println("Saving S:");
            System.out.println("A: " + firstLocation);
            System.out.println("B: " + middleLocation);
            System.out.println("C: " + currentLocation);
            secondSolution = middleLocation;

        // In case duplicates are found, print them, this will help to correct the nearby radius
        } else if(!firstSolution.isNearby(middleLocation) && !secondSolution.isNearby(middleLocation)) {
            System.out.println("Save D:");
            System.out.println("A: " + firstLocation);
            System.out.println("B: " + middleLocation);
            System.out.println("C: " + currentLocation);
        }

        // Rotate firstLocation and middleLocation
        setLastLocation(currentLocation);

        // If both solutions are present start to collect the route data
        if(secondSolution != null) {
            finalRouteStatus = IN_PROGRESS;
            finalRoute.add(secondSolution);
            finalRoute.add(currentLocation);
        }
    }
}
