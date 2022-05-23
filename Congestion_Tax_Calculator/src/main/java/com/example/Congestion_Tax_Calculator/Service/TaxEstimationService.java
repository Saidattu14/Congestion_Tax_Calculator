package com.example.Congestion_Tax_Calculator.Service;

import com.example.Congestion_Tax_Calculator.Model.CongestionTaxRulesModel;
import com.example.Congestion_Tax_Calculator.Model.TaxEstimatedResponse;
import com.example.Congestion_Tax_Calculator.Model.TaxEstimationModel;
import com.example.Congestion_Tax_Calculator.Model.VehiclesModel;
import com.example.Congestion_Tax_Calculator.Repository.CongestionTaxRepository;
import com.example.Congestion_Tax_Calculator.enums.NonTaxExemptVehicles;
import com.example.Congestion_Tax_Calculator.enums.TaxExemptVehicles;
import com.example.Congestion_Tax_Calculator.enums.TaxMessageInfo;
import com.example.Congestion_Tax_Calculator.enums.VehicleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;


@Service
public class TaxEstimationService {

    @Autowired
    CongestionTaxRepository congestionTaxRepository;

    private final List<TaxEstimatedResponse> taxEstimatedResponses = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * The method Purpose is to check whether the vehicle type is TaxExempted or NonTaxExempted
     * @param @Vehicle
     * @return VehicleStatus whether the Vehicle is TaxExempted or NonTaxExempted.
     */
    private VehicleStatus CheckTaxExemptVehicle(VehiclesModel vehicle)
    {
        for (TaxExemptVehicles taxExemptVehicles : TaxExemptVehicles.values()) {
            if(Objects.equals(taxExemptVehicles.toString(), vehicle.getVehicle_type()))
            {
                return VehicleStatus.TaxExempted;
            }
        }
        for(NonTaxExemptVehicles nonTaxExemptVehicles : NonTaxExemptVehicles.values())
        {
            if(Objects.equals(nonTaxExemptVehicles.toString(), vehicle.getVehicle_type()))
            {
                return VehicleStatus.NonTaxExempted;
            }
        }
        return null;
    }

    /**
     * The method Purpose is to find Whether the CongestionTaxRules Data is exists from TaxEstimation City
     * @param @TaxEstimationModel
     * @return CongestionTaxRules of City Else null.
     */
    public Optional<CongestionTaxRulesModel> CheckCity(TaxEstimationModel taxEstimationModel)
    {
        try {
            Optional<CongestionTaxRulesModel> congestionTaxRules = congestionTaxRepository.findById(taxEstimationModel.getCity_name());
//            System.out.println(congestionTaxRules);
            return congestionTaxRules;
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return Optional.empty();
    }

    /**
     * The method is to Estimate Tax for TaxEstimation Data based on CongestionTaxRules.
     * The Main Purpose is to identify the date and timing details and category them and apply respective Congestion Tax Rules.
     * @param @congestionTaxRules @taxEstimationData
     * @return TaxEstimated Responses of Vehicles.
     */
    public List<TaxEstimatedResponse> EstimateTax(Optional<CongestionTaxRulesModel> congestionTaxRules, TaxEstimationModel taxEstimationData)
    {

        CongestionTaxRulesModel congestionTaxRulesObj = congestionTaxRules.get();
        List<VehiclesModel> vehiclesData = taxEstimationData.getVehiclesList();
        for (VehiclesModel vehicle : vehiclesData) {
            List<String> taxExemptedDates = new ArrayList<>();
            List<String> errorDates = new ArrayList<>();
            List<LocalDateTime> validTaxEstimationDates = new ArrayList<>();
            VehicleStatus vehicleStatus1 = CheckTaxExemptVehicle(vehicle);
            if (vehicleStatus1 == VehicleStatus.TaxExempted) {
                taxEstimatedResponses.add(new TaxEstimatedResponse(
                        vehicle,
                        TaxMessageInfo.Tax_is_Exempted_For_ + vehicle.getVehicle_type(),
                        0,
                        taxExemptedDates, errorDates));
            }
            else if(vehicleStatus1 == VehicleStatus.NonTaxExempted) {
                int vehicle_tax = 0;
                for (int i = 0; i < vehicle.getDates().length; i++) {
                    try {
                        String[] datetime = vehicle.getDates()[i].split(" ");
                        LocalDateTime dateTime = LocalDateTime.parse(vehicle.getDates()[i], formatter);
                        if (!CheckTaxFreeDate(LocalDate.from(dateTime))) {
                            validTaxEstimationDates.add(dateTime);
                        } else {
                            taxExemptedDates.add(datetime[0] +" "+ datetime[1]);
                        }

                    } catch (Exception e) {
                        errorDates.add(vehicle.getDates()[i]);
                    }
                }
                if (validTaxEstimationDates.size() > 0) {
                    if (congestionTaxRulesObj.isSingle_charge_rule()) {
                        vehicle_tax = SingleChargeRuleTaxEstimation(validTaxEstimationDates, congestionTaxRulesObj);
                    } else {
                        vehicle_tax = TaxEstimationWithoutSingleCharge(validTaxEstimationDates,congestionTaxRulesObj);
                    }
                }
                taxEstimatedResponses.add(vehicleTaxData(vehicle, vehicle_tax, taxExemptedDates, errorDates, validTaxEstimationDates));
            }
            else
            {
                taxEstimatedResponses.add(new TaxEstimatedResponse(
                    vehicle,
                    TaxMessageInfo.Invalid_Vehicle_Type.toString(),
                    0,
                    taxExemptedDates, errorDates));

            }

        }
        return taxEstimatedResponses;
    }


     /**
     * The method Purpose is for Calculating Tax on Date Values Without Single Charge Rule
     * @param @LocalDateTime (Valid Dates and Time Values)  @CongestionTaxRulesModel (Timing Details)
     * @return total tax of all dates
     */
    private int TaxEstimationWithoutSingleCharge(List<LocalDateTime> validTaxEstimationDates,CongestionTaxRulesModel congestionTaxRulesObj)
    {

        int total_tax = 0;
        for(int i = 0; i<validTaxEstimationDates.size() && total_tax <60;i++)
        {
            int tx = GetTollFee(validTaxEstimationDates.get(i),congestionTaxRulesObj);
            total_tax = Math.min(total_tax + tx, 60);
        }
        return total_tax;
    }

    /**
     * This method is for Calculating Tax on Date Values With Single Charge Rule.
     * It works by sorting the valid dates and picks the maximum tax value within the Hour difference timings.
     * @param @LocalDateTime (Valid Dates and Time Values)  @CongestionTaxRulesModel (Timing Details)
     * @return tax of all the dates
     */
    private int SingleChargeRuleTaxEstimation(List<LocalDateTime> validTaxEstimationDates,CongestionTaxRulesModel congestionTaxRulesObj)
    {

        List<LocalDateTime> sortedValidTaxEstimationDatesList = validTaxEstimationDates.stream().sorted(Comparator.naturalOrder()).toList();
        int total_tax = 0;
        for(int i = 0; i<sortedValidTaxEstimationDatesList.size() && total_tax <60;i++)
        {
            LocalDateTime l1 = sortedValidTaxEstimationDatesList.get(i);
            List<LocalDateTime> temp = new ArrayList<>();
            temp.add(l1);
            for(int j = i+1;j<sortedValidTaxEstimationDatesList.size();j++)
            {
                LocalDateTime l2 = sortedValidTaxEstimationDatesList.get(j);
                if(l1.getYear() == l2.getYear() && l2.getMonth() == l1.getMonth() && l1.getDayOfMonth() == l2.getDayOfMonth())
                {
                   int hours = l2.getHour() - l1.getHour();
                   int minutes = l2.getMinute() - l1.getMinute();
                   int seconds = l2.getSecond() - l1.getSecond();
                   int total_time = hours*3600 + minutes*60 + seconds;
                   if(total_time <= 3600)
                   {
                       temp.add(l2);
                   }
                }
                else
                {
                    break;
                }
            }
            int index = 0;
            int max_tax = 0;
            for(int i1 = 0; i1 <temp.size();i1++)
            {
               int tx = GetTollFee(temp.get(i1),congestionTaxRulesObj);
               if(tx >= max_tax)
               {
                   max_tax = tx;
                   index = i1;
               }
            }
            total_tax = Math.min(total_tax + max_tax, 60);
            i = i + index;
        }
        return total_tax;
    }

    /**
     * This method is for Checking whether the date is Tax free date or not.
     * @param @LocalDateTime (Valid Dates and Time Values)
     * @return true for Tax Free Date Else False
     */
    private boolean CheckTaxFreeDate(LocalDate date) {

        int month = date.getMonthValue();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayOfMonth = date.getDayOfMonth();
        String[] holidays_dates = new String[] {"2013-01-01", "2013-03-29",
                "2014-04-01","2013-05-01","2013-05-09",
                "2013-06-06","2013-06-21","2013-11-01","2013-12-25","2013-12-26"};
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(month == 7)
        {
            return true;
        }
        else if(dayOfWeek == SUNDAY || dayOfWeek == SATURDAY)
        {
            return true;
        }
        else if (date.getYear() == 2013)
        {
            for(int i = 0; i<holidays_dates.length;i++)
            {
                try {
                    LocalDate dateTime = LocalDate.parse(holidays_dates[i]);
                    if((dateTime.getMonthValue() == month && dateTime.getMonthValue() == dayOfMonth) ||
                            dayBeforeHolidayCheck(month,dayOfMonth,dateTime.getMonthValue(),dateTime.getDayOfMonth(),dateTime.getYear()))
                    {
                        return true;
                    }
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        }
        return false;
    }

    /**
     * This method is for Checking whether the date is Holiday before date or not.
     * @param @month @day @afterMonth @afterDay @year
     * @return true for Tax Free Date Else False
     */
    private boolean dayBeforeHolidayCheck(int month, int day,int afterMonth, int afterDay,int year)
    {

        for(int i=1; i<=12;i++)
        {
            if(afterDay == 1 && afterMonth == 1)
            {
                if(month == 12 && day == 31)
                {

//                    System.out.println(month + " "+ day);
                    return true;
                }
            }
            else if(afterDay == 1)
            {
                if(i == 2 || i == 4 || i == 6 || i == 8 || i== 9 || i == 11)
                {
                    if(month == i-1 && day == 31)
                    {

//                        System.out.println(month + " "+ day);
                        return true;
                    }
                }
                else if(i==5 || i== 7 || i == 10 || i==12)
                {
                    if(month == i-1 && day == 30)
                    {

//                        System.out.println(month + " "+ day);
                        return true;
                    }
                }
                else if(i== 3)
                {
                    if(year%4 ==0 && month == i-1 && day == 29)
                    {
//                        System.out.println(month + " "+ day);
                        return true;
                    }
                    else if(month == i-1 && day == 28)
                    {

//                        System.out.println(month + " "+ day);
                        return true;
                    }
                }
            }
            else
            {
                if(afterMonth == i && day == afterDay-1)
                {

//                    System.out.println(month + " "+ day);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method is to return tax value according to timing cost as per rules of Congestion Tax Rules of City.
     * @param @LocalDateTime @CongestionTaxRulesModel @TaxEstimationModel
     * @return tax
     */
    private int GetTollFee(LocalDateTime localDateTime,CongestionTaxRulesModel congestionTaxRulesObj)
    {

        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        if (hour == 6 && minute <= 29)
        {
            return congestionTaxRulesObj.getTax_details_on_time_600_to_629();
        }
        else if (hour == 6) {
            return congestionTaxRulesObj.getTax_details_on_time_630_to_659();
        }
        else if (hour == 7) {
            return congestionTaxRulesObj.getTax_details_on_time_700_to_759();
        }
        else if (hour == 8 && minute <= 29) {
            return congestionTaxRulesObj.getTax_details_on_time_800_to_829();
        }
        else if (hour == 8 || hour >= 9 && hour <= 14)
        {
            return congestionTaxRulesObj.getTax_details_on_time_830_to_1459();
        }
        else if (hour == 15 && minute <= 29) {
            return congestionTaxRulesObj.getTax_details_on_time_1500_to_1529();
        }
        else if (hour == 15 || hour == 16)
        {
            return congestionTaxRulesObj.getTax_details_on_time_1530_to_1659();
        }
        else if (hour == 17) {
            return congestionTaxRulesObj.getTax_details_on_time_1700_to_1759();
        }
        else if (hour == 18 && minute <= 29) {
            return congestionTaxRulesObj.getTax_details_on_time_1800_to_1829();
        }
        else if((hour == 18 && minute >=30) || (hour >= 18 && hour <= 5 && minute >=0 && minute<=59))
        {
           return congestionTaxRulesObj.getTax_details_on_time_1830_to_0559();
        }
        return 0;
    }


    /**
     * This method is for creating Tax Response for the vehicle.
     * @param @CVehicle @TaxExemptedDates @ErrorDates @ValidDates @Tax
     * @return Tax Response Data
     */
    private TaxEstimatedResponse vehicleTaxData(VehiclesModel vehicle, int tax, List<String> taxExemptedDates, List<String> errorDates, List<LocalDateTime>validTaxEstimationDates)
    {
        int tED = taxExemptedDates.size();
        int eD = errorDates.size();
        int vTED = validTaxEstimationDates.size();
        if(tED > 0 && eD>0 && vTED >0)
        {
            return new TaxEstimatedResponse(
                    vehicle,
                    TaxMessageInfo.Valid_Dates_Invalid_Dates_And_Tax_Exempted_Days_Are_Present.toString(),
                    tax,
                    taxExemptedDates,errorDates);
        }
        else if(tED > 0 && eD > 0 && vTED <=0)
        {
            return new TaxEstimatedResponse(
                    vehicle,
                    TaxMessageInfo.Invalid_Dates_And_Tax_Exempted_Days_Are_Present.toString(),
                    tax,
                    taxExemptedDates,errorDates);
        }
        else if(tED >0 && eD <=0 && vTED <= 0)
        {
            return new TaxEstimatedResponse(
                    vehicle,
                    TaxMessageInfo.Only_Tax_Exempted_Days_Are_Present.toString(),
                    tax,
                    taxExemptedDates,errorDates);
        }
        else if(eD > 0 && vTED <= 0 && tED <= 0)
        {
            return new TaxEstimatedResponse(
                    vehicle,
                    TaxMessageInfo.Only_Invalid_Dates_Are_Present.toString(),
                    tax,
                    taxExemptedDates,errorDates);
        }
        else if(vTED >0 && eD <= 0 && tED <= 0)
        {
            return new TaxEstimatedResponse(
                    vehicle,
                    TaxMessageInfo.Only_Valid_Dates_Days_Are_Present.toString(),
                    tax,
                    taxExemptedDates,errorDates);
        }
        else if(vTED>0 && tED > 0 && eD <= 0)
        {
            return new TaxEstimatedResponse(
                    vehicle,
                    TaxMessageInfo.Valid_Dates_And_Tax_Exempted_Days_Are_Present.toString(),
                    tax,
                    taxExemptedDates,errorDates);
        }
        else if(vTED >0 && eD > 0 && tED <=0)
        {
            return new TaxEstimatedResponse(
                    vehicle,
                    TaxMessageInfo.Valid_Dates_And_Invalid_Dates_Are_Present.toString(),
                    tax,
                    taxExemptedDates,errorDates);
        }
        return null;
    }
}
