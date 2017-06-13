/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

import org.json.JSONObject;
import java.math.BigDecimal;

/**
 *
 * @author Mingxing Chen
 */
public class OtherCharge {    
    // amount of the OtherChage
    BigDecimal amount;     
    BigDecimal percent;
    String name;    
    private ChargeType chargeType;

    public OtherCharge() {
        this("", new BigDecimal(0.00));
    }
    
    public OtherCharge(String name, BigDecimal amount) {
        this.name = name;
        this.amount = amount;
        this.percent = BigDecimal.ZERO;
        chargeType = ChargeType.AMOUNT; // default
    }

    public OtherCharge(String name, ChargeType type){
        this.name = name;
        this.chargeType = type;
        this.amount = new BigDecimal(0.00);
        this.percent = BigDecimal.ZERO;
    }
    
    public OtherCharge(JSONObject object){
        this();
        if(object != null){
            this.name = object.optString("name");
            this.amount = new BigDecimal(object.optString("amount"));   
            this.chargeType = ChargeType.valueOf(object.optString("charge_type").toUpperCase());
            this.percent = new BigDecimal(object.optString("percent"));
        }
    }
     
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public void setAmount(String amountStr){   
        this.amount = new BigDecimal(amountStr);
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public ChargeType getChargeType() {
        return chargeType;
    }

    public void setChargeType(ChargeType chargeType) {
        this.chargeType = chargeType;
    }
    
    @Override
    public String toString() {
        return name + ": " + amount;
    }
    
    public JSONObject toJSONObject(){
        JSONObject object = new JSONObject();
        object.put("name", name);
        object.put("amount", amount);
        object.put("percent", percent);
        object.put("charge_type", chargeType.toString());
        
        return object;
    }
}
