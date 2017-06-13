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
public class Items {

    private final ArrayList<Item> items = new ArrayList<>();
    
    
    public Items(JSONObject json, Category catgory){
        JSONArray array = json.optJSONArray("item");
        if(array != null){
            for(int i=0; i<array.length(); i++){
                JSONObject obj = array.optJSONObject(i);
                if(obj != null){
                    Item item = new Item(obj);
                    item.setCategory(catgory);
                    
//                    if(obj.has("selection")){
//                        Selection selection = new Selection(obj);
//                        item.setSelection(selection);
//                    }
 
                    if(obj.has("choice")){
                        Choice choice = new Choice(obj);
                        item.setChoice(choice);
                    }
                    
                    items.add(item); //add(subItem);
                }
            }
        }
    }
    
    public Items(JSONObject json){
        JSONArray array = json.optJSONArray("item");
        if(array != null){
            for(int i=0; i<array.length(); i++){
                JSONObject obj = array.optJSONObject(i);
                if(obj != null){
                    Item item = new Item(obj);
                    
//                    if(obj.has("selection")){
//                        Selection selection = new Selection(obj);
//                        item.setSelection(selection);
//                    }
 
                    if(obj.has("choice")){
                        Choice choice = new Choice(obj);
                        item.setChoice(choice);
                    }
                    items.add(item); //add(subItem);
                }
            }
        }
    }

    public ArrayList<Item> getItems() {
        return items;
    }
    
    public int size(){
        return items.size();
    }
    
    public JSONArray toJSONArray(){
        JSONArray array = new JSONArray();
        for(Item item:items)
            array.put(item.toJSONObject());
        return array;
    }
}
