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
public class Selection {
    
    private final ArrayList<Item> items = new ArrayList<>();
    private SubItem parentItem;
 //   private final JSONArray selection;
    
    public Selection(){ 
  //      items = new ArrayList<>();
 //       selection = new JSONArray();        
    }
    
    /**
     * Create Selection from JSONObject
     * @param json 
     */
    public Selection(JSONObject json){
        JSONArray selection = json.optJSONArray("selection");
        if(selection != null)
            for(int i=0; i<selection.length(); i++)
                items.add(new Item(selection.optJSONObject(i)));
    }
    
    /**
     * Create Selection from JSONObject and set its containing parent SubItem
     * @param json
     * @param parent 
     */
    public Selection(JSONObject json, SubItem parent){
        this.parentItem = parent;
        JSONArray selection = json.optJSONArray("selection");
        if(selection != null){
            for(int i=0; i<selection.length(); i++){
                items.add(new Item(selection.optJSONObject(i)));
            }
        }
    }

    /**
     * List of Items in Selection
     * @return list of Items or empty List of Item
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    public SubItem getParentItem() {
        return parentItem;
    }

    public void setParentItem(SubItem parentItem) {
        this.parentItem = parentItem;
    }

    /**
     * JSONArray of Selection
     * @return never null
     */
    public JSONArray toJSONArray() { // don't return selection, although it's a JSONArray
        //return selection;
        JSONArray array = new JSONArray();
        for(Item item:items){
            array.put(item.toJSONObject());
        }
        return array;
    }  
    
    public JSONObject toJSONObject(){
        return new JSONObject().put("selection", toJSONArray());
    }
    
    /**
     * Item is added only when there is a "selection"
     * @param item 
     */
    public void addItem(Item item){   
            items.add(item);
    }
   
}
