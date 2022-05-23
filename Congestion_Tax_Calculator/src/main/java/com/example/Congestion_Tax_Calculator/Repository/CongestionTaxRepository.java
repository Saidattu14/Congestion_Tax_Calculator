package com.example.Congestion_Tax_Calculator.Repository;


import com.example.Congestion_Tax_Calculator.Model.CongestionTaxRulesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface CongestionTaxRepository extends JpaRepository<CongestionTaxRulesModel,String> {
    /**
     * This method is SQL Query Where it creates CongestionTaxRules Table.
     */
    @Modifying
    @Transactional
    @Query(value = "CREATE TABLE congestion_tax_rules(\n" +
            "  city_name varchar(255) NOT NULL PRIMARY KEY,\n" +
            "  single_charge_rule boolean,\n" +
            "  tax_details_on_time_600_to_629 int,\n" +
            "  tax_details_on_time_630_to_659 int,\n" +
            "  tax_details_on_time_700_to_759 int,\n" +
            "  tax_details_on_time_800_to_829 int,\n" +
            "  tax_details_on_time_830_to_1459 int,\n" +
            "  tax_details_on_time_1500_to_1529 int,\n" +
            "  tax_details_on_time_1530_to_1659 int,\n" +
            "  tax_details_on_time_1700_to_1759 int,\n" +
            "  tax_details_on_time_1800_to_1829 int,\n" +
            "  tax_details_on_time_1830_to_0559 int \n" +
            ")",nativeQuery = true)
    void createCongestionTaxRulesTable();


    /**
     * This method is SQL Query Where it drops CongestionTaxRules Table.
     */
    @Modifying
    @Transactional
    @Query(value = "drop table congestion_tax_rules",nativeQuery = true)
    void dropCongestionTaxRulesTable();

}
