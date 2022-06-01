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
    @Column(name = "cityname", nullable = false, insertable = true)
    private String cityName;
    @Column(name = "singlechargerule")
    private boolean  singleChargeRule;
    @Column(name = "taxdetailsontime600to629")
    private int taxDetailsOnTime600to629;
    @Column(name = "taxdetailsontime630to659")
    private int taxDetailsOnTime630to659;
    @Column(name = "taxdetailsontime700to759")
    private int taxDetailsOnTime700to759;
    @Column(name = "taxdetailsontime800to829")
    private int taxDetailsOnTime800to829;
    @Column(name = "taxdetailsontime830to1459")
    private int taxDetailsOnTime830to1459;
    @Column(name = "taxdetailsontime1500to1529")
    private int taxDetailsOnTime1500to1529;
    @Column(name = "taxdetailsontime1530to1659")
    private int taxDetailsOnTime1530to1659;
    @Column(name = "taxdetailsontime1700to1759")
    private int taxDetailsOnTime1700to1759;
    @Column(name = "taxdetailsontime1800to1829")
    private int taxDetailsOnTime1800to1829;
    @Column(name = "taxdetailsontime1830to0559")
    private int taxDetailsOnTime1830to0559;

    public CongestionTaxRulesModel() {
    }


    public boolean isSingleChargeRule() {
        return singleChargeRule;
    }



    public String getCityName() {
        return cityName;
    }

    public CongestionTaxRulesModel
            (String cityName, boolean singleChargeRule,
                       int taxDetailsOnTime600to629, int taxDetailsOnTime630to659,
                       int taxDetailsOnTime700to759, int taxDetailsOnTime800to829,
                       int taxDetailsOnTime830to1459, int taxDetailsOnTime1500to1529,
                       int taxDetailsOnTime1530to1659,
                       int taxDetailsOnTime1700to1759, int taxDetailsOnTime1800to1829,
                       int taxDetailsOnTime1830to0559)
    {
        this.cityName = cityName;
        this.singleChargeRule = singleChargeRule;
        this.taxDetailsOnTime600to629 = taxDetailsOnTime600to629;
        this.taxDetailsOnTime630to659 = taxDetailsOnTime630to659;
        this.taxDetailsOnTime700to759 = taxDetailsOnTime700to759;
        this.taxDetailsOnTime800to829 = taxDetailsOnTime800to829;
        this.taxDetailsOnTime830to1459 = taxDetailsOnTime830to1459;
        this.taxDetailsOnTime1500to1529 = taxDetailsOnTime1500to1529;
        this.taxDetailsOnTime1530to1659 = taxDetailsOnTime1530to1659;
        this.taxDetailsOnTime1700to1759 = taxDetailsOnTime1700to1759;
        this.taxDetailsOnTime1800to1829 = taxDetailsOnTime1800to1829;
        this.taxDetailsOnTime1830to0559 = taxDetailsOnTime1830to0559;
    }

    public int getTaxDetailsOnTime600to629() {
        return taxDetailsOnTime600to629;
    }

    public int getTaxDetailsOnTime630to659() {
        return taxDetailsOnTime630to659;
    }

    public int getTaxDetailsOnTime700to759() {
        return taxDetailsOnTime700to759;
    }

    public int getTaxDetailsOnTime800to829() {
        return taxDetailsOnTime800to829;
    }

    public int getTaxDetailsOnTime830to1459() {
        return taxDetailsOnTime830to1459;
    }

    public int getTaxDetailsOnTime1500to1529() {
        return taxDetailsOnTime1500to1529;
    }

    public int getTaxDetailsOnTime1530to1659() {
        return taxDetailsOnTime1530to1659;
    }

    public int getTaxDetailsOnTime1700to1759() {
        return taxDetailsOnTime1700to1759;
    }

    public int getTaxDetailsOnTime1800to1829() {
        return taxDetailsOnTime1800to1829;
    }

    public int getTaxDetailsOnTime1830to0559() {
        return taxDetailsOnTime1830to0559;
    }
}
