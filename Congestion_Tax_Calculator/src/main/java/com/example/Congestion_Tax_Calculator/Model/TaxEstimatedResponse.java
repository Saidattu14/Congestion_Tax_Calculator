package com.example.Congestion_Tax_Calculator.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;



 /**
 * This is a TaxEstimatedResponse Model class which holds information about Estimated Tax Details of Vehicles.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TaxEstimatedResponse {


    private VehiclesModel vehicle;
    private String message;
    private int tax;
    private List<String> taxExemptedDates;
    private List<String> errorDates;

    public TaxEstimatedResponse() {

    }

    public TaxEstimatedResponse(VehiclesModel vehicle, String message, int tax, List<String>  taxExemptedDates, List<String> errorDates) {
        this.vehicle = vehicle;
        this.message = message;
        this.tax = tax;
        this.taxExemptedDates = taxExemptedDates;
        this.errorDates = errorDates;
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

     public List<String> getTaxExemptedDates() {
         return taxExemptedDates;
     }

     public List<String> getErrorDates() {
         return errorDates;
     }

     @Override
    public String toString() {
        return "TaxEstimatedRespose{" +
                "vehicle=" + vehicle +
                ", message='" + message + '\'' +
                ", tax=" + tax +
                ", taxExemptedDates=" + taxExemptedDates +
                ", errorDates=" + errorDates +
                '}';
    }
}
