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
public class ModifierCategory{

    private final ArrayList<Category> catList = new ArrayList<>();
    //private JSONArray modifierJSONArry;
    
    public ModifierCategory(){
  //      modifierJSONArry = new JSONArray();
    }

    public ModifierCategory(JSONObject json, Category parent) {
        JSONArray array = json.optJSONArray("modifier_category");
        if(array != null)
            for(int i=0; i<array.length(); i++){
                Category cat = new Category(array.optJSONObject(i));
                cat.setCategory(parent);
                catList.add(cat);  
            }
    }
    
    /**
     * Create a ModifierCategory with no parent Category
     * @param json 
     */
    public ModifierCategory(JSONObject json) {
        JSONArray array = json.optJSONArray("modifier_category");
        if(array != null)
            for(int i=0; i<array.length(); i++){
                catList.add(new Category(array.optJSONObject(i)));  
            }
    }
    
    public ArrayList<Category> getCategorys(){
        return this.catList;
    }
    
    public void addCategory(Category category){
        catList.add(category);
        //modifierJSONArry.put(category);
    }
    
    public void addCategory(Category category, int index){
        catList.add(index, category);
        //modifierJSONArry.put(index, category);
    }
    
    public JSONArray toJSONArray(){
        JSONArray array = new JSONArray();
        for(Category cat:catList){
            array.put(cat.toJSONObject());
        }
        return array;
    }
    
}
