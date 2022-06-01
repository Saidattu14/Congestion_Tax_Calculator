package com.example.Congestion_Tax_Calculator.Model;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * This is a Vehicle Model class which holds information about Vehicle Details.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VehiclesModel {
    private int vehicleId;
    private String vehicleType;
    private String vehicleName;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime[] dates;


    public VehiclesModel(int vehicleId, String vehicleType, String vehicleName, LocalDateTime[] dates) {
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.vehicleName = vehicleName;
        this.dates = dates;
    }

    public int getVehicleId() {

        return vehicleId;
    }

    public String getVehicleType()
    {
        return vehicleType;
    }

    public String getVehicleName()
    {
        return vehicleName;
    }

    public LocalDateTime[] getDates() {

        return dates;
    }


    public VehiclesModel() {

    }

}
