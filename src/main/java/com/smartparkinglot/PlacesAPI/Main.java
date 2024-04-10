package org.example;

public class Main {
    public static void main(String[] args) {
        // Exemplu practic de utilizarea PlacesAPI (si un pic de GeocodingAPI) in contextul proiectului
        ParkingLot closestParkingLotToYou = new ParkingLot("AIzaSyDl_t4kyMr_YrRAdBapSbdwD11xSiDooAA");
        while (true) {
            closestParkingLotToYou.findClosestLotInfo();
            System.out.println(closestParkingLotToYou);
        }
    }
}