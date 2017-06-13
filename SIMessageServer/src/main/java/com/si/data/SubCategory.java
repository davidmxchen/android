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
public class SubCategory extends SubItem{

    public SubCategory(JSONObject json) {
        super(json);
    }
    
    /**
     * Loosely implemented for subCategory comparison, as long as the name for display is the
     * same, then belong to the same SubCategory
     * @param other
     * @return 
     */
    @Override
    public boolean equals(Object other){
        if(!(other instanceof SubCategory))
            return false;
        SubCategory ot = (SubCategory)other;
        if(this.getDisplayName().equalsIgnoreCase(ot.getDisplayName()))
            return true;
        return false;
    }
    
    /**
     * All SubCategorys put on the same LinkList in HashTable
     * @return 
     */
    @Override
    public int hashCode(){
        // use only name for hashCode, the name shall never be null.
        return 20170316;
    }
    
    @Override
    public JSONObject toJSONObject(){
        return super.toJSONObject();
    }
}
