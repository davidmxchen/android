/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

import java.util.Objects;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
public class OrderID implements Comparable<OrderID>{
    // number assigned to the order, no two orders have the same number unless they are splitted orders
    // of the same order
    int number;
    // prefix used to categorize type of order
    String prefix;
    // suffix used for splitted orders of this order, for combined order leave one emtpy
    String suffix;
    
    public OrderID() {
        this(1);
    }    

    public OrderID(int number) {
        this.number = number;
        prefix = "";
        suffix = "";
    }

    public OrderID(JSONObject object){
        this.number = object.optInt("number");
        this.prefix = object.optString("prefix");
        this.suffix = object.optString("suffix");
    }
    
    public OrderID(int number, String prefix, String suffix) {
        this.number = number;
        this.prefix = prefix;
        this.suffix = suffix;
    }



    @Override
    public String toString() {
        String id = "";
        if(!prefix.equals(""))
            id += prefix + "-";
        id += number;
        if(!suffix.equals(""))
            id += "-" + suffix;
        
        return id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean equals(Object obj){
        if(obj instanceof OrderID){
            OrderID id = (OrderID)obj;
            return prefix.equals(id.getPrefix()) && (number == id.getNumber()) && suffix.equals(id.getSuffix());
        }else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + this.number;
        hash = 71 * hash + Objects.hashCode(this.prefix);
        hash = 71 * hash + Objects.hashCode(this.suffix);
        return hash;
    }

    public JSONObject toJSONObject(){
        JSONObject object = new JSONObject();
        object.put("prefix", prefix);
        object.put("number", number);
        object.put("suffix", suffix);
        return object;
    }

    @Override
    public int compareTo(OrderID o) {
        System.out.println("orderID compareTo method calling now");
        if(number == o.getNumber()){
            return suffix.compareTo(o.getSuffix());
        }
        else if(number > o.getNumber())
            return 1;
        else
            return -1;
    }
}
