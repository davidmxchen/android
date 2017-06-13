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
public class Item extends SubItem{
    private SubCategories subCategories;
    private Printer printer; 
  
    public Item(JSONObject json){
        super(json);
        if(json.has("sub_category"))    // only applied to menu, not on specific item
            subCategories = new SubCategories(json);
        else
            subCategories = null;
        
        if(json.has("printer"))
            this.printer = new Printer(json.optJSONObject("printer"));  //???
        else
            this.printer = null;
    }

    // make copy of the item
    public Item(Item it){
        this(it.toJSONObject());
    }
    
    public Item(String itemName, String price, String description){
        super(itemName, price, description);
    }
    
    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    public Printer getPrinter() {
        return printer;
    }

    public SubCategories getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(SubCategories subCategories) {
        this.subCategories = subCategories;
    }
    
    public JSONObject toJSONObject(){
        JSONObject obj = super.toJSONObject();
        if(subCategories != null)
            obj.put("sub_category", subCategories.toJSONArray());
        if(printer != null)
            obj.put("printer", printer.toJSONObject());
        return obj;
    }
}
