package com.example.Congestion_Tax_Calculator.Service;

import com.example.Congestion_Tax_Calculator.Model.CongestionTaxRulesModel;
import com.example.Congestion_Tax_Calculator.Repository.CongestionTaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@Service
public class CongestionTaxService {

    @Autowired
    CongestionTaxRepository congestionTaxRepository;


    public String create_table()
    {
        try {
            congestionTaxRepository.createCongestionTaxRulesTable();
            return "Table Created";
        }
        catch (Exception e)
        {
            System.out.println(e);
            return "Table Failed To Create";
        }
    }

    public String drop_table()
    {
        try {
            congestionTaxRepository.dropCongestionTaxRulesTable();
            return "Table Dropped Successfully";
        }
        catch (Exception e)
        {
            System.out.println(e);
            return "Table Failed To Drop";
        }
    }

    public String insert_row(CongestionTaxRulesModel congestionTaxRulesModel)
    {
        Optional<CongestionTaxRulesModel> cs = congestionTaxRepository.findById(congestionTaxRulesModel.getCity_name());
        if(cs.isEmpty() == true) {
            try {
                congestionTaxRepository.save(congestionTaxRulesModel);
                return "Inserted Successfully";
            } catch (Exception e) {
                System.out.println(e);
                return "Failed to Insert";
            }
        }
        else
        {
            return "City Congestion Tax Rules Already Present";
        }
    }

    public List<CongestionTaxRulesModel> read_all(String city_name)
    {
        List<CongestionTaxRulesModel> cs = new ArrayList<>();
        if(city_name == null)
        {
            try {
                cs = congestionTaxRepository.findAll();
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
        else
        {
            try {
                Optional<CongestionTaxRulesModel> cs1 = congestionTaxRepository.findById(city_name);
                cs.add(cs1.get());
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
        return cs;
    }
}
