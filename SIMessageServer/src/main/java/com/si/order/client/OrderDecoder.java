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
import com.si.data.Order;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderDecoder implements Decoder.Text<Order>{

    @Override
    public Order decode(String jsonStr) throws DecodeException {
        JSONObject orderJSONObject = new JSONObject(jsonStr);
        return new Order(orderJSONObject);        
    }

    @Override
    public boolean willDecode(String jsonStr) {
        try{
            new JSONObject(jsonStr);
            return true;
        }catch (JSONException ex){
            return false;
        }
    }

    @Override
    public void init(EndpointConfig ec) {
        System.out.println("init");
    }

    @Override
    public void destroy() {
        System.out.println("destroy");
    }
    
}
