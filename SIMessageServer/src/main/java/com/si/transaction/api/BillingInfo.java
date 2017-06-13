/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.transaction.api;

import com.si.data.CustomerInfo;
import com.si.data.Order;
import java.math.BigDecimal;
import net.authorize.TransactionType;

/**
 *
 * @author Mingxing Chen
 */
/**
 * BillingInfo represent actual bill posted into customer account for customer's food order charge
 * customer credit card info can be swiped or enter through UI, card info entered through UI is treated
 * as CreditCard Not Present for card authentication.
 * The billAmount of billingInfo is the same amount as customer order totalAmount, the customer's totalAmount
 * may include tipAmount(Service Charge), but the tipAmount of BillingInfo is the tipAmount appear on customer's
 * credit signing slip.
 * @author Mingxing Chen
 */
public class BillingInfo {
    private CardTrackData cardData;
    private BigDecimal billAmount;
    private BigDecimal tipAmount;
    private BigDecimal totalAmount;
    private Order customerOrder;
    private CustomerInfo customerInfo;
    private CreditCardInfo cardInfo;
    private CreditCardPmtInfo paymentInfo;
    
    // reference to transaction info
    // refId is usually order id, Up to 20 characters, use "#orderId" as refId
    private String refId;
    // Numeric, 4 digits.
    private String employeeId;
    
    // reference for card TransactionType, 
    private TransactionType transactionType;
    private BigDecimal transactionAmount;
    
    public BillingInfo(){
        tipAmount = new BigDecimal(0.00);
        totalAmount = new BigDecimal(0.00);
    }

    public CardTrackData getCardTrackData() {
        return cardData;
    }

    public void setCardTrackData(CardTrackData cardData) {
        this.cardData = cardData;
    }

    public BigDecimal getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(BigDecimal tipAmount) {
        this.tipAmount = tipAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;   
    }

    public Order getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(Order customerOrder) {        
        this.customerOrder = customerOrder;
        if(customerOrder == null)
            return;
        billAmount = customerOrder.getTotal();
    }

    public BigDecimal getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public CardTrackData getCardData() {
        return cardData;
    }

    public void setCardData(CardTrackData cardData) {
        this.cardData = cardData;
    }

    public CreditCardInfo getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(CreditCardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    
    public String getRefId() {
        return refId;
    }

    /**
     * refId is usually order id, Up to 20 characters
     * @param refId 
     */
    public void setRefId(String refId) {
        this.refId = refId;
    }

    
    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * Employee's id for this process
     * @param employeeId max 4 digits, digits only
     */
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType type) {
        this.transactionType = type;
    }    

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public CreditCardPmtInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(CreditCardPmtInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }    
    
    
}
