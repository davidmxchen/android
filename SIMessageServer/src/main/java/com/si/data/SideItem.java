/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

//import com.si.util.*;
import java.util.Locale;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
public class SideItem {
    private JSONObject name;
    private String price = "";
    private String description;
    public static Locale locale;    
    
    public SideItem(JSONObject json){
        this.name = json.getJSONObject("name");
        this.price = json.optString("price");
        this.description = json.optString("description");
    }
    
    public void setPrice(String price){
       this.price = price;
    }
    
    public String getPrice(){
        return price;
    }
    
    public void setName(JSONObject name){
        this.name = name;
    }
    
    public JSONObject getName(){
        return name;
    }

    public String getName(Locale locale1){
        return name.optString(locale1.getDisplayLanguage());
    }
    
    public String getDisplayName(){
        return getName(locale);
    }
    
    public String getDescription() {
        return description;
    }

    public static Locale getLocale() {
        return locale;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static void setLocale(Locale locale) {
        SideItem.locale = locale;
    }
        

}
