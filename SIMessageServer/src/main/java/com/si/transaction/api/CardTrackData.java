/*
    Card Data Format Track 1:
    Start sentinel — one character (generally '%')
    Format code="B" — one character (alpha only)
    Primary account number (PAN) — up to 19 characters. Usually, but not always, matches the credit card number printed on the front of the card.
    Field Separator — one character (generally '^')
    Name — 2 to 26 characters
    Field Separator — one character (generally '^')
    Expiration date — four characters in the form YYMM.
    Service code — three characters
    Discretionary data — may include Pin Verification Key Indicator (PVKI, 1 character), 
        PIN Verification Value (PVV, 4 characters), Card Verification Value or Card Verification Code (CVV or CVC, 3 characters)
    End sentinel — one character (generally '?')
    Longitudinal redundancy check (LRC) — it is one character and a validity character calculated from other data on the track.


CVV2 = Visa 
CVC2 = MasterCard 
CID = American Express and Discover 

*/

package com.si.transaction.api;

import net.authorize.data.creditcard.CardType;
import org.json.JSONObject;

/**
 * Card Track Data process track 1&2 data, Card reader reads track1 and track2 data in one line without space
 * @author Mingxing Chen
 */
public class CardTrackData{ 
    private final String start1Sentinel = "%"; // track 1 starting sentinel
    private final String start2Sentinel = ";"; // track 2 starting sentinel 
    // for bank card only, no other card will be processed
    private final String formatCode = "B";  
    private final String field1Separator = "^"; // track 1 field separator
    private final String field2Separator = "="; // track 2 field separator
    private final String endSentinel = "?"; // both track end with the same sentinel
    
    // card reader string may contain both track1 & track2, or just one of them
    private String trackString; // card swiper one swipe read in string
    private String track1String;
    private String track2String;
       
    private String primaryAccountNumber;
    private String name;
    private String firstName;
    private String lastName;
    private String expirationDate; // foramt MMYY
    private String expirationMonth;
    private String expirationYear;
    private String serviceCode;
    private String discretionaryData;
    
    
    private CardType cardType;
    
    private CardTrackData(){
        trackString = "";
        track1String = "";
        track2String = "";
        discretionaryData = "";
        primaryAccountNumber = "";
        expirationDate = "";
        firstName = "";
        lastName = "";
    }
    
    /**
     * Create a credit card with card track string, read from card swipe reader
     * @param trackString 
     * @throws CardFormatException extends java.lang.Exception, if card reading error.
     */
    public CardTrackData(String trackString) throws Exception{
        processTarckData(trackString);
    }
    
    public CardTrackData(JSONObject json){
        trackString = json.optString("track_string");
        try{
            processTarckData(trackString);
        }catch(Exception exception){
            System.out.println("error: " + exception.getMessage());
        }
    }
    
    private void processTarckData(String trackStr ) throws Exception{
        this.trackString = trackStr;
        // check start and end
        if(trackString.startsWith(start1Sentinel)|| trackStr.startsWith(start2Sentinel) && trackString.indexOf(endSentinel) != -1)
            if(trackStr.startsWith(start2Sentinel) && trackStr.endsWith(endSentinel)){// only track2 string
                track1String = "";
                track2String = trackStr;
           p("track2 only");     
                processTrack2();
            }else if(trackStr.indexOf(start2Sentinel) == -1){ // only track1 string
                track1String = trackStr;
                track2String = "";
           p("track1 only");
                processTrack1();
            }else if(trackStr.indexOf(start2Sentinel) != -1){
                int index = trackStr.indexOf(start2Sentinel);
                track1String = trackStr.substring(0, index);
                track2String = trackStr.substring(index);
           p("both tracks");
                processTrack1();
            }
        else throw new CardFormatException("Credit Card Format Error"); 
    }

    private void processTrack1() throws Exception{
        if(track1String.isEmpty())
            return;
        
        int length = track1String.length();
        if(!formatCode.equals(track1String.substring(1, 2)))
            throw new CardFormatException("Not a Valid Credit Card");
        
        String dataString = track1String.substring(2, length-1); // contain no sentinel
        int index = dataString.indexOf(field1Separator);
        primaryAccountNumber = dataString.substring(0, index);
        cardType = Luhn.getCardType(primaryAccountNumber);
        
        dataString = dataString.substring(index+1);
        
        index = dataString.indexOf(field1Separator);
        name = dataString.substring(0, index);
        lastName = name.substring(0, name.indexOf("/"));
        firstName = name.substring(name.indexOf("/")+1);
        
        dataString = dataString.substring(index+1);
        
        String dateString = dataString.substring(0, 4);
        expirationYear = "20" + dateString.substring(0, 2);
        expirationMonth = dateString.substring(2);
        expirationDate = dateString.substring(2) + dateString.substring(0, 2); // "MMYY"
        
        dataString = dataString.substring(4);
        
        serviceCode = dataString.substring(0, 3);
        
        // no processing for the rest code
        dataString = dataString.substring(3);
        discretionaryData = dataString;
    }
    
    private void processTrack2() throws Exception{
        if(track2String.isEmpty())
            return;
        int index = track2String.indexOf(field2Separator);
        if(index == -1)
             throw new CardFormatException("Not a Valid Credit Card");
        primaryAccountNumber = track2String.substring(0, index);      
        cardType = Luhn.getCardType(primaryAccountNumber);
        
        String dataString = track2String.substring(index+1);
        
        String dateString = dataString.substring(0, 4);
        expirationYear = "20" + dateString.substring(0, 2);
        expirationMonth = dateString.substring(2);
        expirationDate = dateString.substring(2) + dateString.substring(0, 2); // "MMYY"
        
        dataString = dataString.substring(4);
        
        serviceCode = dataString.substring(0, 3);
        
        // no processing for the rest code
        dataString = dataString.substring(3);
        discretionaryData = dataString;
    }
    
    public String getTrackString() {
        return trackString;
    }

    public void setTrackString(String trackString) {
        this.trackString = trackString;
    }

    public String getPrimaryAccountNumber() {
        return primaryAccountNumber;
    }

    public void setPrimaryAccountNumber(String primaryAccountNumber) {
        this.primaryAccountNumber = primaryAccountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getTrack1String() {
        return track1String;
    }

    public String getTrack2String() {
        return track2String;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getExpirationMonth() {
        return expirationMonth;
    }

    public String getExpirationYear() {
        return expirationYear;
    }

    public CardType getCardType() {
        return cardType;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public JSONObject toJSONObject(){
        return new JSONObject().put("track_string", trackString);
    }
    
    private static class CardFormatException extends Exception {

        public CardFormatException(String message) {
            super(message);
        }
    }
    
    /**
     * Credit card data read from swipe reader.
     * @param trackString
     * @return CardTrackData if trackString is valid, null if not valid format from card reading
     */
    public static CardTrackData createCardTrackData(String trackString){
        CardTrackData data = null;
        try{
            data = new CardTrackData(trackString);            
        }catch (Exception e){     
            System.out.println("invalid track number");
            return null;
        }
        return data;
    }
    
    public static void p(String str){
        System.out.println(str);
    }
    
    public static void main(String[] args){
        String str = "%B4514078527206426^CHEN/MINGXING ^1710201901000001000100066000000?;4514078527206426=17102019010006610001?";
       // str = ";4514078527206426=17102019010006610001?";
        CardTrackData data = new CardTrackData();
        try{
            data.processTarckData(str);
            p("acc#:" + data.getPrimaryAccountNumber());
            p("Name: first: " + data.getFirstName() + " last: " + data.getLastName());
            p("Expiration Date: " + data.getExpirationDate());
            p("Card type: " + data.getCardType().toString() );
            p("Service Code:" + data.getServiceCode());
        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        
        
    }
}