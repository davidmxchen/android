/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
 public enum ItemStatus {
     NEW("NEW"),
     SAVED("SAVED"),
     SENT("SENT"), 
     SPLIT("SPLIT"),
     PRINTED("PRINTED"),
     DELETED("DELETED");

     private final String status;
    
     ItemStatus(String status) {
         this.status = status;
     }
    
     public JSONObject toJSONObject(){
         return new JSONObject().put("item_status", status);
     }
    
     public String toString(){         
         return status;
     }
    
}
