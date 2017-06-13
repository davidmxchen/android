/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.si.data;

import com.si.transaction.api.CreditCardPmtInfo;
import com.si.resource.OrderStatus;
import com.si.resource.OrderType;
import com.si.resource.PaymentType;
import java.text.NumberFormat;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import net.authorize.TransactionType;

/**
 * @author Mingxing Chen
 */

public class Order {
    
    private OrderType orderType; 
    private CustomerInfo customerInfo;
    private ObservableList<TableOrderItem> orderItems;
    private final ObservableList<CreditCardPmtInfo> cardPmtList;    
    private ObjectProperty<OrderStatus> orderStatus;
    private BigDecimal taxAmount;
    private BigDecimal discount;
    private BigDecimal otherChargeAmount;
            
    private BigDecimal subTotal;
    private BigDecimal total;
    private BigDecimal taxRate;
    private BigDecimal paidAmount;
    private BigDecimal tipAmount;
    private int totalItem;
    private OtherCharge otherCharge;
    private DiscountInfo discountInfo;
    private Time orderTime;
    private Time updateTime;
    private String uniqueId;
    
    // observable property to track the status of the order
    private OrderID orderId;
    private String serverName;  
    private PaymentType paymentType;
    private String tableName;
    
    private boolean LOCKED = false;
    
    private NumberFormat currencyFormat;
    { 
        currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setMinimumFractionDigits(2);
    }

    public Order() {
        uniqueId = System.currentTimeMillis()+"";
        orderType = OrderType.DINE_IN; // by default
        customerInfo = new CustomerInfo();
        taxAmount = new BigDecimal(0.00);
        discount = new BigDecimal(0.00);
        otherChargeAmount = new BigDecimal(0.00);
        subTotal = new BigDecimal(0.00);
        total = new BigDecimal(0.00);
        taxRate = new BigDecimal(0.00);
        totalItem = 0;
        paidAmount = new BigDecimal(0.00);
        tipAmount = new BigDecimal(0.00);
        otherCharge = new OtherCharge();
        discountInfo = new DiscountInfo();
        orderTime = new Time(System.currentTimeMillis());
        // update time not set
        updateTime = new Time(0);
        orderItems = FXCollections.observableArrayList();
        cardPmtList = FXCollections.observableArrayList();
    //    orderId = MainController.getNewOrderID();
        serverName = "";
        paymentType = PaymentType.DUE;
        tableName = "";
       // orderStatus = new SimpleObjectProperty<>(OrderStatus.NEW);
        orderStatusProperty().set(OrderStatus.NEW);
        cardPmtList.addListener(new ListChangeListener<CreditCardPmtInfo>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends CreditCardPmtInfo> change) {
                System.out.println("card payment list changed");
            }
        });
    } 
    
    public Order(JSONObject object){
        uniqueId = object.optString("unique_id");
        if(object.optJSONObject("order_id") != null)
            orderId = new OrderID(object.optJSONObject("order_id"));
        serverName = object.optString("server_name");
        orderTime = new Time(object.optLong("order_time"));  
        updateTime = new Time(object.optLong("update_time"));
        customerInfo = new CustomerInfo(object.optJSONObject("customer_info"));
        tableName = object.optString("table_name");
        orderType = OrderType.getValue(object.optString("order_type")); 
        
        taxAmount = new BigDecimal(object.optDouble("tax_amount", 0.00d)).setScale(2, RoundingMode.HALF_UP);
        taxRate = new BigDecimal(object.optDouble("tax_rate", 0.00d)).setScale(2, RoundingMode.HALF_UP);
        discount = new BigDecimal(object.optDouble("discount", 0.00d)).setScale(2, RoundingMode.HALF_UP);
        otherChargeAmount = new BigDecimal(object.optDouble("other_charge_amount", 0.00d)).setScale(2, RoundingMode.HALF_UP);
        totalItem = object.optInt("total_item");
        subTotal = new BigDecimal(object.optDouble("sub_total", 0.00d)).setScale(2, RoundingMode.HALF_UP);
        total = new BigDecimal(object.optDouble("total", 0.00d)).setScale(2, RoundingMode.HALF_UP);
        paidAmount = new BigDecimal(object.optDouble("paid_amount", 0.00d)).setScale(2, RoundingMode.HALF_UP);
        tipAmount = new BigDecimal(object.optDouble("tip_amount", 0.00d)).setScale(2, RoundingMode.HALF_UP);
        
        otherCharge = new OtherCharge(object.optJSONObject("other_charge"));
        discountInfo = new DiscountInfo(object.optJSONObject("discount_info"));
        if(object.has("payment_type"))
            paymentType = PaymentType.valueOf(object.optString("payment_type").toUpperCase());
        else
            paymentType = PaymentType.DUE;
        
        orderItems = FXCollections.observableArrayList();
        
        JSONArray array = object.optJSONArray("order_items");
        if(array != null){
            for(int i=0; i<array.length(); i++){
                orderItems.add(new TableOrderItem(array.optJSONObject(i)));
            }
        }  
        OrderStatus status = OrderStatus.NEW;
        if(object.has("order_status"))
            status = OrderStatus.valueOf(object.optString("order_status").toUpperCase());  
        //orderStatus = new SimpleObjectProperty<>(status);
        orderStatusProperty().set(status);
        
        cardPmtList = FXCollections.observableArrayList();
        // more json data for this list to load
        array = object.optJSONArray("card_pmt_list");
        if(array != null){
            for(int i=0; i<array.length(); i++){
                cardPmtList.add(new CreditCardPmtInfo(array.optJSONObject(i)));
            }
        }
        cardPmtList.addListener(new ListChangeListener<CreditCardPmtInfo>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends CreditCardPmtInfo> change) {
                System.out.println("card payment list changed");
            }
        });
        resetTotal();
    }

    public ObjectProperty<OrderStatus> orderStatusProperty(){
        if(orderStatus == null)
            orderStatus = new SimpleObjectProperty<OrderStatus>(this, "order_status");
        return orderStatus;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    
    public final OtherCharge getOtherCharge() {
        return otherCharge;
    }

    public final void setOtherCharge(OtherCharge otherCharge) {
        this.otherCharge = otherCharge;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
    
    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public ObservableList<TableOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ObservableList<TableOrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public Time getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Time updateTime) {
        this.updateTime = updateTime;
    }    
    
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(BigDecimal tipAmount) {
        this.tipAmount = tipAmount;
    }

    public Time getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Time orderTime) {
        this.orderTime = orderTime;
    }
    
    public OrderID getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderID orderId) {
        this.orderId = orderId;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }
    
    public OrderStatus getOrderStatus() {
        return orderStatusProperty().get();
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        orderStatusProperty().set(orderStatus);
    }
    
    public String getServer() {
        return serverName;
    }

    public void setServer(String serverName) {
        this.serverName = serverName;
    }    
    
    public JSONObject toJSONObject(){
        JSONObject object = new JSONObject();
        object.put("unique_id", uniqueId);
        object.put("order_type", OrderType.getName(orderType)); 
        object.put("order_status", orderStatus.get());
        object.put("customer_info", customerInfo.toJSONObject());
        object.put("payment_type", paymentType.toString().toUpperCase());
        object.put("tax_amount", taxAmount.toString());
        object.put("tax_rate", taxRate.toString());
        object.put("discount", discount.toString());
        object.put("other_charge_amount", otherChargeAmount.toString());
        object.put("sub_total", subTotal.toString());
        object.put("total_item", totalItem);
        object.put("total", total.toString());
        object.put("paid_amount", paidAmount.toString());
        object.put("tip_amount", tipAmount.toString());
        object.put("other_charge", otherCharge.toJSONObject());
        object.put("discount_info", discountInfo.toJSONObject());
        // the long number represent time in seconds
        object.put("order_time", orderTime.getDate().getTime()); // the same as getTimeInMilis()
        object.put("update_time", updateTime.getTimeInMilis());
        object.put("order_id", orderId.toJSONObject());
        object.put("server_name", serverName); 
        object.put("table_name", tableName);
        
        // to JSONArray
        JSONArray array = new JSONArray();
        for(TableOrderItem item:orderItems){
            array.put(item.toJSONObject());
        }
        object.put("order_items", array);
        
        // process card pmt list
        array = new JSONArray();
        for(CreditCardPmtInfo info: cardPmtList)
            array.put(info.toJSONObject());
        object.put("card_pmt_list", array);
        
        return object;
    }

    public DiscountInfo getDiscountInfo() {
        return discountInfo;
    }

    public void setDiscountInfo(DiscountInfo discountInfo) {
        this.discountInfo = discountInfo;
    }
        
    public void resetTotal(){
        subTotal = new BigDecimal(0.00);
        total = new BigDecimal(0.00);
        taxAmount = new BigDecimal(0.00);
        totalItem = 0;
        for(TableOrderItem item:orderItems){
            totalItem += item.getNumber();
            subTotal = subTotal.add(item.getPrice()).setScale(2, RoundingMode.HALF_UP);          
        } 
        //subTotal = subTotal.setScale(2, RoundingMode.HALF_UP);
        
        if(discountInfo.getChargeType().equals(ChargeType.PERCENT)){
            discount = discountInfo.getPercent().multiply(subTotal).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            // update discountInfo if different
            discountInfo.setAmount(discount);
        }else
            discount = discountInfo.getAmount();        
         
        if(otherCharge.getChargeType().equals(ChargeType.PERCENT)){
            otherChargeAmount = otherCharge.getPercent().multiply(subTotal).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            // update otherCharge
            otherCharge.setAmount(otherChargeAmount);
        }else
            otherChargeAmount = otherCharge.getAmount();
        
        // tax should not be applied to otherCharge
        taxAmount = subTotal.subtract(discount).multiply(taxRate).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        total = subTotal.subtract(discount).add(taxAmount).add(otherChargeAmount).setScale(2, RoundingMode.HALF_UP);
    }

        /**
        String subTotalStr = currencyFormat.format(subTotal);
        String otherchargeStr = currencyFormat.format(othercharge);
        String taxStr = currencyFormat.format(taxAmount);
        String totalStr = currencyFormat.format(total);
        String discountStr = currencyFormat.format(discount);
        
        try{
            subTotal = currencyFormat.parse(subTotalStr).BigDecimalValue();
            othercharge = currencyFormat.parse(otherchargeStr).BigDecimalValue();
            taxAmount = currencyFormat.parse(taxStr).BigDecimalValue();
            total = currencyFormat.parse(totalStr).BigDecimalValue();
            discount = currencyFormat.parse(discountStr).BigDecimalValue();
        } catch (ParseException e){
            System.out.println("parse error: ");  
            e.printStackTrace();
        }
        * **/
    

    public BigDecimal getOtherChargeAmount() {
        return otherChargeAmount;
    }

//    public void setOtherChargeAmount(BigDecimal otherChargeAmt) {
//        // Special implementation for service charge, new charge must be less than original,
//        // apply to credit card payment only        
//        if(orderStatus.equals(OrderStatus.CARD_FINALIZED) && (this.otherChargeAmount.compareTo(otherChargeAmt) > 0)){
//            // reset the total amount, call to reset new tip amount
//            this.otherChargeAmount = otherChargeAmt;
//            resetTotal();
//            updateCreditCardPmtInfo();
//        }else
//            this.otherChargeAmount = otherChargeAmt;
//    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }  

    public ObservableList<CreditCardPmtInfo> getCardPmtList() {
        return cardPmtList;
    }

    public void addCreditCardPmtInfo(CreditCardPmtInfo info){
        cardPmtList.add(info);
    }
    
    
    
    /**
     * When credit card transaction type changed, usually full amount captured in finalize transaction, update tip
     * amount the customer has signed. If transaction is VOID, remove payment transaction from the order
     */
    public void updateCreditCardPmtInfo(){
        BigDecimal totalChargedAmt = new BigDecimal(0.00);
        ArrayList<CreditCardPmtInfo> voidList = new ArrayList<CreditCardPmtInfo>();
        ArrayList<CreditCardPmtInfo> capturedList = new ArrayList<CreditCardPmtInfo>();
        ArrayList<CreditCardPmtInfo> uncapturedList = new ArrayList<CreditCardPmtInfo>();
        for(CreditCardPmtInfo pmtInfo:cardPmtList){
            if(pmtInfo.getTransactionType().equals(TransactionType.AUTH_CAPTURE) || 
                    pmtInfo.getTransactionType().equals(TransactionType.PRIOR_AUTH_CAPTURE)){
                capturedList.add(pmtInfo);
                
                totalChargedAmt = totalChargedAmt.add(pmtInfo.getTransactionAmount());                
            }else if(pmtInfo.getTransactionType().equals(TransactionType.AUTH_ONLY)){
                uncapturedList.add(pmtInfo);
            }else if(pmtInfo.getTransactionType().equals(TransactionType.VOID))
                voidList.add(pmtInfo);
        }
        // all card payments captured
        if(!capturedList.isEmpty()){
            cardPmtList.removeAll(uncapturedList); 
            setOrderStatus(OrderStatus.CARD_FINALIZED);
            
            if(totalChargedAmt.compareTo(total) > 0)
                tipAmount = totalChargedAmt.subtract(total).setScale(2, RoundingMode.HALF_UP);
            else
                tipAmount = BigDecimal.ZERO;
        }
        
        if(!voidList.isEmpty()){ // remove all other transaction except for the void
            cardPmtList.removeAll(uncapturedList);
            cardPmtList.removeAll(capturedList);
            // reset order status
            tipAmount = BigDecimal.ZERO;
            setOrderStatus(OrderStatus.CHECK_UNPAID);
            paymentType = PaymentType.DUE;
        } 
    } 
    
    public String toString(){
        return toJSONObject().toString();
    }
    
    /**
     * Check if the order is locked in edit to avoid concurrency
     * @return boolean
     */
    public boolean isLocked(){
        return LOCKED;
    }
    
    /**
     * Lock this order for edit exclusively, if the order is already locked by other, false is returned
     * if the order is not locked, then lock this order, true is returned
     * @return boolean
     */
    public boolean lockOrder(){
        if(LOCKED)
            return false;
        LOCKED = true;
        return true;
    }
    
    /**
     * Unlock the order
     */
    public void UnlockOrder(){
        LOCKED = false;
    }
}
