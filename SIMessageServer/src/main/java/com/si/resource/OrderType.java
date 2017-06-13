/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.resource;

import java.util.Locale;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
public enum OrderType {
    DINE_IN("Dine In", "堂吃"),
    WALK_IN("Walk In", "外等"),
    PICK_UP("Pick Up", "来取"), 
    DELIVERY("Delivery", "外送"),
    ONLINE_PICK_UP("Online Pick Up", "来取--Online"),
    ONLINE_DELIVERY("Online Delivery", "外送--Online");

    private final String en;
    private final String zh;
    public static Locale locale = Locale.ENGLISH;

    private OrderType() {
        this.en = "";
        this.zh = "";
    }    
    
    OrderType(String en, String zh) {
        this.en = en;
        this.zh = zh;
    }
    
    public String getEN(){
        return en;
    }
    
    public String getCN(){
        return zh;
    }
    
    public JSONObject toJSONObject(){
        JSONObject object = new JSONObject();
        object.put("en", en);
        object.put("zh", zh);
        return object;
    }
    
    public String toString(){
        if(locale.equals(Locale.ENGLISH))
            return en;
        else
            return zh;
    }
    
    public static OrderType valueOfName(String name){
        OrderType typeOfName = null;
        if(locale.equals(Locale.ENGLISH)){
            for(OrderType type: OrderType.values()){
                if(name.equals(type.getEN()))
                    typeOfName = type;
            }
        }else {
            for(OrderType type: OrderType.values()){
                if(name.equals(type.getCN()))
                    typeOfName = type;
            }
        }
        return typeOfName;
    }
    
    /**
     * Get the String representation of OrderType, such OrderType.DINE_IN is represented as "DINE_IN"
     * @param orderType
     * @return String of OrderType name
     */
    public static String getName(OrderType orderType){
        if(orderType == OrderType.DINE_IN)
            return "DINE_IN";
        else if(orderType == OrderType.DELIVERY)
            return "DELIVERY";
        else if(orderType ==  OrderType.ONLINE_DELIVERY)
            return "ONLINE_DELIVERY";
        else if (orderType == OrderType.ONLINE_PICK_UP)
            return "ONLINE_PICK_UP";
        else if (orderType == OrderType.PICK_UP)
            return "PICK_UP";
        else if(orderType == OrderType.WALK_IN)
            return "WALK_IN";
        else 
            return null;
    }

    /**
     * Get OrderType by it's name representation, such as when String value is "DINE_IN", the OrderType is OrderType.DINE_IN
     * @param value represents OrderType, must be in upper case
     * @return OrderType of the value name represents, null if value not match
     */
    public static OrderType getValue(String value){
        String nValue = value.toUpperCase();
        switch (nValue) {
            case "DINE_IN":
                return OrderType.DINE_IN;
            case "DELIVERY":
                return OrderType.DELIVERY;
            case "ONLINE_DELIVERY":
                return OrderType.ONLINE_DELIVERY;
            case "ONLINE_PICK_UP":
                return OrderType.ONLINE_PICK_UP;
            case "PICK_UP":
                return OrderType.PICK_UP;
            case "WALK_IN":
                return OrderType.WALK_IN;
            default:
                return null;
        }
    }
}
