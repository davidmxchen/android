/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.transaction.api;

import com.si.data.CardType;
import com.si.data.Time;
import java.math.BigDecimal;
import net.authorize.TransactionType;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */

/**
 * CreditCardPmtInfo represents one credit card transaction info, the TransactionType represent transaction
 * stage of AUTH_ONLY, AUTH_CAPTURE, PRIOR_AUTH_CAPTURE/CAPTURE_ONLY, to finalize a transaction should be captured.
 * TransactionType can also be VOID for the void transaction and CREDIT for the refund transaction.
 * The CreditCardPmtInfo contains customer's sensitive card data and must be protected.
 */
public class CreditCardPmtInfo {
    // Credit card payment info must contain either CreditCardInfo or CardTrackData 
    private CreditCardInfo cardInfo;
    private CardTrackData trackData;
    
    private String authenCode;
    private String transactionID;
    private Time transTime;
    // TransactionType AUTH_ONLY must follow by PRIOR_AUTH_CAPTURE
    // TransactionType must be PRIOR_AUTH_CAPTURE/CAPTURE_ONLY or AUTH_CAPTURE to finalize payment
    private TransactionType transactionType;
    // NOTE:
    // Bill authorize amount is uaually 20% more than bill amount(for tip), if customer signed tip is less than
    // 20%, the bill amount plus tip amount is the captured (final authenAmount), no additional amount is charged
    // If customer signed more than 20% of bill amount, the full prior authen amount is captured, the extra is 
    // charged by AUTH_CAPTURE tranction and added to additional amount
    
    // the authorized or final captured amount
    private BigDecimal transactionAmount;
    
    public CreditCardPmtInfo(){
        transTime = new Time(System.currentTimeMillis());
    }
    
    public CreditCardPmtInfo(JSONObject json){
        
    } 

    public CardTrackData getTrackData() {
        return trackData;
    }

    public void setTrackData(CardTrackData trackData) {
        this.trackData = trackData;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType type) {
        this.transactionType = type;
    }

    public CreditCardInfo getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(CreditCardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    public String getAuthenCode() {
        return authenCode;
    }

    public void setAuthenCode(String authenCode) {
        this.authenCode = authenCode;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public Time getTransTime() {
        return transTime;
    }

    public void setTransTime(Time transTime) {
        this.transTime = transTime;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public JSONObject toJSONObject(){
        return null;
    }
    
}
