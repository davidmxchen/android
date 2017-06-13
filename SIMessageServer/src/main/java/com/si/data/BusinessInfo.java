/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

import com.si.resource.BusinessKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
public class BusinessInfo {

    /**
     * Create BusinessInfo from JSONObject
     * @param json 
     */
    public BusinessInfo(JSONObject json){
        if(json != null){
            
            JSONArray arr = json.optJSONArray("name");
            if(arr != null){
                name = new String[arr.length()];
                for(int i=0; i<arr.length(); i++)
                    name[i] = arr.optString(i);
            }
            
            this.address = new Address(json.optJSONObject("address"));
            JSONArray array = json.optJSONArray("phone_number");            
            if(array != null){
                phoneNumbers = new String[array.length()];
                for(int i=0; i<array.length(); i++){
                    phoneNumbers[i] = array.optString(i);
                }
            }
        }
    }

    /**
    * BusinessInfo from resource 
    */
    public BusinessInfo() {
        try {
            this.businessResource = ResourceBundle.getBundle("com.si.resource.BusinessInfo");
            this.name = tokenizer(businessResource.getString(BusinessKey.name.toString()), ", ");
            this.address = new Address(businessResource.getString(BusinessKey.street.toString()), 
                    businessResource.getString(BusinessKey.city.toString()),
                    businessResource.getString(BusinessKey.state.toString()), 
                    businessResource.getString(BusinessKey.zip.toString()));
            this.phoneNumbers = tokenizer(businessResource.getString(BusinessKey.phone.toString()), ", ");
            
        } catch (MissingResourceException e) {
            System.err.println("fail to load resource: " + e);
        }
    }

    private String[] tokenizer(String text, String token){
        ArrayList<String> list = new ArrayList<>();
        int len = token.length();
        int index = text.indexOf(token);
        
        while (index != -1){
            String line = text.substring(0, index);
            list.add(line);
            text = text.substring(index + len);
            index = text.indexOf(token);            
        }
        list.add(text);
        String []str = new String[list.size()];
        index = 0;
        for(String st:list){
            str[index++] = st;
        }
        return str;
    }
    
    public String[] getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String[] phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
    
    public void setLogo(Image logo) {
        this.logo = logo;
    }

    public void setName(String name) {
        this.name = tokenizer(name, ", ");
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String[] getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public Image getLogo() {
        return logo;
    }

    public String[] getBusinessName() {
        return name;
    }


    
    public JSONObject toJSONObject(){
        JSONObject object = new JSONObject();
        JSONArray arr = new JSONArray();
        if(name != null){
           for(String n:name)
                arr.put(n);
            object.put("name", arr); 
        }
        
        object.put("address", address.toJSONObject());
        
        if(phoneNumbers != null){
            JSONArray array = new JSONArray();
            for(String phone:phoneNumbers)
                array.put(phone);
            object.put("phone_number", new JSONArray(phoneNumbers));
        }
        return object;
    }
    
    private Image logo;


    private String[] name;
    private Address address;
    private String[] phoneNumbers;
    private ResourceBundle businessResource;
    
    public static void main(String[] arg){
        BusinessInfo info = new BusinessInfo();
        System.out.println("Business is: " + info.toJSONObject().toString());
        System.out.println("testing time: " + new Date(System.currentTimeMillis()));
    }
}
