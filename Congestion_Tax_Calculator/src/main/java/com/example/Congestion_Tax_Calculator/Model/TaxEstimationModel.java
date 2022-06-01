package com.example.Congestion_Tax_Calculator.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;


 /**
 * This is a TaxEstimatedModel class which holds information about Tax Estimated Vehicle Details of city.
 */
 @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
 public class TaxEstimationModel {


    private List<VehiclesModel> vehiclesList;
    private String cityName;


    public String getCityName() {
        return cityName;
    }


    public TaxEstimationModel(String cityName, List<VehiclesModel> vehiclesList) {
        this.vehiclesList = vehiclesList;
        this.cityName = cityName;
    }

     public List<VehiclesModel> getVehiclesList() {
         return vehiclesList;
     }

     public TaxEstimationModel() {
     }

 }
