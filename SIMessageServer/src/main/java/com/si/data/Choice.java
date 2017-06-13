/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen 
 */
public class Choice {
    private int number;
    private final ArrayList<Item> items;
    
    public Choice(){
        items =  new ArrayList<>();        
    }
    
    public Choice(JSONObject json){
        JSONObject object = json.optJSONObject("choice");
        number = object.optInt("number");        
        items = new Items(object).getItems();        
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ArrayList<Item> getItems() {
        return items;
    }
    
    public JSONArray toJSONArray(){
        JSONArray array = new JSONArray();
        for(Item item:items)
            array.put(item.toJSONObject());
        return array;
    }
    
    public JSONObject toJSONObject(){
        JSONObject object = new JSONObject();
        object.put("number", number);
        
        JSONArray array = new JSONArray();
        for(Item item:items)    // allow empty array
            array.put(item.toJSONObject());
        object.put("item", array);
        
        return object;
    }
    
    public void add(Item item){
        items.add(item);
    }
    
    public void add(int index, Item item){
        items.add(index, item);
    }
}
