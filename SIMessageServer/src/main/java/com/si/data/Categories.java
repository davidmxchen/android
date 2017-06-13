/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.si.resource.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONTokener;

/**
 *
 * @author Mingxing Chen
 */
public class Categories implements MenuData{
    private static final Logger LOG = Logger.getLogger(Categories.class.getName());
    private final ArrayList<Category> catList;
    private Item emptyItem;
    private BusinessInfo businessInfo;
    private Time lastUpdateTime;
    
    public static Language catLanguage = new Language();
    public static String CATEGORY_JSON_NAME = "MenuList.json";

    
    public Categories(JSONObject json){
        catList = new ArrayList<>();
        JSONArray array = json.optJSONArray("category");
        if(array != null)
            for(int i=0; i<array.length(); i++)
                catList.add(new Category(array.optJSONObject(i)));
        
        if(json.has("empty_item"))
            emptyItem = new Item(json.optJSONObject("empty_item"));
        else
            emptyItem = null;
        
        if(catLanguage == null)
            catLanguage = new Language();
        
        if(json.has("business_info"))
            businessInfo = new BusinessInfo(json.optJSONObject("business_info"));
        if(json.has("last_update_time"))
            lastUpdateTime = new Time(json.getLong("last_update_time"));
        else
            lastUpdateTime = new Time(0l);
    }

    public Time getLastUpdateTime() {
        return lastUpdateTime;
    }

    public Item getEmptyItem() {
        return emptyItem;
    }

    public boolean hasEmptyItem() {
        return emptyItem != null;
    }

    public void setLastUpdateTime(Time lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setLanguage(Language locale1){
        Categories.catLanguage = locale1;
    }
    
    @Override
    public String[] getCategoryNames() {
        ArrayList<String> list = getCategoryNamesList();
        String[] arr = new String[list.size()];
        int i = 0;
        for(String str:list){
            arr[i++] = str;
        }
        return arr;
    }

    @Override
    public ArrayList<String> getCategoryNamesList() {
        ArrayList<String> names = new ArrayList<>();
        for(Category cat:catList){
            names.add(cat.getDisplayName());
        }
        return names;
    }
  
    @Override
    public Category getCategory(String catName) {
        for(Category cat:catList){
            if(cat.getDisplayName().equals(catName))
                return cat;
        }
        return null;
    }  

    public ArrayList<Category> getCategorys(){
        return this.catList;
    }
  
    public static Categories createCategories(){
       Categories cats = null;
        try{
            JSONObject rootObj = new JSONObject(new JSONTokener( Resource.class.getResourceAsStream(CATEGORY_JSON_NAME)));
            JSONObject root = rootObj.getJSONObject("root");
            cats = new Categories(root);
        }catch(JSONException e){
            System.out.println("can not read file" + e.getMessage());
        }
        return cats;
    }

    public BusinessInfo getBusinessInfo() {
        return businessInfo;
    }

    public void setBusinessInfo(BusinessInfo businessInfo) {
        this.businessInfo = businessInfo;
    }
    
    public JSONObject toJSONObject(){
        JSONObject object = new JSONObject();
        if(businessInfo != null)
            object.put("business_info", businessInfo.toJSONObject());
        if(emptyItem != null)
            object.put("empty_item", emptyItem.toJSONObject());
        // prepare cats JSONArray, don't use "new JSONArray(catList);"
        JSONArray array = new JSONArray();
        for(Category cat:catList)
            array.put(cat.toJSONObject());
        object.put("category", array);
        object.put("last_update_time", lastUpdateTime.getTimeInMilis());
        
        return object; //new JSONObject().put("root", object);
    }

    public static void main(String[] args){
        Categories cats = Categories.createCategories();
       // System.out.println("business info is:");
        //System.out.println(cats.getBusinessInfo().toJSONObject());
        int index=0;
        for(Category cat:cats.getCategorys()){
            System.out.println("category at " + index + " is==>" + cat.toJSONObject().toString(1));
            index++;
        }
        try{
        // recreate with json object
        Categories cats1 = new Categories(cats.toJSONObject());
        Categories cats2 = new Categories(cats1.toJSONObject());
        Categories cats3 = new Categories(new JSONObject(cats2.toJSONObject().toString()));
        System.out.println("=========================================");
        System.out.println("new json of the cats is:");
        int i = 0;
        for(Category cat:cats2.getCategorys()){
            System.out.println(cat.toJSONObject());
        }
        if(cats1.toJSONObject().toString().equals(cats2.toJSONObject().toString()))
            System.out.println("holly! long strings are equals");
        else
            System.out.println("long string may fail to compare");
        
        System.out.println("print json cats1.toJSONObject==> " + cats1.toJSONObject()); // this produce all string in one line
        System.out.println("print cats2.toJSONObject().toString()==> " + cats2.toJSONObject().toString()); // same as above
        //System.out.println("print json.toString==> " + cats.toJSONObject().toString(2)); //  good indentation
        System.out.println("cats 3====>\n" + cats3.toJSONObject().toString());
        
        }catch(JSONException ex){
            System.out.println("error msg:" + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
