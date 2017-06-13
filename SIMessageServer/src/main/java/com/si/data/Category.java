/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
public class Category extends SubItem {
    private Items catItems;
    // Some category has too many items, its hard to find in one display, divide items into
    // several sub-categories
    private SubCategories subCategories;
    
    // One group category contains more than one category, used to group several categories into one
    // for easy display in one category button, such as lunch special contain different style lunches in fusion
    // restaurant. Group Category  is a list of Category Object.
    private GroupCategory groupCategory;
    
    private ModifierCategory modifierCategory;
    // for each subCat of the category, add items belong to the subCat to a ArrayList and map the list to subCat
    private HashMap<SubCategory, ArrayList<Item>> subCatItemsMap;
    private boolean isGroupCategory = false;
    private boolean isModifierCategory = false;
    
    public Category(){
        super();
    }
    
    /*
    * Create Category with JSONObject
    */
    public Category(JSONObject json) {
        super(json);
        // Several small categories which has very few items can put together to form a group category
        // When a Category has too many items, it's divided to several sub categories
        // if a category is a group category, any category of the group cannot contain SubCategory
        // A modifier category can be neither group nor sub category
        if(json.has("group_category")){
            groupCategory = new GroupCategory(json, this);
            isGroupCategory = true; 
        }else if(json.has("sub_category")){
            subCategories = new SubCategories(json, this);
            isGroupCategory = false;
        }else if(json.has("modifier_category")){
            modifierCategory = new  ModifierCategory(json, this);
            isModifierCategory = true;
        }// else if just normal category
        
        //category must contain "item":[...], and may contains "selection":[...], this is the selection of the Category.
//        if(json.has("selection"))
//            selection = new Selection(json);    
//        else
//            selection = null;
        
        // A category must contain 0 or more items
        if(json.has("item"))
            catItems = new Items(json, this);
        else
            catItems = new Items(new JSONObject());  // empty Items with no item to ensure catItems can never be null
        
        if(subCategories != null && catItems != null)
            createSubCatMap();
        else
            subCatItemsMap = null;
    }
        
    private void createSubCatMap(){
        // create map and initial data
        subCatItemsMap = new HashMap<>();
        for(SubCategory subCat:subCategories.getSubCategorys()){
            subCatItemsMap.put(subCat, new ArrayList<Item>());
        } 
        for(Item item:catItems.getItems()){ // item may has no sub category
            // item may belong to one or more sub category, need to be added to different subCat
            SubCategories itemSubCats = item.getSubCategories();
            if(itemSubCats != null){
                for(SubCategory subCat:itemSubCats.getSubCategorys() ){
                    if(subCatItemsMap.get(subCat) != null){ // add this item to map
                        subCatItemsMap.get(subCat).add(item);
                    }
                }
            }
        }       
    }
    
    public Category createGroupCategory(){
        groupCategory = new GroupCategory();
        this.isGroupCategory = true;
        return this;
    }
    
    public Category createModifierCategory(){
        modifierCategory = new ModifierCategory();
        this.isModifierCategory = true;
        return this;
    }
    
    public Category createSubCategory(){
        subCategories = new SubCategories();
        return this;
    }
    /*
    * Get the Items in the category
    * @return: The Item list of this category, empty list if no item
    */
    public ArrayList<Item> getItems(){
        return catItems.getItems();
    }   

    public void setSubCategories(SubCategories subCategories) {
        this.subCategories = subCategories;
    }
    
    /*
    * Get the Item of selection in the category, the content(s) in the selection is(are) treated as Item(s).
    * @return: The Item list of items of the selection in this category, empty list if no item or no selection.
    */
    public ArrayList<Item> getSelectionItems(){
        if(selection == null)
            return new ArrayList<>();
        return selection.getItems();
    }
    
    public Item getItem(String itemName){
        for(Item item:catItems.getItems()){
            if(item.getDisplayName().equals(itemName))
                return item;
        }
        return null;
    }
    
    /**
     * Get the SubCategorys of the Category. A Category with too many Items is not easy to look up for 
     * specific item in UI, if items are divides with several subCategory, then it's very easy to find in UI
     * @return: an ArrayList of SubCategory of the Category.
     */
    public ArrayList<SubCategory> getSubCategorys(){
        if(subCategories == null)
            return  new ArrayList<>();
        return subCategories.getSubCategorys();
    }    

    /**
     * One group category contains more than one category, used to group several categories into one
     * for easy display in one category button, such as lunch special contain different lunches style in fusion
     * restaurant. Group Category is just a list of Category Objects, each category contains list of items
     * @return an ArrayList of Category
     */
    public GroupCategory getGroupCategory() {
        return groupCategory;
    }
    
    public ModifierCategory getModifierCategory(){
        return modifierCategory;
    }
    
    /*
    * If the Category is a group category
    */
    public boolean isGroupCategory(){
        return isGroupCategory;
    }
    
    public boolean isModifierCategory(){
        return isModifierCategory;
    }
    
    public boolean hasSubCategory(){
        return subCategories != null;
    }
    
    /**
     * A Map to map list of items to SubCategory, for each SubCategory of the Category, there is an ArrayList 
     * of items of the Category, items of the Category are divided into groups called SubCategory.
     * @return HashMap, never return null but empty map if no subCategory
     */
    public HashMap<SubCategory, ArrayList<Item>> getSubCatItemsMap() {
        if(subCatItemsMap == null)
            return new HashMap<>();
        return subCatItemsMap;
    }
    
//    @Override
//    public boolean equals(Object obj){
//        if(obj instanceof Category){
//            Category cat = (Category)obj;
//            return this.getDisplayName().equals(cat.getDisplayName());
//        }
//        return false;
//    }    
    
    public JSONObject toJSONObject(){
        JSONObject object = super.toJSONObject();
        if(subCategories != null)
            object.put("sub_category", subCategories.toJSONArray());
        if(catItems != null && catItems.size() > 0) // some category may not contain any items
            object.put("item", catItems.toJSONArray());     
        if(groupCategory != null)
            object.put("group_category", groupCategory.toJSONArray());
        if(modifierCategory != null)
            object.put("modifier_category", modifierCategory.toJSONArray());
        
        return object;
    }
}
