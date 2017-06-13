/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.transaction.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import net.authorize.Environment;
import net.authorize.Merchant;
import net.authorize.ResponseReasonCode;
import net.authorize.TransactionType;
import net.authorize.aim.Transaction;
import net.authorize.aim.cardpresent.Result;
import net.authorize.data.creditcard.CreditCard;
import java.math.RoundingMode;

/**
 *
 * @author Mingxing Chen
 */

/**
 * createTransactionRequest
    Element 	Description 	Format
    merchantAuthentication 	Required.
    Contains merchant authentication information.

    name 	Required.
    Merchant’s unique API Login ID.
    The merchant API Login ID is provided in the Merchant Interface and must be stored securely.

    The API Login ID and Transaction Key together provide the merchant authentication required for access to the payment gateway. 	Up to 20 characters
    transactionKey 	Required.
    Merchant’s unique Transaction Key.
    The merchant Transaction Key is provided in the Merchant Interface and must be stored securely.

    The API Login ID and Transaction Key together provide the merchant authentication required for access to the payment gateway. 	16 characters.
    employeeId 	Merchant-assigned employee ID.
    Required only if your payment processor is EVO. 	Numeric, 4 digits.
    transactionRequest 	Transaction information. This element includes all of the fields that follow.

    transactionType 	Type of credit card transaction.
    If the value submitted does not match a supported value, the transaction is rejected. If this field is not submitted or the value is blank, the payment gateway will process the transaction as an authCaptureTransaction. 	authCaptureTransaction
    amount 	Required.
    Amount of the transaction.
    This is the total amount and must include tax, shipping, and any other charges. The amount can either be hard coded or posted to a script. 	Up to 15 digits with a decimal point (no dollar symbol. For example, 8.95
    payment 	This section includes payment information.

    trackData 	trackData can contain track1 and track2.

    track1 	(Applies to Card Present only.)

    Conditional

    Required only if track2, cardNumber, and expirationDate are absent.
    Track 1 data read from credit card. It is not necessary to submit Track 1 and Track 2 data, and cardNumber and expirationDate. If both tracks are sent by the POS application, the gateway will use the Track 1 information.

    If neither Track 1 nor Track 2 data is submitted, but cardNumber and expirationDate are submitted, the Card Present transaction rate may be downgraded. 	Valid Track 1 data.

    Note: Starting and ending sentinel characters must be discarded before submitting transactions.
    track2 	(Applies to Card Present only.)

    Conditional

    Required only if track1 and cardNumber, and expirationDate are absent.
    Track 2 data read from credit card. This information is required only if Track 1 and cardNumber and expirationDate are absent. It is not necessary to submit Track 1 and Track 2 data, and cardNumber and expirationDate. If both tracks are sent by the POS application, the gateway will use the Track 1 information.

    If neither Track 1 nor Track 2 data is submitted, but cardNumber and expirationDate are submitted, the Card Present transaction rate may be downgraded. 	Valid Track 2 data.

    Note: Starting and ending sentinel characters must be discarded before submitting transactions.
    creditCard 	The following elements belong to the element; include them only for credit card transactions.

    cardNumber 	Required.
    The customer’s credit card number.
    Optional for Card Present.

    This is sensitive cardholder information and must be stored securely and in accordance with the Payment Card Industry (PCI) Data Security Standard.

    For more information about PCI, please refer to the Standards, Compliance and Security developer training video at http://developer.authorize.net/training. 	Between 13 and 16 digits without spaces

    Only the last four digits are required for credit card transactions.
    expirationDate 	Required.
    The customer’s credit card expiration date.
    Optional for Card Present.

    This is sensitive cardholder information and must be stored securely and in accordance with the Payment Card Industry (PCI) Data Security Standard.

    For more information about PCI, please refer to the Standards, Compliance and Security developer training video at http://developer.authorize.net/training. 	One of the following:

    MMYY,
    MM/YY,
    MM-YY, MMYYYY,
    MM/YYYY,
    MM-YYYY
    cardCode 	The customer’s card code.
    The three- or four-digit number on the back of a credit card (on the front for American Express).

    This field is required if the merchant would like to use the Card Code Verification (CCV) security feature.

    Cardholder information must be stored securely and in accordance with the Payment Card Industry (PCI) Data Security Standard.

    For more information about PCI, please refer to the Standards, Compliance and Security developer training video at http://developer.authorize.net/training. 	Numeric
    profile 	The following field enables you to CREATE a customer profile from the data sent to make the transaction.

    createProfile 	true, false
    If set to true, a CIM profile will be
    generated from the customer and
    payment data. 	
    solution 	Contains information about the software that generated the transaction.

    id 	The solution ID is generated by Authorize.Net and provided to the solution provider.
            Alphanumeric. Up to 50 characters.
    order 	Contains information about order.

    invoiceNumber 	Merchant-defined invoice number associated with the order.

    description 	Description of the item purchased. 
     * @author Mingxing Chen
 */
public class PmtTransaction {
    private Merchant merchant;
    private BillingInfo billingInfo;
    private Result<Transaction> result;
    
    public PmtTransaction(BillingInfo info){
        String apiLoginID = "48DuPjdz93";
        String transactionKey = "4669nDbm2RB3a5Mq";
        String MD5Value = "829215";
        merchant = Merchant.createMerchant(Environment.SANDBOX, apiLoginID, transactionKey);
        merchant.setDeviceType(net.authorize.DeviceType.PERSONAL_COMPUTER_BASED_TERMINAL);
        merchant.setMarketType(net.authorize.MarketType.RETAIL);
        merchant.setMD5Value(MD5Value);
        billingInfo = info;
    }
    
    public PmtTransaction(Merchant merchant, BillingInfo billingInfo){
        this.merchant = merchant;
        this.billingInfo = billingInfo;
    }
    
    public static PmtTransaction createTransaction(Merchant merchant, BillingInfo info){
        return new PmtTransaction(merchant, info);
    }

    public static PmtTransaction createTransaction(BillingInfo info){
        return  new PmtTransaction(info);
    }
    
    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public BillingInfo getBillingInfo() {
        return billingInfo;
    }

    public void setBillingInfo(BillingInfo billingInfo) {
        this.billingInfo = billingInfo;
    }

    /**
     * Process billing authorize/capture transaction.
     * 
     * @return 
     */   
    public boolean processTranction(){
        // for new transaction
        // if in billingInfo tip is set with customer consent, the bill will be processed with 
        // TransactionType.AUTH_CAPTURE tranction
        if(billingInfo.getTransactionType() == null){ // new transaction
            if(billingInfo.getTipAmount().compareTo(BigDecimal.ZERO) == 1){
                chargeCreditCard();
            }else{
                // tip not set, default is 0.00
                // authorize only
                authorizeCreditCard();
            }                
        }
            
        return true;
    }
    
    /**
     * Used for charge or authorize a credit card transaction
     * @param type, for AUTH_ONLY and AUTH_CAPTURE
     * @param amount 
     *  @throws java.lang.Exception
     */
    public void processTransaction(TransactionType type, BigDecimal amount){
        
        CreditCard card = CreditCard.createCreditCard();        
        CardTrackData data = billingInfo.getCardTrackData(); 
        if(data != null){ // card info by swipe
            card.setCardPresent(true);
            card.setCardType(data.getCardType());        
            card.setTrack1(data.getTrack1String());
            card.setTrack2(data.getTrack2String());
        }else if(billingInfo.getCardInfo() != null){// card info by entering
            CreditCardInfo cardInfo = billingInfo.getCardInfo();
            card.setCardPresent(true);  // still present but unreadable
            card.setCreditCardNumber(cardInfo.getCreditCardNumber());
            card.setExpirationMonth(cardInfo.getExpirationMonth());
            card.setExpirationYear(cardInfo.getExpirationYear());
            card.setCardType(Luhn.getCardType(cardInfo.getCreditCardNumber()));
        }
        
        Transaction transaction = merchant.createAIMTransaction(type, amount);
        transaction.setCreditCard(card);
        
        // add more info
        result = (Result<Transaction>)merchant.postTransaction(transaction);
        billingInfo.setTransactionType(type);
        billingInfo.setTransactionAmount(amount);
    }
    
    /**
     * For restaurant POS use only.
     * To authorize a transaction, the bill is charged with 20% tip added to the total amount for AUTH_ONLY transaction.
     * After customer signed, if the tip amount is less than 20%, capture bill amount + tip amount as total
     * using PRIOR_AUTH_CAPTURE type; if the tip amount is more than 20%, capture the total authorized amount
     * using PRIOR_AUTH_CAPTURE type, then initial a AUTH_CAPTURE transaction for the additional amount.
     * ---------
     * Use this method to authorize a credit card payment. To actually charge the funds you will need to follow up 
     * with a capture transaction.
     * @return true or false
     */
    public boolean authorizeCreditCard(){
        // type for this charge only
        TransactionType type = TransactionType.AUTH_ONLY;
        BigDecimal chargeAmount = billingInfo.getTotalAmount()
                .multiply(new BigDecimal(1.20)).setScale(2, RoundingMode.HALF_UP);
        try{
            processTransaction(type, chargeAmount);    
        }catch(Exception e){
            return false;
        }
        return true;
    }
    
    /**
     * Use this method to authorize and capture a credit card payment.
     * This transaction if final if succeed.
     * @return true or false
     */
    public boolean chargeCreditCard(){
        // type for this charge only
        TransactionType type = TransactionType.AUTH_CAPTURE;
        BigDecimal chargeAmount = billingInfo.getTotalAmount();
        
        try{
            processTransaction(type, chargeAmount);    
        }catch(Exception e){
            return false;
        }
        return true;       
    }
    
    /**
     * Use this method to capture funds for a transaction that was previously authorized using authOnlyTransaction.
     * @param transactionID
     * @param amount 
     */
    public void captureTransaction(String transactionID, BigDecimal amount){
        TransactionType type = TransactionType.PRIOR_AUTH_CAPTURE;
        Transaction trans = merchant.createAIMTransaction(type, amount);
        trans.setTransactionId(transactionID);
        
        result = (Result<Transaction>)merchant.postTransaction(trans);
        billingInfo.setTransactionType(type);
        billingInfo.setTransactionAmount(amount);
    }
    
    /**
     * This transaction type is used to refund a customer for a transaction that was originally
     * processed and successfully settled through the payment gateway.
     * Use void transaction for order on the same day.
     * The customer's credit card must be presented to process refund transaction to credit card, in case 
     * when customer's credit card info is not stored
     * @param amount
     * @throws java.lang.Exception
     */
    public void refundTransaction(BigDecimal amount) throws Exception{
        processTransaction(TransactionType.CREDIT, amount);
    }
    
    /**
     * Void a Transaction 
     * This transaction type can be used to cancel either an original transaction that is not yet 
     * settled or an entire order composed of more than one transaction. 
     * A Void prevents the transaction or the order from being sent for settlement.
     * A Void can be submitted against any other transaction type
     * @param transactionID
     */
    public void voidTransaction(String transactionID){
        TransactionType type = TransactionType.VOID;
        
        Transaction voidTransaction = merchant.createAIMTransaction(type, BigDecimal.ZERO);
        voidTransaction.setTransactionId(transactionID);
            
        result = (Result<Transaction>)merchant.postTransaction(voidTransaction);
        billingInfo.setTransactionType(type);
    }
    
    public Result<Transaction> getResult(){
        return result;
    }
    
    // testing 
    public static void main(String[] args){
        String swipeStr = "%B4514078527206426^CHEN/MINGXING ^1710201901000001000100066000000?;4514078527206426=17102019010006610001?";
        CreditCardInfo cardInfo = CreditCardInfo.createCreditCard(CardTrackData.createCardTrackData(swipeStr));
        BillingInfo info = new BillingInfo();
        //info.setCardInfo(cardInfo);
        info.setCardTrackData(CardTrackData.createCardTrackData(swipeStr));
        BigDecimal totalAmount = new BigDecimal(9.99);
        info.setTotalAmount(totalAmount);
        
        // credit payment info for store response data
        CreditCardPmtInfo pmtInfo = new CreditCardPmtInfo();
        pmtInfo.setCardInfo(cardInfo);
        pmtInfo.setTransactionAmount(totalAmount);
        // create transaction
        PmtTransaction transaction = new PmtTransaction(info);
        
        
        System.out.println("charge a card for amount 9.99...");
        transaction.authorizeCreditCard();
        
        Result<Transaction> result = transaction.getResult();
        System.out.println("Response: ");
        String transactionID = "";
        if(result.isApproved()){
            System.out.println("approved");
            System.out.println("Auth Code: " + result.getAuthCode());
            System.out.println("Tranaction ID:" + result.getTransId());
            System.out.println("refTrans ID:" + result.getRefTransId());
            transactionID = result.getTransId();
            pmtInfo.setAuthenCode(result.getAuthCode());
            pmtInfo.setTransactionID(transactionID);
        }else if(result.isDeclined()) {
            System.out.println("Card Declined");
        }else if(result.isError()){
            System.out.println("Error:" + result.getResponseCode().getDescription());
        }
        
        
        System.out.println("Tranaction ID:" + transactionID);
        if(transactionID.equals("")){
            transactionID = "2251477102";
            
        }
        System.out.println("now testing capturing");
        // testing capture
        BigDecimal amount = new BigDecimal(10.20);
            
        transaction.captureTransaction(transactionID, amount);
        Result<Transaction> result2 = transaction.getResult();
        if(result2.isApproved()){
            System.out.println("approved for capture");
            System.out.println("Auth Code: " + result2.getAuthCode());
            System.out.println("Tranaction ID:" + result2.getTransId());
            System.out.println("refTrans ID:" + result2.getRefTransId());
        }else if(result2.isDeclined()) {
            System.out.println("Card Declined");
        }else if(result2.isError()){
            System.out.println("Error:" + result2.getResponseCode().getDescription());
        }
        
        System.out.println("testing void transaction...");
        transaction.voidTransaction(transactionID);
        result = transaction.getResult();
        System.out.println("show result....");
        if(result.isApproved()){
            System.out.println("approved");
            System.out.println("Auth Code: " + result.getAuthCode());
            System.out.println("Tranaction ID:" + result.getTransId());
            System.out.println("refTrans ID:" + result.getRefTransId());
            transactionID = result.getTransId();
        }else if(result.isDeclined()) {
            System.out.println("Card Declined");
        }else if(result.isError()){
            System.out.println("Error:" + result.getResponseCode().getDescription());
        }
        System.out.println("after void the transactionID is " +transactionID);
      /**  
        //  refund
        if(result.isApproved()){
            System.out.println("refunding the transaction ...");
            transaction.voidTransaction(pmtInfo);
            result = transaction.getResult();
            ArrayList<ResponseReasonCode> list = result.getResponseReasonCodes();
            System.out.println("Response Code(s) are:");
            for(ResponseReasonCode code:list){
                System.out.println("code name:"+ code.name() + "Reason: " + code.getReasonText());
            }
        }
        */
    }
  /*      Charge a Credit Card
        Authorize a Credit Card
        Capture a Previously Authorized Amount
        Capture Funds Authorized Through Another Channel
        Refund a Transaction
        Void a Transaction
        Update Split Tender Group
        Debit a Bank Account
        Credit a Bank Account
        Charge a Customer Profile
        Charge a Tokenized Credit Card
    Apple Pay Transactions
    PayPal Express Checkout
    Recurring Billing
    Charge Customer Profiles
    Manage Customer Profiles
    Transaction Reporting

*/
}
