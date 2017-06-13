/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.resource;

import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
public enum ProtocolType {
    AUTH("auth"), /* authentication */
    ACK("ack"), /* acknowledge */
    REGISTER("register"),
    ADD_ORDER("add_order"),
    UPDATE_ORDER("update_order"),
    UPDATE_TIME("update_time"),
    DELETE_ORDER("delete_order"),
    LOCK_TABLE("lock_table"),
    UNLOCK_TABLE("unlock_table"),    
    LOCK_ORDER("lock_order"),
    UNLOCK_ORDER("unlock_order"),
    SEND_ORDER("send_order"),
    SYNC_ORDERS("sync_orders"),
    SYNCH_MENU("synch_menu"), /* synchronize menu to have most recent updated menu*/
    UPLOAD_MENU("upload_menu"),
    DOWNLOAD_MENU("download_menu"),
    REQUEST_ORDER_ID("request_order_id"),
    RESPONSE_ORDER_ID("response_order_id");
    
    private final String protocol_type;
    
    private ProtocolType(){
        protocol_type = "";
    }
    
    ProtocolType(String type){
        protocol_type = type;
    }    
        
    public static ProtocolType valueOfName(String pType){
        ProtocolType valueType = null;
        for(ProtocolType type:ProtocolType.values()){
            if(type.toString().equals(pType)){
                valueType = type;
                break;
            }
        }
        return valueType;
    }
         
    public JSONObject toJSONObject(){
        return new JSONObject().put("protocol_type", protocol_type);
    }
    
    @Override
    public String toString(){
        return protocol_type;
    }
}
