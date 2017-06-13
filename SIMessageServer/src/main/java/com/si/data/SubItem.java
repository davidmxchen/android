/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
public class SubItem {
    
    private Item parentItem;
    private JSONObject name;   
    private String price;
    private String description;
//    private JSONObject json;    
    private Category parentCategory;
    
    protected Selection selection;
    protected Choice choice;
    protected String attribute;
    protected JSONObject sendTo;
    public static Language language;    
    
    public SubItem(){
        name = new JSONObject();
        this.price = "";
        this.description = "";
    }
    
    public SubItem(Name name1){
        this.name = name1.toJSONObject();
        this.price = "";
        this.description = "";
    }
    
    public SubItem(String itemName, String price, String description) {
        name = new JSONObject().put("en", itemName);
        this.price = price;
        this.description = description;
    }
    
    /**
     * Create a new SubItem from JSON object
     * @param json 
     */
    public SubItem(JSONObject json){
        // name must be no null even not exist in json object
        // create new JSONObject for name instead of reference to
        this.name = new JSONObject(json.optJSONObject("name").toString());
        if(json.has("price"))
            this.price = json.optString("price");
        if(json.has("description"))
            this.description = json.optString("description");
        if(json.has("send_to"))
            this.sendTo = new JSONObject(json.optJSONObject("send_to"));
//        this.json = json;
        // create Selection of this SubItem and set this as parent item
        if(json.has("selection")) // selection is json.optJSONArray("selection")
            this.selection = new Selection(json, this);
    }
    
//    public SubItem(SubItem subItem){
//        new this(subItem.toJSONObject());
//    }
    
    /**
     * Add the other SubItem to this subItem and return a new SubItem
     * @param subItem
     * @return 
     */
    public SubItem add(SubItem otherSubItem){
        System.out.println("begin with: " + toJSONObject());
        System.out.println("add with: " + otherSubItem.toJSONObject());
        // copy this
        SubItem newItem = new SubItem(this.toJSONObject());
        SubItem otherItem = new SubItem(otherSubItem.toJSONObject());
        
        // add name, compare name's size
        JSONObject  newItemName = newItem.getName();
        JSONObject otherItemName = otherItem.getName();
        Iterator<String> key;
        if(newItemName.keySet().size() >= otherItemName.keySet().size())        
            key = newItemName.keys();
        else
            key = otherItemName.keys();
        while(key.hasNext()){
            String tempKey = key.next();
            String value = newItemName.optString(tempKey).trim() + " " + otherItemName.optString(tempKey).trim();           
            // replace or add new key-value pair
            newItemName.put(tempKey, value.trim());
        }
        
        // description
        if(otherItem.getDescription() != null){
            if(newItem.getDescription() == null)
                newItem.setDescription(otherItem.getDescription());
            else
                newItem.setDescription((newItem.getDescription() + " " + otherItem.getDescription()).trim());
        }
        
        
        // special handle case for price
        if(newItem.getPrice() != null){ // not applicable if this has no "price" tag
            double d1 = 0.0d, d2= 0.0d;
            if(!newItem.getPrice().trim().isEmpty())
                d1 = new Double(newItem.getPrice()).doubleValue();
            if(otherItem.getPrice() != null && !otherItem.getPrice().isEmpty())
                d2 = new Double(otherItem.getPrice()).doubleValue();
            double total = d1 + d2;
            newItem.setPrice(new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).toPlainString());   
        } 
        
        System.out.println("new subitem json string:" + newItem.toJSONObject().toString());
        return newItem;
    }
    
    public JSONObject getSendTo() {
        return sendTo;
    }

    public void setSendTo(JSONObject sendTo) {
        this.sendTo = sendTo;
    }
    
    public Category getCategory() {
        return parentCategory;
    }

    public void setCategory(Category category) {
        this.parentCategory = category;
    }
    
    public void setParent(Item parent) {
        this.parentItem = parent;
    }

    public void setName(JSONObject name) {
        this.name = name;
    }

    public void setName(Name name){
        this.name = name.toJSONObject();
    }
    
    public void setLanguage(Language language) {
        SubItem.language = language;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Item getParent() {
        return parentItem;
    } 

//    public JSONObject getJson() {
//        if(json == null)
//            json = this.toJSONObject();
//        return json;
//    }

    public void setParentItem(Item parentItem) {
        this.parentItem = parentItem;
    }

//    public void setJson(JSONObject json) {
//        this.json = json;
//    }

    public JSONObject getName() {
        return name;
    }

    public String getName(String n){
        return name.optString(n);
    }
    
    public String getDisplayName(){
        if(language == null)    // default english
            language = new Language();
        return getName(language.getDisplayName());
    }
    
    public Language getLanguage() {
        return SubItem.language;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

//    public JSONObject getItemJSONObject(){
//        return json;
//    }
    
    public void setSelection(Selection selection){
        this.selection = selection;
    }
    
    public boolean hasSubItem(){
        return selection != null;
    }
    
    public ArrayList<Item> getSubItems(){
        return hasSubItem() ? selection.getItems() : null;
    }

    public Item getParentItem() {
        return parentItem;
    }

    public Selection getSelection() {
        return selection;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
    }

    public Choice getChoice() {
        return choice;
    }
    
    public boolean hasChoice(){
        return choice != null;
    }
    
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
    
    public String getCatName() {
        return parentCategory.getDisplayName();
    }

    
    public JSONObject toJSONObject(){
        JSONObject jObj = new JSONObject();
        jObj.put("name", name);
        if(price != null && !price.isEmpty())
            jObj.put("price", price);
        if(description != null && !description.isEmpty())
            jObj.put("description", description);
        if(sendTo != null)
            jObj.put("send_to", sendTo);
        if(selection != null)
            jObj.put("selection", selection.toJSONArray());
        if(choice != null)
            jObj.put("choice", choice.toJSONObject());
        return jObj;
    }

    @Override
    public boolean equals(Object another){
        if(!(another instanceof SubItem))
            return false;
        SubItem other = (SubItem)another;
        if(this == other)
            return true;
        JSONObject thisObject = this.toJSONObject();
        JSONObject thatObject = other.toJSONObject();
        if(thisObject.equals(thatObject) || thisObject.toString().equals(thatObject.toString()))
            return true;
        return false;
    }
    
    /**
     * The general contract of hashCode is:
     * 1. Whenever it is invoked on the same object more than once during an execution of a Java application, 
     * the hashCode method must consistently return the same integer, provided no information used in equals 
     * comparisons on the object is modified. This integer need not remain consistent from one execution of 
     * an application to another execution of the same application.
     * 2. If two objects are equal according to the equals(Object) method, then calling the hashCode method 
     * on each of the two objects must produce the same integer result.
     * 3. It is not required that if two objects are unequal according to the equals(java.lang.Object) method,
     * then calling the hashCode method on each of the two objects must produce distinct integer results. 
     * However, the programmer should be aware that producing distinct integer results for unequal objects 
     * may improve the performance of hash tables.
     * @return 
     */
    @Override
    public int hashCode(){
        // use only name for hashCode, the name shall never be null.
        return name.hashCode();
    }
    
    @Override
    public String toString(){
        if(price != null && !price.trim().isEmpty()){
            System.out.println("price is: " + price);
            if(new BigDecimal(price).compareTo(new BigDecimal(0.0)) > 0)// more than zero amount
                return getDisplayName() + " $" + price;
            else
                return getDisplayName();
        }
        return getDisplayName();
    } 


}
