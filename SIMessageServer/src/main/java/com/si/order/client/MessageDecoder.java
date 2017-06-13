/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.order.client;

/**
 *
 * @author Mingxing Chen
 */

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageDecoder implements Decoder.Text<JSONObject>{

    /**
     * Decode any valid json string as JSONObject
     * @param message, must be a valid json sting
     * @return JSONObject
     * @throws DecodeException 
     */
    @Override
    public JSONObject decode(String jsonStr) throws DecodeException {
     
        JSONObject object = null;
        try{
            object = new JSONObject(jsonStr);
        }catch(JSONException ex){
            System.out.println("decoding error, invalid json string ==>" + ex.getMessage());
            throw new DecodeException("error", ex.getMessage());
        }
        return new JSONObject(jsonStr);
    }

    @Override
    public boolean willDecode(String jsonStr) {
        System.out.println("call willDecoding now");
        try{
            new JSONObject(jsonStr);
            return true;
        }catch (JSONException ex){
            System.out.println("decoding error");
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void init(EndpointConfig ec) {
        System.out.println("MessageDecoder init");
    }

    @Override
    public void destroy() {
        System.out.println("MessageDecoder destroy");
    }
    
}
