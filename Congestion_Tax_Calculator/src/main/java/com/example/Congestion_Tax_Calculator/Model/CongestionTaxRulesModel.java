package com.example.Congestion_Tax_Calculator.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
* This is a CongestionTaxRulesModel class which holds information about congestion_tax_rules table row data.
*/
@Entity
@Table(name = "congestion_tax_rules")
public class CongestionTaxRulesModel {

   /**
   * This is Primary Key value of Table.
   */
    @Id
    @Column(name = "city_name", nullable = false, insertable = true)
    private String city_name;
    private boolean  single_charge_rule;
    private int tax_details_on_time_600_to_629;
    private int tax_details_on_time_630_to_659;
    private int tax_details_on_time_700_to_759;
    private int tax_details_on_time_800_to_829;
    private int tax_details_on_time_830_to_1459;
    private int tax_details_on_time_1500_to_1529;
    private int tax_details_on_time_1530_to_1659;
    private int tax_details_on_time_1700_to_1759;
    private int tax_details_on_time_1800_to_1829;
    private int tax_details_on_time_1830_to_0559;

    public CongestionTaxRulesModel() {
    }


    public boolean isSingle_charge_rule() {
        return single_charge_rule;
    }



    public String getCity_name() {
        return city_name;
    }

    public CongestionTaxRulesModel
            (String city_name, boolean single_charge_rule,
                       int tax_details_on_time_600_to_629, int tax_details_on_time_630_to_659,
                       int tax_details_on_time_700_to_759, int tax_details_on_time_800_to_829,
                       int tax_details_on_time_830_to_1459, int tax_details_on_time_1500_to_1529,
                       int tax_details_on_time_1530_to_1659,
                       int tax_details_on_time_1700_to_1759, int tax_details_on_time_1800_to_1829,
                       int tax_details_on_time_1830_to_0559)
    {
        this.city_name = city_name;
        this.single_charge_rule = single_charge_rule;
        this.tax_details_on_time_600_to_629 = tax_details_on_time_600_to_629;
        this.tax_details_on_time_630_to_659 = tax_details_on_time_630_to_659;
        this.tax_details_on_time_700_to_759 = tax_details_on_time_700_to_759;
        this.tax_details_on_time_800_to_829 = tax_details_on_time_800_to_829;
        this.tax_details_on_time_830_to_1459 = tax_details_on_time_830_to_1459;
        this.tax_details_on_time_1500_to_1529 = tax_details_on_time_1500_to_1529;
        this.tax_details_on_time_1530_to_1659 = tax_details_on_time_1530_to_1659;
        this.tax_details_on_time_1700_to_1759 = tax_details_on_time_1700_to_1759;
        this.tax_details_on_time_1800_to_1829 = tax_details_on_time_1800_to_1829;
        this.tax_details_on_time_1830_to_0559 = tax_details_on_time_1830_to_0559;
    }

    public int getTax_details_on_time_600_to_629() {
        return tax_details_on_time_600_to_629;
    }

    public int getTax_details_on_time_630_to_659() {
        return tax_details_on_time_630_to_659;
    }

    public int getTax_details_on_time_700_to_759() {
        return tax_details_on_time_700_to_759;
    }

    public int getTax_details_on_time_800_to_829() {
        return tax_details_on_time_800_to_829;
    }

    public int getTax_details_on_time_830_to_1459() {
        return tax_details_on_time_830_to_1459;
    }

    public int getTax_details_on_time_1500_to_1529() {
        return tax_details_on_time_1500_to_1529;
    }

    public int getTax_details_on_time_1530_to_1659() {
        return tax_details_on_time_1530_to_1659;
    }

    public int getTax_details_on_time_1700_to_1759() {
        return tax_details_on_time_1700_to_1759;
    }

    public int getTax_details_on_time_1800_to_1829() {
        return tax_details_on_time_1800_to_1829;
    }

//    public void setCity_name(String city_name) {
//        this.city_name = city_name;
//    }

    public int getTax_details_on_time_1830_to_0559() {
        return tax_details_on_time_1830_to_0559;
    }
}
