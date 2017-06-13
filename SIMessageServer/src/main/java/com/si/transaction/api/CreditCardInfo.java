package com.si.transaction.api;

import com.si.data.CardType;
import com.si.data.LuhnAlg;
import org.json.JSONObject;


/**
 * Simple Credit card information.
 *
 */

public class CreditCardInfo{

    private String creditCardNumber = "";
    private String expirationDate = ""; // "MMYY"
    private String expirationMonth = ""; //"MM"
    private String expirationYear = ""; // "YYYY"
    private CardType cardType = CardType.UNKNOWN;
    private String cardVerificationCode = ""; // card verication code/value
    private String cardHolderName = ""; // First Last
    private CardReadType readType = CardReadType.KEY_ENTER; // default to key enter, if not swipe
    private CardTrackData trackData;
    
    /**
     * Create CreditCardInfo by entering card info
     */
    public CreditCardInfo() {
        readType = CardReadType.KEY_ENTER;
    }

    private CreditCardInfo(CardTrackData data){
        creditCardNumber = data.getPrimaryAccountNumber();
        expirationDate = data.getExpirationDate();
        cardType = LuhnAlg.getCardType(creditCardNumber);
        cardHolderName = data.getName();
        cardVerificationCode = "";
        readType = CardReadType.SWIPE_READ;
        trackData = data;
    }
    
    /**
     * Create CreditCardInfo with JSON data
     * @param json 
     */
    public CreditCardInfo(JSONObject json){
        if(json != null){
            creditCardNumber = json.optString("card_number");
            expirationDate = json.optString("exp_date"); // mmyy
            cardType = CardType.valueOf(json.optString("card_type"));
            cardHolderName = json.optString("card_name");
            cardVerificationCode = json.optString("card_ver_code"); // cvc 
            if(json.has("card_read_type")) // else use default
                readType = CardReadType.valueOf(json.optString("card_read_type"));
            if(json.has("card_track_data")) // else null
                trackData = new CardTrackData(json.optJSONObject("card_track_data"));
        }        
    }
    
    /**
     * Create CreditCardInfo with swipe card data
     * @param CardTrackData
     * @return CreditCardInfo
     */
    public static CreditCardInfo createCreditCard(CardTrackData cardData) {
            return new CreditCardInfo(cardData);
    }

    /**
     * @return the creditCardNumber
     */
    public String getCreditCardNumber() {
            return creditCardNumber;
    }

    /**
     * @param creditCardNumber
     *            the creditCardNumber to set
     */
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
        cardType = LuhnAlg.getCardType(creditCardNumber);
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    /**
     * @return the expirationMonth
     */
    public String getExpirationDate() {
            return expirationDate;
    }

    /**
     * @param expDate in MMYY format
     * the expirationDate to set
     */
    public void setExpirationDate(String expDate) {
            this.expirationDate = expDate;
            if(!expDate.isEmpty() && expDate.length() == 4){
                this.expirationMonth = expDate.substring(0, 2);
                this.expirationYear = "20" + expDate.substring(2);
            }
    }

    /**
     * @return the cardType
     */
    public CardType getCardType() {
            return cardType;
    }

    /**
     * @param cardType
     *            the cardType to set
     */
    public void setCardType(CardType cardType) {
            this.cardType = cardType;
    }

    public String getCardVerificationCode() {
        return cardVerificationCode;
    }

    public void setCardVerificationCode(String cardVerificationCode) {
        this.cardVerificationCode = cardVerificationCode;
    }

    public String getCardMaskNumber()
    {
        int length = creditCardNumber.length();
        String start = creditCardNumber.substring(0, 2);
        String end = creditCardNumber.substring(length-4);
        String mask = "";
        for(int i=0; i <length-6; i++){
            mask += "X";
        }
        return start + mask + end;
    }

    public String getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(String expirationMoth) {
        this.expirationMonth = expirationMoth;
    }

    public String getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(String expirationYear) {
        this.expirationYear = expirationYear;
    }

    public CardReadType getReadType() {
        return readType;
    }

    public void setReadType(CardReadType readType) {
        this.readType = readType;
    }

    public CardTrackData getTrackData() {
        return trackData;
    }
     
    public JSONObject toJSONObject(){
        JSONObject json = new JSONObject();
        json.put("card_number", creditCardNumber);
        json.put("exp_date", expirationDate); 
        json.put("card_type", cardType.toString());
        json.put("card_name", cardHolderName);
        json.put("card_ver_code", cardVerificationCode);
        json.put("card_read_type", readType.toString());
        if(trackData != null)
            json.put("card_track_data", trackData.toJSONObject()); 
        return json;
    }
    
    static enum CardReadType {
        SWIPE_READ,
        KEY_ENTER,
        OTHER   // maybe from iPhone Wallet
    }
}
