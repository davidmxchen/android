/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.transaction.api;

/**
 *
 * 

<%
     String apiLoginID = "YOUR_API_LOGIN_ID";
     String transactionKey = "YOUR_TRANSACTION_KEY";
     String MD5Value = "YOUR_API_LOGIN_ID";
  
     Merchant merchant = Merchant.createMerchant(
Environment.SANDBOX, apiLoginID, transactionKey);
     merchant.setDeviceType(net.authorize.DeviceType.VIRTUAL_TERMINAL);
     merchant.setMarketType(net.authorize.MarketType.RETAIL);
     merchant.setMD5Value(MD5Value);

    // Create credit card
    CreditCard creditCard = CreditCard.createCreditCard();
    creditCard.setCardType(CardType.VISA);
    creditCard.setTrack1("%B4111111111111111^CARDUSER/JOHN^1803101000000000020000831000000?");
    creditCard.setTrack2(";4111111111111111=1803101000020000831?");

    // Create transaction
    Transaction authCaptureTransaction = merchant.createAIMTransaction(
TransactionType.AUTH_CAPTURE, new BigDecimal(1.99));
    authCaptureTransaction.setCreditCard(creditCard);

    Result<Transaction> result = (Result<Transaction>)merchant.postTransaction(
authCaptureTransaction);

    if (result.isApproved()) {
         out.println("Approved!</br>");
         out.println("Transaction Id: " + result.getTransId());
    } else if (result.isDeclined()) {
         out.println("Declined.</br>");
         out.println(result.getResponseReasonCodes().get(0) + " : " +
result.getResponseReasonCodes().get(0).getReasonText());
    } else {
         out.println("Error.</br>");
         out.println(result.getResponseReasonCodes().get(0) + " : " +
result.getResponseReasonCodes().get(0).getReasonText());
    }
%>
 */

import java.math.BigDecimal;
import java.util.Map;
import net.authorize.Environment;
import net.authorize.Merchant;
import net.authorize.TransactionType;
import net.authorize.aim.cardpresent.Result;
import net.authorize.aim.Transaction;
import net.authorize.data.*;
import net.authorize.data.creditcard.*;
        
public class Authorize {
    
    private String apiLoginID = "48DuPjdz93";
    private String transactionKey = "4669nDbm2RB3a5Mq";
    private String MD5Value = "829215";
    private Merchant merchant;
    private CreditCard creditCard;
    private Result<Transaction> result;
    
    public Authorize(){
        merchant = Merchant.createMerchant(Environment.SANDBOX, apiLoginID, transactionKey);
        merchant.setDeviceType(net.authorize.DeviceType.VIRTUAL_TERMINAL);
        merchant.setMarketType(net.authorize.MarketType.RETAIL);
        merchant.setMD5Value(MD5Value);
    }
    
    
    public void setCreditCard(CreditCard card){
        creditCard = card;
    }
    
    public void setCardTrackData(CardTrackData data){
        creditCard = CreditCard.createCreditCard();
        creditCard.setCardType(CardType.VISA);
        creditCard.setTrack1(data.getTrack1String());
        creditCard.setTrack2(data.getTrack2String());
    }
    
    public void doAuthorize(TransactionType type, double amount){
        Transaction authCaptureTransaction = merchant.createAIMTransaction(type, new BigDecimal(amount));
        authCaptureTransaction.setCreditCard(creditCard);
        result = (Result<Transaction>)merchant.postTransaction(authCaptureTransaction);
    }

    public Result<Transaction> getResult(){
        return result;
    }
    
    public static void main(String[] args){
        Authorize authorize = new Authorize();
        String trackString = "%B4514078527206426^CHEN/MINGXING ^1710201901000001000100066000000?;4514078527206426=17102019010006610001?";
        CardTrackData data = CardTrackData.createCardTrackData(trackString);
        authorize.setCardTrackData(data);
        authorize.doAuthorize(TransactionType.AUTH_CAPTURE, 1.99);
        Result<Transaction> result = authorize.getResult();
        
        if(result == null)
            System.out.println("result null");
        System.out.println("Card response Code: "+ result.getCardCodeResponse() );
        System.out.println("Auth Code: " + result.getAuthCode());
        
        if (result.isApproved()) {
            System.out.println("Approved!");
            System.out.println("Transaction Id: " + result.getTransId());
        } else if (result.isDeclined()) {
            System.out.println("Declined.");
            System.out.println(result.getResponseReasonCodes().get(0) + " : " +
            result.getResponseReasonCodes().get(0).getReasonText());
        } else {
            System.out.println("Error.");
            System.out.println(result.getResponseReasonCodes().get(0) + " : " +
            result.getResponseReasonCodes().get(0).getReasonText());
        }
    
    }
}
