package com.example.Congestion_Tax_Calculator.Service;

import com.example.Congestion_Tax_Calculator.Model.CongestionTaxRulesModel;
import com.example.Congestion_Tax_Calculator.Repository.CongestionTaxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@Service
@Slf4j
public class CongestionTaxService {

    @Autowired
    CongestionTaxRepository congestionTaxRepository;


    public String createTable()
    {
        try {
            congestionTaxRepository.createCongestionTaxRulesTable();
            return "Table Created";
        }
        catch (Exception e)
        {
            log.trace(String.valueOf(e));
            return "Table Failed To Create";
        }
    }

    public String dropTable()
    {
        try {
            congestionTaxRepository.dropCongestionTaxRulesTable();
            return "Table Dropped Successfully";
        }
        catch (Exception e)
        {
            log.trace(String.valueOf(e));
            return "Table Failed To Drop";
        }
    }

    public String insertRow(CongestionTaxRulesModel congestionTaxRulesModel)
    {
        Optional<CongestionTaxRulesModel> cs = congestionTaxRepository.findById(congestionTaxRulesModel.getCityName());
        if(cs.isEmpty()) {
            try {
                congestionTaxRepository.save(congestionTaxRulesModel);
                return "Inserted Successfully";
            } catch (Exception e) {
                log.trace(String.valueOf(e));
                return "Failed to Insert";
            }
        }
        else
        {
            return "City with Congestion Tax Rules Already Present";
        }
    }

    public List<CongestionTaxRulesModel> readAll(String cityName)
    {
        List<CongestionTaxRulesModel> cs = new ArrayList<>();
        if(cityName == null)
        {
            try {
                cs = congestionTaxRepository.findAll();
            }
            catch (Exception e)
            {
                log.trace(String.valueOf(e));
            }
        }
        else
        {
            try {
                Optional<CongestionTaxRulesModel> cs1 = congestionTaxRepository.findById(cityName);
                if(cs1.isPresent())
                {
                    cs.add(cs1.get());
                }

            }
            catch (Exception e)
            {
                log.trace(String.valueOf(e));
            }
        }
        return cs;
    }
}
