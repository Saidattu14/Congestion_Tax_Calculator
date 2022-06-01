package com.example.Congestion_Tax_Calculator.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;
import java.util.List;



 /**
 * This is a TaxEstimatedResponse Model class which holds information about Estimated Tax Details of Vehicles.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TaxEstimatedResponse {


    private VehiclesModel vehicle;
    private String message;
    private int tax;
    private List<LocalDateTime> taxExemptedDates;


    public TaxEstimatedResponse() {

    }

    public TaxEstimatedResponse(VehiclesModel vehicle, String message, int tax, List<LocalDateTime> taxExemptedDates) {
        this.vehicle = vehicle;
        this.message = message;
        this.tax = tax;
        this.taxExemptedDates = taxExemptedDates;
    }

     public VehiclesModel getVehicle() {
         return vehicle;
     }

     public String getMessage() {
         return message;
     }

     public int getTax() {
         return tax;
     }

     public List<LocalDateTime> getTaxExemptedDates() {
         return taxExemptedDates;
     }


}
