/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.transaction.api;

/**
 *
 * @author Mingxing Chen
 */

/**
 * "merchantAuthentication": {
 * "name": "API_LOGIN_ID",
 * "transactionKey": "API_TRANSACTION_KEY"
 * }
 * 
 */
public class MerchantAuthentication {
    private final String name;
    private final String transactionKey;
    private String employeeId;

    
    private MerchantAuthentication(){
        name = "";
        transactionKey = "";
    }
    
    public MerchantAuthentication(String name, String key){
        this.name = name;
        this.transactionKey = key;
    }

    public String getName() {
        return name;
    }

    public String getTransactionKey() {
        return transactionKey;
    }
    
    public String getLogInID(){
        return name;
    }
  
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }  
}
