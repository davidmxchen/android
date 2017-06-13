/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.order.client;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
public class MessageEncoder implements Encoder.Text<JSONObject>{

    @Override
    public String encode(JSONObject json) throws EncodeException {
        return json.toString();        
    }

    @Override
    public void init(EndpointConfig ec) {
        
        System.out.println("MessageEncoder init");
    }

    @Override
    public void destroy() {
        System.out.println("MessageEncoder destroy");
    }
    
}
