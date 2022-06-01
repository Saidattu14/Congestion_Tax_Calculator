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
            "  cityName varchar(255) NOT NULL PRIMARY KEY,\n" +
            "  singleChargeRule boolean,\n" +
            "  taxDetailsOnTime600to629 int,\n" +
            "  taxDetailsOnTime630to659 int,\n" +
            "  taxDetailsOnTime700to759 int,\n" +
            "  taxDetailsOnTime800to829 int,\n" +
            "  taxDetailsOnTime830to1459 int,\n" +
            "  taxDetailsOnTime1500to1529 int,\n" +
            "  taxDetailsOnTime1530to1659 int,\n" +
            "  taxDetailsOnTime1700to1759 int,\n" +
            "  taxDetailsOnTime1800to1829 int,\n" +
            "  taxDetailsOnTime1830to0559 int \n" +
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
