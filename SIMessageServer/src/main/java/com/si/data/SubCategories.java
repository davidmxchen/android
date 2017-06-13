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
public class SubCategories {
    private final ArrayList<SubCategory> subCatList = new ArrayList<>();;
    
    public SubCategories(){
       
    }
    
    public SubCategories(JSONObject json, Category cat){
        if(json.has("sub_category")){
            JSONArray array = json.optJSONArray("sub_category");
            if(array != null){
                for(int i=0; i<array.length(); i++){
                    SubCategory subCat = new SubCategory(array.optJSONObject(i));
                    subCat.setCategory(cat);
                    subCatList.add(subCat);
                }
            }
        }
    }
    
    public SubCategories(JSONObject json){
        if(json.has("sub_category")){
            JSONArray array = json.optJSONArray("sub_category");
            if(array != null){
                for(int i=0; i<array.length(); i++){
                    subCatList.add(new SubCategory(array.optJSONObject(i)));
                }
            }
        }
    }
    
    public ArrayList<SubCategory> getSubCategorys(){
        return subCatList;
    }
    
    public JSONArray toJSONArray(){
        JSONArray array = new JSONArray();
        for(SubCategory item:subCatList)
            array.put(item.toJSONObject());
        return array;
    }
}
