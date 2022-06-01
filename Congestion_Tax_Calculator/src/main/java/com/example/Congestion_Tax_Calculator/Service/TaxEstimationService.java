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
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;


@Service
@Slf4j
public class TaxEstimationService {

    @Autowired
    CongestionTaxRepository congestionTaxRepository;

    /**
     * The method Purpose is to check whether the vehicle type is TaxExempted or NonTaxExempted
     * @param @Vehicle
     * @return VehicleStatus whether the Vehicle is TaxExempted or NonTaxExempted.
     */
    private VehicleStatus checkTaxExemptVehicle(VehiclesModel vehicle)
    {
        for (TaxExemptVehicles taxExemptVehicles : TaxExemptVehicles.values()) {
            if(Objects.equals(taxExemptVehicles.toString(), vehicle.getVehicleType()))
            {
                return VehicleStatus.TAX_EXEMPTED;
            }
        }
        for(NonTaxExemptVehicles nonTaxExemptVehicles : NonTaxExemptVehicles.values())
        {
            if(Objects.equals(nonTaxExemptVehicles.toString(), vehicle.getVehicleType()))
            {
                return VehicleStatus.NON_TAX_EXEMPTED;
            }
        }
        return null;
    }

    /**
     * The method Purpose is to find Whether the CongestionTaxRules Data is exists from TaxEstimation City
     * @param @TaxEstimationModel
     * @return CongestionTaxRules of City Else null.
     */
    public Optional<CongestionTaxRulesModel> checkCity(TaxEstimationModel taxEstimationModel)
    {
        try {
            return congestionTaxRepository.findById(taxEstimationModel.getCityName());
        }
        catch (Exception e)
        {
            log.trace(String.valueOf(e));
        }
        return Optional.empty();
    }

    /**
     * The method is to Estimate Tax for TaxEstimation Data based on CongestionTaxRules.
     * The Main Purpose is to identify vehicle status and call respective functions for tax calculation.
     * @param @congestionTaxRules @taxEstimationData
     * @return TaxEstimated Responses of Vehicles.
     */
    public List<TaxEstimatedResponse> estimateTax(@NotNull CongestionTaxRulesModel congestionTaxRulesObj, @NotNull TaxEstimationModel taxEstimationData)
    {

        List<VehiclesModel> vehiclesData = taxEstimationData.getVehiclesList();
        List<TaxEstimatedResponse> taxEstimatedResponses = new ArrayList<>();
        for (VehiclesModel vehicle : vehiclesData) {
            VehicleStatus vehicleStatus1 = checkTaxExemptVehicle(vehicle);
            if (vehicleStatus1 == VehicleStatus.TAX_EXEMPTED) {
                taxEstimatedResponses.add(taxExemptedVehicleResponse(vehicle));
            }
            else if(vehicleStatus1 == VehicleStatus.NON_TAX_EXEMPTED)
            {
                 taxEstimatedResponses.add(nonTaxExemptedVehicleTypeResponse(vehicle,congestionTaxRulesObj));
            }
            else
            {
                taxEstimatedResponses.add(inValidVehicleTypeResponse(vehicle));
            }
        }
        return taxEstimatedResponses;
    }

    /**
     * The method Purpose is for Calculating Tax on NON Tax Exempted Vehicle Types
     * It does by seperating tax Exempted Dates and valid Dates.
     * On Valid Dates it apply tax estimation based on either Single Charge Tax rule or Without  Single Charge Tax rule.
     * @param @VehiclesMoodel (one Vehicle Details) @CongestionTaxRulesModel (Timing Details)
     * @return TaxEstimated Responses of Vehicle.
     */
    private TaxEstimatedResponse nonTaxExemptedVehicleTypeResponse(VehiclesModel vehicle,CongestionTaxRulesModel congestionTaxRulesObj)
    {
        int vehicleTax = 0;
        List<LocalDateTime> taxExemptedDates = new ArrayList<>();
        List<LocalDateTime> validTaxEstimationDates = new ArrayList<>();
        for (int i = 0; i < vehicle.getDates().length; i++) {

            LocalDateTime dateTime = vehicle.getDates()[i];
            if (!checkTaxFreeDate(LocalDate.from(dateTime)))
            {
                validTaxEstimationDates.add(dateTime);
            } else {
                taxExemptedDates.add(dateTime);
            }
        }
        if (!validTaxEstimationDates.isEmpty()) {
            if (congestionTaxRulesObj.isSingleChargeRule())
            {
                vehicleTax = singleChargeRuleTaxEstimation(validTaxEstimationDates, congestionTaxRulesObj);
            }
            else
            {
                vehicleTax = taxEstimationWithoutSingleCharge(validTaxEstimationDates,congestionTaxRulesObj);
            }
        }
        return vehicleTaxData(vehicle, vehicleTax, taxExemptedDates, validTaxEstimationDates);
    }

     /**
     * The method Purpose is for Calculating Tax on Date Values Without Single Charge Rule
     * @param @LocalDateTime (Valid Dates and Time Values)  @CongestionTaxRulesModel (Timing Details)
     * @return total tax of all dates
     */
    private int taxEstimationWithoutSingleCharge(List<LocalDateTime> validTaxEstimationDates,CongestionTaxRulesModel congestionTaxRulesObj)
    {

        int totalTax = 0;
        int index = 0;
        int maximumPerdayTax = 0;
        for(int i = 0; i<validTaxEstimationDates.size();i++)
        {
            LocalDateTime l1 = validTaxEstimationDates.get(index);
            LocalDateTime l2 = validTaxEstimationDates.get(i);
            if(checkSameDate(l1,l2))
            {
                maximumPerdayTax = maximumPerdayTax + getTollFee(l2,congestionTaxRulesObj);
            }
            else
            {
                totalTax = totalTax + Math.min(maximumPerdayTax, 60);
                maximumPerdayTax = getTollFee(validTaxEstimationDates.get(i),congestionTaxRulesObj);
                index = i;
            }

        }
        totalTax = totalTax + Math.min(maximumPerdayTax, 60);
        return totalTax;
    }

    /**
     * This method is for Calculating Tax on Date Values With Single Charge Rule.
     * It works by sorting the valid dates and picks the maximum tax value within the Hour difference timings.
     * @param @LocalDateTime (Valid Dates and Time Values)  @CongestionTaxRulesModel (Timing Details)
     * @return tax of all the dates
     */
    private int singleChargeRuleTaxEstimation(@NotNull List<LocalDateTime> validTaxEstimationDates, CongestionTaxRulesModel congestionTaxRulesObj)
    {
        List<LocalDateTime> sortedValidTaxEstimationDatesList = validTaxEstimationDates.stream().sorted(Comparator.naturalOrder()).toList();
        int totalTax = 0;
        int maximumSingleChargeTax = 0;
        int singleChargeTax =0;
        int index=0;
        int maximumPerdayTax = 0;
        for(int i = 0; i<sortedValidTaxEstimationDatesList.size();i++)
        {
            LocalDateTime l1 = sortedValidTaxEstimationDatesList.get(index);
            LocalDateTime l2 = sortedValidTaxEstimationDatesList.get(i);
            if(getTimeDiffernce(l1,l2)<=3600)
            {
               singleChargeTax = getTollFee(l2,congestionTaxRulesObj);
               if(singleChargeTax >= maximumSingleChargeTax)
               {
                   maximumSingleChargeTax = singleChargeTax;
               }
            }
            else
            {
                if(!checkSameDate(l1,l2))
                {
                    totalTax = totalTax + Math.min(maximumPerdayTax + maximumSingleChargeTax, 60);
                    maximumPerdayTax = 0;
                }
                else
                {
                    maximumPerdayTax = Math.min(maximumPerdayTax + maximumSingleChargeTax, 60);
                }
                maximumSingleChargeTax = getTollFee(l2,congestionTaxRulesObj);
                index = i;
            }

        }
        totalTax = totalTax + Math.min(maximumPerdayTax + maximumSingleChargeTax, 60);
        return totalTax;
    }

    /**
     * This method is for checking the two LocalDateTime values are occured on Same Date or not.
     * @param @LocalDateTime l1 (Valid Dates and Time Values) @LocalDateTime l2 (Valid Dates and Time Values)
     * @return the true if two date are having the same month, same day and same year Else False
     */
    private boolean checkSameDate( LocalDateTime l1 ,  LocalDateTime l2)
    {
        return (l1.getYear() == l2.getYear() && l2.getMonth() == l1.getMonth() && l1.getDayOfMonth() == l2.getDayOfMonth());

    }

    /**
     * This method is for getting the time differnce between two dates.
     * @param @LocalDateTime l1 (Valid Dates and Time Values) @LocalDateTime l2 (Valid Dates and Time Values)
     * @return the time differnce value in seconds
     */
    private int getTimeDiffernce(LocalDateTime l1 ,  LocalDateTime l2)
    {
        if(checkSameDate(l1,l2))
        {
            int hours = l2.getHour() - l1.getHour();
            int minutes = l2.getMinute() - l1.getMinute();
            int seconds = l2.getSecond() - l1.getSecond();
            return hours*3600 + minutes*60 + seconds;
        }
        return 10000;

    }

    /**
     * This method is for Checking whether the date is Tax free date or not.
     * @param @LocalDateTime (Valid Dates and Time Values)
     * @return true for Tax Free Date Else False
     */
    private boolean checkTaxFreeDate(LocalDate date) {

        int month = date.getMonthValue();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayOfMonth = date.getDayOfMonth();
        LocalDate[] holidaysDates = new LocalDate[] {LocalDate.parse("2013-01-01"), LocalDate.parse("2013-03-29"),
                LocalDate.parse("2014-04-01"), LocalDate.parse("2013-05-01"), LocalDate.parse("2013-05-09"),
                LocalDate.parse("2013-06-06"), LocalDate.parse("2013-06-21"), LocalDate.parse("2013-11-01"), LocalDate.parse("2013-12-25"), LocalDate.parse("2013-12-26")};
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
            for (LocalDate holidays_date : holidaysDates) {

                if ((holidays_date.getMonthValue() == month && holidays_date.getMonthValue() == dayOfMonth) ||
                        dayBeforeHolidayCheck(month,dayOfMonth,holidays_date.getMonthValue(),holidays_date.getDayOfMonth(),holidays_date.getYear())) {
                    return true;
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

        if (afterMonth == 1 && afterDay == 1 && month == 12 && day == 31)
        {
            return true;
        }
        if(afterMonth== 3)
        {
            if(year%4 ==0 && month == afterMonth-1 && day == 29)
                return true;
            else if(month == afterMonth - 1 && day == 28)
            {
                return true;
            }
        }
        if((afterMonth == 2 || afterMonth == 4 || afterMonth == 6 || afterMonth == 8 || afterMonth== 9 || afterMonth == 11) && (month == afterMonth - 1 && day == 31))
        {
            return true;
        }
        if((afterMonth==5 || afterMonth== 7 || afterMonth == 10 || afterMonth==12) && (month == afterMonth - 1 && day == 30))
        {
            return true;
        }
        return (afterMonth == month && day == afterDay - 1);
    }

    /**
     * This method is to return tax value according to timing cost as per rules of Congestion Tax Rules of City.
     * @param @LocalDateTime @CongestionTaxRulesModel @TaxEstimationModel
     * @return tax
     */
    private int getTollFee(LocalDateTime localDateTime,CongestionTaxRulesModel congestionTaxRulesObj)
    {
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        if (hour == 6 && minute <= 29)
        {
            return congestionTaxRulesObj.getTaxDetailsOnTime600to629();
        }
        else if (hour == 6) {
            return congestionTaxRulesObj.getTaxDetailsOnTime630to659();
        }
        else if (hour == 7) {
            return congestionTaxRulesObj.getTaxDetailsOnTime700to759();
        }
        else if (hour == 8 && minute <= 29) {
            return congestionTaxRulesObj.getTaxDetailsOnTime800to829();
        }
        else if (hour == 8 || hour >= 9 && hour <= 14)
        {
            return congestionTaxRulesObj.getTaxDetailsOnTime830to1459();
        }
        else if (hour == 15 && minute <= 29) {
            return congestionTaxRulesObj.getTaxDetailsOnTime1500to1529();
        }
        else if (hour == 15 || hour == 16)
        {
            return congestionTaxRulesObj.getTaxDetailsOnTime1530to1659();
        }
        else if (hour == 17) {
            return congestionTaxRulesObj.getTaxDetailsOnTime1700to1759();
        }
        else if (hour == 18 && minute <= 29) {
            return congestionTaxRulesObj.getTaxDetailsOnTime1800to1829();
        }
        else {
           return congestionTaxRulesObj.getTaxDetailsOnTime1830to0559();
        }

    }

    /**
     * This method is for creating Tax Response for the vehicle.
     * @param @Vehicle @TaxExemptedDates @ValidDates @Tax
     * @return Tax Response Data
     */
    private TaxEstimatedResponse vehicleTaxData(VehiclesModel vehicle, int tax, List<LocalDateTime> taxExemptedDates,List<LocalDateTime>validTaxEstimationDates)
    {
        int taxExemptedDateSize = taxExemptedDates.size();
        int validTaxEstimationDatesSize = validTaxEstimationDates.size();
        if(taxExemptedDateSize > 0 && validTaxEstimationDatesSize >0)
        {
            return new TaxEstimatedResponse(
                    vehicle,
                    TaxMessageInfo.VALID_DATES_AND_TAX_EXEMPTED_DAYS_ARE_PRESENT.toString(),
                    tax,
                    taxExemptedDates);
        }
        else if(taxExemptedDateSize >0)
        {
            return new TaxEstimatedResponse(
                    vehicle,
                    TaxMessageInfo.ONLY_TAX_EXEMPTED_DAYS_ARE_PRESENT.toString(),
                    tax,
                    taxExemptedDates);
        }

        else if(validTaxEstimationDatesSize >0)
        {
            return new TaxEstimatedResponse(
                    vehicle,
                    TaxMessageInfo.ONLY_VALID_DATES_DAYS_ARE_PRESENT.toString(),
                    tax,
                    taxExemptedDates);
        }
        return null;
    }


    /**
     * This method is for creating Tax Response for the vehicle for Tax Exempted Vehicle.
     * @param @Vehicle (One vehicle details)
     * @return @TaxEstimatedResponse
     */
    private TaxEstimatedResponse taxExemptedVehicleResponse(VehiclesModel vehicle)
    {

        return new TaxEstimatedResponse(
                vehicle,
                TaxMessageInfo.TAX_IS_EXEMPTED_FOR +"_"
                        + vehicle.getVehicleType(),
                0,
                new ArrayList<>());
    }


    /**
     * This method is for creating Tax Response for the vehicle for In Valid Vehicle Type.
     * @param @Vehicle (One vehicle details)
     * @return @TaxEstimatedResponse
     */
    private TaxEstimatedResponse inValidVehicleTypeResponse(VehiclesModel vehicle)
    {
        return new TaxEstimatedResponse(
                vehicle,
                TaxMessageInfo.INVALID_VEHICLE_TYPE.toString(),
                0,
                new ArrayList<>());
    }

}
