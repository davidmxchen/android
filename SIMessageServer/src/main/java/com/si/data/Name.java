/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

import java.util.Iterator;
import java.util.Set;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
public class Name {
    private final JSONObject name;
    
    public Name(){
        name = new JSONObject();
    }
    
    public Name(JSONObject name){
        this.name = name.optJSONObject("name");
    }
    
    public String getName(Language language){
        if(name != null)
            return name.optString(language.getName());
        else
            return null;
    }
    
    public JSONObject toJSONObject(){
        return new JSONObject().put("name", name);
    }
    
    public void addName(Language language, String nameString){
        name.put(language.getName(), nameString);
    }
    
    public void addName(String lanStr, String nameStr){
        name.put(lanStr, nameStr);
    }

    public JSONObject getJSONObject(){
        return name;
    }
    
    public boolean equal(Name another){
        if(this.toJSONObject().toString().equals(another.toJSONObject().toString())){
            System.out.println("same json string");
            return true;
        }
        Set set1 = this.name.keySet();
        Set set2 = another.getJSONObject().keySet();
        if(set1.size() == set2.size()){
            // check all key values
            Iterator iterator = this.name.keys();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                if(!this.name.getString(key).equals(another.getJSONObject().getString(key))){
                    return false;
                }                
            }
            return true;
        }
          
        return false;
    }
}
