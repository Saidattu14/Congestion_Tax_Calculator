package com.example.Congestion_Tax_Calculator.Model;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * This is a Vehicle Model class which holds information about Vehicle Details.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VehiclesModel {
    private int vehicle_id;
    private String vehicle_type;
    private String vehicle_name;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime[] dates;


    public VehiclesModel(int vehicle_id, String vehicle_type, String vehicle_name, LocalDateTime[] dates) {
        this.vehicle_id = vehicle_id;
        this.vehicle_type = vehicle_type;
        this.vehicle_name = vehicle_name;
        this.dates = dates;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public LocalDateTime[] getDates() {
        return dates;
    }


    public VehiclesModel() {

    }

}
