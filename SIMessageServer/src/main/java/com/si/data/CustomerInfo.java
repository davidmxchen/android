/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

import org.json.JSONObject;
import com.si.transaction.api.CreditCardInfo;

/**
 *
 * @author Mingxing Chen
 */
public class CustomerInfo {
    private String name = "";
    private String cellPhone = "";
    private String phone = "";
    private String extension = "";
    // empty address
    private Address address = new Address();
    // empty card info
    private CreditCardInfo cardInfo = new CreditCardInfo();
    private String note = "";
    private String email = "";
    private String mediaAccount = "";

    public CustomerInfo() {
        this("", "", new Address());     
    }    
        
    public CustomerInfo(String name, String phone, Address address) {
        this.name = name;
        this.cellPhone = phone;
        this.phone = phone;
        this.address = address;
    }
        
    public CustomerInfo(JSONObject json) {
        if(json != null){
            this.name = json.optString("name");
            this.cellPhone = json.optString("cell_phone");
            this.phone = json.optString("phone");
            this.extension = json.optString("extension");
            this.address = new Address(json.optJSONObject("address"));
            this.cardInfo = new CreditCardInfo(json.optJSONObject("credit_card_info"));
            this.note = json.optString("note");
            this.email = json.optString("email");
            this.mediaAccount = json.optString("media_account");
        }
    }   
    
    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public CreditCardInfo getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(CreditCardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }   

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMediaAccount() {
        return mediaAccount;
    }

    public void setMediaAccount(String mediaAccount) {
        this.mediaAccount = mediaAccount;
    } 
    
    public JSONObject toJSONObject(){
        JSONObject object = new JSONObject();
        object.put("name", name);
        object.put("cell_phone", cellPhone);
        object.put("phone", phone);
        object.put("extension", extension);
        if(address != null)
            object.put("address", address.toJSONObject());
        if(cardInfo != null)
            object.put("credit_card_info", cardInfo.toJSONObject());
        object.put("note", note);
        object.put("email", email);
        object.put("media_account", mediaAccount);
            
        return object;
    }

    /**
     * A simple info for CustomerInfo to be displayed in TextArea in UI for delivery order
     * @return String for simple info
     */
    public String getSimpleInfo(){
    
        String text = phone.trim();
        if(!extension.isEmpty())
            text += " Ext:" + extension;
        if(address != null && !address.toString().isEmpty())
            text += "\n" + address.toString();
        if(!name.isEmpty())
            text += "\n" + name;
        if(!note.isEmpty())
            text += "\n" + note;
        
        return text;
    }
    
    /*
    public static class CreditCard {
        private String cardNumber;
        private String expDate;
        private String securityCode;
        private String cardName;
        private String cardType;

        public String getSecurityCode() {
            return securityCode;
        }

        public void setSecurityCode(String securityCode) {
            this.securityCode = securityCode;
        }

        public String getCardName() {
            return cardName;
        }

        public void setCardName(String cardName) {
            this.cardName = cardName;
        }
        
        public CreditCard(JSONObject json){
            if(json != null){
                cardNumber = json.optString("card_number");
                expDate = json.optString("expiration_date");
                securityCode = json.optString("security_code");
                cardName = json.optString("card_name");
                cardType = json.optString("card_type");
            }
            else
            {
                cardNumber = "";
                expDate = "";
                securityCode = "";
                cardName = "";
                cardType = "";
            }
        }
        
        public CreditCard(){
            this("", "");
        }
        
        public CreditCard(String number, String exp) {
            this(number, exp, "");
        }

        public CreditCard(String number, String exp, String name){
            this.cardNumber = number;
            this.expDate = exp;
            this.cardName = name;
            this.securityCode = "";
            this.cardType = "";
        }
        
        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getExpDate() {
            return expDate;
        }

        public void setExpDate(String expDate) {
            this.expDate = expDate;
        }
        
        public JSONObject toJSONObject(){
            JSONObject object = new JSONObject();
            object.put("card_number", cardNumber);
            object.put("expiration_date", expDate);
            object.put("card_name", cardName);
            object.put("security_code", securityCode);
            object.put("card_type", cardType);
            
            return object;
        }        
        
        public String toString(){
            if(cardNumber.trim().equals("") && expDate.trim().equals(""))
                return "";
            return cardNumber + "\n" + expDate;
        }
                
    }
    
    */
}
