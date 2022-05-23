package com.example.Congestion_Tax_Calculator.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;


 /**
 * This is a TaxEstimatedModel class which holds information about Tax Estimated Vehicle Details of city.
 */
 @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
 public class TaxEstimationModel {


    private List<VehiclesModel> vehiclesList;
    private String city_name;


    public String getCity_name() {
        return city_name;
    }


    public TaxEstimationModel(String city_name, List<VehiclesModel> vehiclesList) {
        this.vehiclesList = vehiclesList;
        this.city_name = city_name;
    }

     public List<VehiclesModel> getVehiclesList() {
         return vehiclesList;
     }

     public TaxEstimationModel() {
     }

 }
