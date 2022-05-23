package com.example.Congestion_Tax_Calculator.Controller;

import com.example.Congestion_Tax_Calculator.Model.CongestionTaxRulesModel;
import com.example.Congestion_Tax_Calculator.Model.TaxEstimatedResponse;
import com.example.Congestion_Tax_Calculator.Model.TaxEstimationModel;
import com.example.Congestion_Tax_Calculator.Service.CongestionTaxService;
import com.example.Congestion_Tax_Calculator.Service.TaxEstimationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1")
public class CongestionTaxCalculatorController {

    @Autowired
    TaxEstimationService taxEstimationService;

    @Autowired
    CongestionTaxService congestionTaxService;


    /**
     * This is a post request end point /tax_calculation.
     * This request is for tax calculation of vehicles details with respective to City.
     * @return the result of tax details of Each vehicle.
     */
    @PostMapping("/tax_calculation")
    public ResponseEntity<List<TaxEstimatedResponse>> taxEstimation(@RequestBody TaxEstimationModel taxEstimationData) {
        try {
             Optional<CongestionTaxRulesModel> congestionTaxRules = taxEstimationService.CheckCity(taxEstimationData);
             if(congestionTaxRules.isEmpty() == true || congestionTaxRules == null)
             {

                 return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
             }
             else
             {

                 List<TaxEstimatedResponse> taxEstimatedResponseList = taxEstimationService.EstimateTax(congestionTaxRules,taxEstimationData);
                 ObjectMapper mapper = new ObjectMapper();
                 String jsonArray = mapper.writeValueAsString(taxEstimatedResponseList);
                 TypeReference<List<TaxEstimatedResponse>> typeRef = new TypeReference<>() {};
                 List<TaxEstimatedResponse> list = mapper.readValue(jsonArray, typeRef);
                 return new ResponseEntity<>(list, HttpStatus.OK);
             }
        }
        catch (Exception e)
        {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This is a post request end point /congestion_tax_rules/create.
     * This request is to create congestion_tax_rules table.
     * @return the String Whether table is successfully created or not.
     */
    @PostMapping("/congestion_tax_rules/create")
    public ResponseEntity<String> createTable() {
        String result = congestionTaxService.create_table();
        if(result.equals("Table Created"))
        {
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        }
        else if(result.equals("Table Failed To Create"))
        {
            return new ResponseEntity<>(result, HttpStatus.NOT_IMPLEMENTED);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * This is a post request end point /congestion_tax_rules/insert.
     * This request is to insert congestion_tax_rules data.
     * @return the String Whether data is successfully inserted or not.
     */
    @PostMapping("/congestion_tax_rules/insert")
    public ResponseEntity<String> insertCongestionTaxRules(@RequestBody CongestionTaxRulesModel congestionTaxRulesModel) {
        String result = congestionTaxService.insert_row(congestionTaxRulesModel);
        if(result.equals("Inserted Successfully"))
        {
            return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
        }
        else if(result.equals("Failed to Insert"))
        {
            return new ResponseEntity<>(result, HttpStatus.NOT_IMPLEMENTED);
        }
        else if(result == "City Congestion Tax Rules Already Present")
        {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * This is a get request end point /congestion_tax_rules/read.
     * This request is to get congestion_tax_rules of all cities.
     * @return the String of congestion_tax_rules data or empty list.
     */
    @GetMapping("/congestion_tax_rules/read")
    public ResponseEntity<List<CongestionTaxRulesModel>> readCongestionTaxRules(
            @RequestParam(required = false) String city_name) {
        List<CongestionTaxRulesModel> list = congestionTaxService.read_all(city_name);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * This is a post request end point /congestion_tax_rules/drop.
     * This request to drop congestion_tax_rules table.
     * @return the String Whether table is successfully dropped or not.
     */
    @PostMapping("/congestion_tax_rules/drop")
    public ResponseEntity<String> dropCongestionTaxRules() {
        String result = congestionTaxService.drop_table();
        if(result.equals("Table Dropped Successfully"))
        {
            return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
        }
        else if(result.equals("Table Failed To Drop"))
        {
            return new ResponseEntity<>(result, HttpStatus.NOT_IMPLEMENTED);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
