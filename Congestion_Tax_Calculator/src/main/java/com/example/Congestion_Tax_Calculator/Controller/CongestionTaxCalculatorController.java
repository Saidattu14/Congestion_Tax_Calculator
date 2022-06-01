package com.example.Congestion_Tax_Calculator.Controller;
import com.example.Congestion_Tax_Calculator.Model.CongestionTaxRulesModel;
import com.example.Congestion_Tax_Calculator.Model.TaxEstimatedResponse;
import com.example.Congestion_Tax_Calculator.Model.TaxEstimationModel;
import com.example.Congestion_Tax_Calculator.Service.CongestionTaxService;
import com.example.Congestion_Tax_Calculator.Service.TaxEstimationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1")
@Slf4j
public class CongestionTaxCalculatorController {

    @Autowired
    private TaxEstimationService taxEstimationService;

    @Autowired
    private CongestionTaxService congestionTaxService;


    public CongestionTaxCalculatorController(TaxEstimationService taxEstimationService, CongestionTaxService congestionTaxService) {
        this.taxEstimationService = taxEstimationService;
        this.congestionTaxService = congestionTaxService;
    }


    /**
     * This is a post request end point /tax_calculation.
     * This request is for tax calculation of vehicles details with respective to City.
     * @return @List<TaxEstimatedResponse> the result of tax details of Each vehicle.
     */
    @PostMapping("/tax_calculation")
    public ResponseEntity<List<TaxEstimatedResponse>> taxEstimation(@RequestBody TaxEstimationModel taxEstimationData) {
        try {
             Optional<CongestionTaxRulesModel> congestionTaxRules = taxEstimationService.checkCity(taxEstimationData);
             if(congestionTaxRules.isEmpty())
             {

                 return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
             }
             else
             {
                 CongestionTaxRulesModel congestionTaxRulesObj = congestionTaxRules.get();
                 List<TaxEstimatedResponse> taxEstimatedResponseList = taxEstimationService.estimateTax(congestionTaxRulesObj,taxEstimationData);
                 return new ResponseEntity<>(taxEstimatedResponseList, HttpStatus.OK);
             }
        }
        catch (Exception e)
        {
            log.trace(String.valueOf(e));
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This is a post request end point /congestion_tax_rules/create.
     * This request is to create congestion_tax_rules table.
     * @return the String Whether table is successfully created or not.
     */
    @PostMapping("/congestion_tax_rules/create")
    public ResponseEntity<String> createCongestionTaxRulesTable() {
        String result = congestionTaxService.createTable();
        return switch (result) {
            case "Table Created" -> new ResponseEntity<>(result, HttpStatus.CREATED);
            case "Table Failed To Create" -> new ResponseEntity<>(result, HttpStatus.NOT_IMPLEMENTED);
            default -> new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    /**
     * This is a post request end point /congestion_tax_rules/insert.
     * This request is to insert congestion_tax_rules data.
     * @return the String Whether data is successfully inserted or not.
     */
    @PostMapping("/congestion_tax_rules/insert")
    public ResponseEntity<String> insertCongestionTaxRules(@RequestBody CongestionTaxRulesModel congestionTaxRulesModel) {
        String result = congestionTaxService.insertRow(congestionTaxRulesModel);

        return switch (result) {
            case "Inserted Successfully" -> new ResponseEntity<>(result, HttpStatus.ACCEPTED);
            case "Failed to Insert" -> new ResponseEntity<>(result, HttpStatus.NOT_IMPLEMENTED);
            case "City with Congestion Tax Rules Already Present" -> new ResponseEntity<>(result, HttpStatus.CONFLICT);
            default -> new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    /**
     * This is a get request end point /congestion_tax_rules/read.
     * This request is to get congestion_tax_rules of all cities.
     * @return the String of congestion_tax_rules data or empty list.
     */
    @GetMapping("/congestion_tax_rules/read")
    public ResponseEntity<List<CongestionTaxRulesModel>> readCongestionTaxRules(
            @RequestParam(required = false) String cityName) {
        List<CongestionTaxRulesModel> list = congestionTaxService.readAll(cityName);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * This is a post request end point /congestion_tax_rules/drop.
     * This request to drop congestion_tax_rules table.
     * @return the String Whether table is successfully dropped or not.
     */
    @PostMapping("/congestion_tax_rules/drop")
    public ResponseEntity<String> dropCongestionTaxRules() {
        String result = congestionTaxService.dropTable();
        return switch (result) {
            case "Table Dropped Successfully" -> new ResponseEntity<>(result, HttpStatus.ACCEPTED);
            case "Table Failed To Drop" -> new ResponseEntity<>(result, HttpStatus.NOT_IMPLEMENTED);
            default -> new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

}
