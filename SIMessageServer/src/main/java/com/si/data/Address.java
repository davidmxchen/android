/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
public class Address {
    private String street;
    private String city;
    private String state;
    private String zip;
    
    public Address() {
        this("", "", "", "");
    }   
    
    public Address(JSONObject addressObject){   
        this();
        if(addressObject != null){
            this.street = addressObject.optString("street");
            this.city = addressObject.optString("city");
            this.state = addressObject.optString("state");
            this.zip = addressObject.optString("zip");
        }
    }
    
    public Address(String street, String city, String state, String zip) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }    
    
    public JSONObject toJSONObject(){
        JSONObject object = new JSONObject();
        object.put("street", street);
        object.put("city", city);
        object.put("state", state);
        object.put("zip", zip);
        return object;
    }
    
    public String toString(){
        String text = "";
        if(!street.equals(""))
            text += street;
        if(!city.equals(""))
            text += "\n" + city;
        if(!state.equals(""))
            text += ", " + state;
        if(!zip.equals(""))
            text += " " + zip;
        return text;
    }
    
    public static void main(String []args){
        ObjectProperty<Address> obejectProperty = new SimpleObjectProperty<>();
        Address address = new Address();
        obejectProperty.set(address);
        obejectProperty.addListener(new ChangeListener<Address>() {

            @Override
            public void changed(ObservableValue<? extends Address> ov, Address t, Address t1) {
                System.out.println("object address changed new: " +  t1.getCity());
                System.out.println("Old address: " + t.getCity());
            }
        });
        obejectProperty.addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable o) {
                System.out.println("invalidated");
            }
        });
        Address address1 = obejectProperty.get();
        address1.setCity("NYC");
        obejectProperty.set(address1);
        
        Address address2 = new Address();
        
        obejectProperty.set(address2);
        
    }
}
