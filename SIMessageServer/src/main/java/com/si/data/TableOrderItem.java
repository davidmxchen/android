/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

/**
 *
 * @author Mingxing Chen
 */


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TableOrderItem {
    private IntegerProperty number;
    private StringProperty description;
    private StringProperty price;
    private ObjectProperty<ItemStatus> status;
    private StringProperty orderTime;
    private StringProperty sentTime;
    private StringProperty sender;
    
    // read only original OrderItem reference
    private OrderItem orderItemRef;
    private final ObservableList<SubItem> subItemList = FXCollections.observableArrayList();
    public static final ObjectProperty<Language> languageProperty = new SimpleObjectProperty<Language>();;   
    
    
    private TableOrderItem(){        
        this(new OrderItem(new Item("name", "price", "description")));
    }
    
    /**
     * Create new TableOrderItem from existing TableOrderItem, clone a TableOrderItem 
     * @param TableOrderItem 
     */
    public TableOrderItem(TableOrderItem item){
        // safely duplicate a TableOrderItem
        this(item.toJSONObject());
//        assert item != null;
//        numberProperty().set(item.getNumber());
//        descriptionProperty().set(item.getDescription());
//        priceProperty().set(item.getPrice().toPlainString());
//        statusProperty().set(item.getStatus());
//        orderTimeProperty().set(item.getOrderTime());
//        sentTimeProperty().set(item.getSentTime());
//        senderProperty().set(item.getSender());  
//        
//        orderItemRef = item.getOrderItem();
//        subItemList.addAll(item.getSubItemList());       
//        
//        subItemList.addListener(new ListChangeListener<SubItem>() {
//
//            @Override
//            public void onChanged(ListChangeListener.Change<? extends SubItem> change) {
//                String newName = orderItemRef.getDisplayName();
//                BigDecimal newPrice = orderItemRef.getPrice();
//                for(SubItem i:subItemList){
//                    newName += "\n  " + i.getDisplayName();
//                    if(i.getPrice() != null && !i.getPrice().isEmpty() && new BigDecimal(i.getPrice()).compareTo(new BigDecimal(0.0)) > 0){
//                        newName += " -- $" + i.getPrice();
//                        newPrice = newPrice.add(new BigDecimal(i.getPrice())).setScale(2, RoundingMode.HALF_UP);
//                    }
//                }    
//                int num = numberProperty().get();
//                descriptionProperty().set(newName);
//                priceProperty().set(newPrice.multiply(new BigDecimal(num)).setScale(2, RoundingMode.HALF_UP).toPlainString());
//
//            }
//        });
//        
//        languageProperty.addListener(new ChangeListener<Language>() {
//            @Override
//            public void changed(ObservableValue<? extends Language> ov, Language oldLanguage, Language newLanguage) {
//                OrderItem.language = newLanguage;
//                Item.language = newLanguage;
//                
//                String desStr = orderItemRef.getDisplayName();
//                
//                for(SubItem i:subItemList){                   
//                    desStr += "\n  " + i.getDisplayName();
//                    if(!i.getPrice().equals("") && new BigDecimal(i.getPrice()).compareTo(new BigDecimal(0.0)) > 0){
//                        desStr += " -- $" + i.getPrice();   
//                    }
//                }
//                descriptionProperty().set(desStr);               
//            }
//        });
    }
    
    /**
     * TableOrderItem created from orderItem, the orderItem represent a specific single order item from Menu
     * @param OrderItem 
     */
    public TableOrderItem(OrderItem item){
        orderItemRef = item;
        numberProperty().set(1);
        descriptionProperty().set(item.getDisplayName());
        priceProperty().set(item.getPrice().toPlainString());
        statusProperty().set(ItemStatus.NEW);
        // order time is added at the time it is added to table
        Time time = new Time(System.currentTimeMillis());
        orderTimeProperty().set(time.getTime());        
        sentTimeProperty().set("");
        senderProperty().set("");
        
        // OrderItem don't contain subItem. TableOrderItem does
        subItemList.addListener(new ListChangeListener<SubItem> () {

            @Override
            public void onChanged(ListChangeListener.Change<? extends SubItem> change) {
                // reset description string on every change
                String desStr = orderItemRef.getDisplayName();
                
                BigDecimal priceBasic = new BigDecimal(0.00);
                
                for(SubItem i:subItemList){                   
                    desStr += "\n  " + i.getDisplayName();
                    if(i.getPrice() != null && !i.getPrice().isEmpty() && new BigDecimal(i.getPrice()).compareTo(new BigDecimal(0.0)) > 0){
                        desStr += " -- $" + i.getPrice();                       
                        priceBasic  = priceBasic.add(new BigDecimal(i.getPrice())).setScale(2, RoundingMode.HALF_UP);
                    }
                }
                int num = numberProperty().get();
                descriptionProperty().set(desStr);
                priceProperty().set(priceBasic.add(orderItemRef.getPrice()).multiply(new BigDecimal(num)).setScale(2, RoundingMode.HALF_UP).toPlainString());
                
            }
        });  
        
        languageProperty.addListener(new ChangeListener<Language>() {
            @Override
            public void changed(ObservableValue<? extends Language> ov, Language oldLanguage, Language newLanguage) {
                OrderItem.language = newLanguage;
                Item.language = newLanguage;
                
                String desStr = orderItemRef.getDisplayName();
                
                for(SubItem i:subItemList){                   
                    desStr += "\n  " + i.getDisplayName();
                    if(i.getPrice() != null && !i.getPrice().isEmpty() && new BigDecimal(i.getPrice()).compareTo(new BigDecimal(0.0)) > 0){
                        desStr += " -- $" + i.getPrice();   
                    }
                }
                descriptionProperty().set(desStr);               
            }
        });
    }  
    
    /**
     * Immutable TableOrder when created in TableView, constructed with data
     * @param tableOrderJson 
     */
    public TableOrderItem(JSONObject tableOrderJson){ 
        numberProperty().set(tableOrderJson.getInt("number"));
        priceProperty().set(tableOrderJson.optString("price"));
        statusProperty().set(ItemStatus.valueOf(tableOrderJson.optString("item_status")));
        orderTimeProperty().set(tableOrderJson.optString("order_time"));
        sentTimeProperty().set(tableOrderJson.optString("sent_time"));
        senderProperty().set(tableOrderJson.optString("sender"));
        
        // create OrderItem with JSON
        orderItemRef = new OrderItem(tableOrderJson.optJSONObject("order_item"));  
        // create sideitem -- a list of Item for subItemList
        JSONArray array = tableOrderJson.optJSONArray("sub_items");
        if(array != null){
            for(int index=0; index<array.length(); index++)
                subItemList.add(new Item(array.optJSONObject(index)));
        }
        
        String name = orderItemRef.getDisplayName();
        for(SubItem item:subItemList){
            name += "\n  " + item.getDisplayName();
            if(item.getPrice() != null && !item.getPrice().isEmpty() && new BigDecimal(item.getPrice()).compareTo(new BigDecimal(0.0)) > 0)
                name += " -- $" + item.getPrice();
        }
        descriptionProperty().set(name);
        
        subItemList.addListener(new ListChangeListener<SubItem>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends SubItem> change) {
                String newName = orderItemRef.getDisplayName();
                BigDecimal newPrice = orderItemRef.getPrice();
                for(SubItem i:subItemList){
                    newName += "\n  " + i.getDisplayName();
                    if(i.getPrice() != null && !i.getPrice().isEmpty() && new BigDecimal(i.getPrice()).compareTo(new BigDecimal(0.0)) > 0){
                        newName += " -- $" + i.getPrice();
                        newPrice = newPrice.add(new BigDecimal(i.getPrice())).setScale(2, RoundingMode.HALF_UP);
                    }
                }    
                int num = numberProperty().get();
                descriptionProperty().set(newName);
                priceProperty().set(newPrice.multiply(new BigDecimal(num)).setScale(2, RoundingMode.HALF_UP).toPlainString());
            }
        });
        
        languageProperty.addListener(new ChangeListener<Language>() {
            @Override
            public void changed(ObservableValue<? extends Language> ov, Language oldLanguage, Language newLanguage) {
                OrderItem.language = newLanguage;
                Item.language = newLanguage;
                
                String desStr = orderItemRef.getDisplayName();
                
                for(SubItem i:subItemList){                   
                    desStr += "\n  " + i.getDisplayName();
                    if(i.getPrice() != null && !i.getPrice().isEmpty() && new BigDecimal(i.getPrice()).compareTo(new BigDecimal(0.0)) > 0){
                        desStr += " -- $" + i.getPrice();   
                    }
                }
                descriptionProperty().set(desStr);               
            }
        });
    }

    public OrderItem getOrderItem() {
        return orderItemRef;
    }
    
    public void setOrderItem(OrderItem item){
        orderItemRef = item;
    }    
    
    public final IntegerProperty numberProperty(){
        if(number == null) number = new SimpleIntegerProperty(this, "number", 1);
        return number;
    }
    
    public final StringProperty descriptionProperty() { 
         if (description == null) description = new SimpleStringProperty(this, "description");
         return description; 
     }
    
    public final StringProperty priceProperty(){
        if(price == null) price = new SimpleStringProperty(this, "price");
        return price;
    }
        
    public final ObjectProperty<ItemStatus> statusProperty(){
        if(status == null) status = new SimpleObjectProperty<>(ItemStatus.NEW);// Property(this, "status");
        return status;
    }

//    public TableOrderItem(IntegerProperty number, StringProperty description, StringProperty price, 
//            ItemStatus status, StringProperty orderTime, StringProperty sentTime, 
//            StringProperty sender, OrderItem orderItemRef, ObservableList<SubItem> subItemList) {
//        this.number = number;
//        this.description = description;
//        this.price = price;
//        this.statusProperty().set(status);
//        this.orderTime = orderTime;
//        this.sentTime = sentTime;
//        this.sender = sender;
//        this.orderItemRef = orderItemRef;
//        this.subItemList.addAll(subItemList);
//    }
    
    public final StringProperty orderTimeProperty(){
        if(orderTime == null) orderTime = new SimpleStringProperty(this, "orderTime");
        return orderTime;
    }
    
    public final StringProperty sentTimeProperty(){
        if(sentTime == null) sentTime = new SimpleStringProperty(this, "sentTime");
        return sentTime;
    }
    
    public final StringProperty senderProperty(){
        if(sender == null) sender = new SimpleStringProperty(this, "sender");
        return sender;
    }

        
    public void setNumber(int item){
        numberProperty().set(item);
    }
    
    public void setNumber(Integer item){
        numberProperty().set(item.intValue());
    }
    
    public void setDescription(String desc){
        descriptionProperty().set(desc);
    }    
  
    public void setPrice(BigDecimal price){
        priceProperty().set(price.toString());
    }
    
    public void setPrice(Double price){
        BigDecimal number = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
        priceProperty().set(number.toPlainString());
    }
    
    public void setPrice(String price){
        if(price.isEmpty())
            priceProperty().set(price);
        else{
            BigDecimal number = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
            priceProperty().set(number.toPlainString());
        }
    }
    
    public void setStatus(ItemStatus status1){
        statusProperty().set(status1);
    } 
    
    public void setOrderTime(String orderTime){
        orderTimeProperty().set(orderTime);
    }
    
    public void setSentTime(String sentTime) {
        sentTimeProperty().set(sentTime);
    }
    
    public void setSender(String sender) {
        senderProperty().set(sender);
    }    

    public int getNumber(){
        return numberProperty().get();
    }
    
    public String getDescription(){
        return descriptionProperty().get();
    }
    
    public BigDecimal getPrice(){
        if(priceProperty().get().isEmpty())
            return new BigDecimal("0.00");
        else
            return new BigDecimal(priceProperty().get());
    }
    
    public ItemStatus getStatus(){
        return statusProperty().get();
    }
    
    public String getOrderTime(){
        return orderTimeProperty().get();
    }
    
    public String getSentTime() {
        return sentTimeProperty().get();        
    }
    
    public String getSender() {
        return senderProperty().get();
    }
    
    public ItemStatus getItemStatus() {
        return statusProperty().get();
    }

    public void setItemStatus(ItemStatus itemStatus) {
        statusProperty().set(itemStatus);
    }
    
    public ObservableList<SubItem> getSubItemList(){
        return this.subItemList;
    } 
    
//    public StringProperty localeProperty() { 
//         if (locale == null) locale = new SimpleStringProperty(this, "locale");
//         return locale; 
//     }
//    
//    public void setLocale(String locale){
//        localeProperty().set(locale);
//    }
    
    public void addItem(Item item){
        subItemList.add(item);
    }
    
    public SubItem getItem(int index){
        return subItemList.get(index);
    }
    
    public int getItemIndex(Item item){
        return subItemList.indexOf(item); 
    }
    
    public void removeLastItem(){
        if(subItemList.size() > 0)
            subItemList.remove(subItemList.size()-1);
    }
    
    /**
     * Get the last item of the SubItemList
     * @return last item, null if the list is empty
     */
    public SubItem getLastItem(){
        int size = subItemList.size();
        if(size == 0)
            return null;
        else
            return subItemList.get(size-1);
    }
    
    public void addSubItem(Item subItem){
        subItemList.add(subItem);
    }
    
    /**
     * Remove the last item of the subItemList except the list is empty
     */
    public void removeLastSubItem(){
        if(subItemList.size() > 0)
            subItemList.remove(subItemList.size()-1);
    }
    
    public JSONObject toJSONObject(){
        JSONObject tableOrder = new JSONObject();
        tableOrder.put("number", numberProperty().get());
        tableOrder.put("price", priceProperty().get());
        tableOrder.put("order_item", orderItemRef.toJSONObject());
        tableOrder.put("item_status", statusProperty().get().toString());
        tableOrder.put("order_time", orderTimeProperty().get());
        tableOrder.put("sent_time", sentTimeProperty().get());
        tableOrder.put("sender", senderProperty().get());
        
        JSONArray array = new JSONArray();
        for(SubItem item:subItemList){
            array.put(item.toJSONObject());
        }
        if(array.length() > 0)
            tableOrder.put("sub_items", array);
//  System.out.println("order: " + tableOrder.toString());      
        return tableOrder;
    } 
 
//    public String toString(){
//        return toJSONObject().toString();
//    }    
    
    // loosely implemented, change later
    public boolean equalsTo(TableOrderItem another){
        if(this == another)
            return true;
        System.out.println("OrderIte equal==>" + this.getOrderItem().equals(another.getOrderItem()));
        System.out.println("price equal==>" + this.getPrice().toString().equals(another.getPrice().toString()));
        System.out.println("description equal==>" + this.getDescription().equals(another.getDescription()));
        System.out.println("order time equal==>" + this.getOrderTime().equals(another.getOrderTime()));
        if( this.getNumber() == another.getNumber() 
                && this.getPrice().toString().equals(another.getPrice().toString()) 
                && this.getDescription().equals(another.getDescription()) 
                && this.getSender().equals(another.getSender())
                //&& this.getOrderItem().equals(another.getOrderItem())
                && this.getOrderTime().equals(another.getOrderTime()) ) 

            return true;
        else
            return false;
    }
}