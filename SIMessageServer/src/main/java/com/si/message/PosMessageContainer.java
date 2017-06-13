/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.message;


import com.si.data.Categories;
import com.si.data.Order;
import com.si.data.Time;
import com.si.resource.ProtocolType;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import javafx.collections.FXCollections;
import javax.websocket.Session;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javax.websocket.EncodeException;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
/**
 * PosMessgeContainer contains all active information of a business POS
 * Each business POS system can only have one instance of PosMessageContainer, all hosts of
 * a business POS system access and updates to one instance of PosMessageContainer
 */
public class PosMessageContainer {
    // Current connected pos session
    private final ObservableList<Session> activeSessions;
    // Current active orders, use order's unique id as key, to map the order
    private final ObservableMap<String, Order> activeOrders;
    // Current locked order by user
    private final ObservableMap<String, Lock> orderLocks;
    // When POS server is not connected, "Orders send to printers" cannot be processed. These message must be
    // stored for future resend
    private final ObservableList<JSONObject> unSentOrderList;
    //Credential contains all info of the business including online payment transaction auth info
    private JSONObject credential;
    // Menu of the restaurant business
    private Categories menu;
    private Time lastMenuUpdateTime = new Time(0l); // assign to zero, this cannot be null;
    private Session SERVER_SESSION;
    private Hashtable<String, Bind> bindMap;
    private final String PROTOCOL = "protocol_type";
    private final String BUS_ID = "business_id";
    private Bind serverBind;
    
    public PosMessageContainer(){
        activeSessions = FXCollections.observableArrayList();
        activeOrders = FXCollections.observableHashMap();
        orderLocks = FXCollections.observableHashMap();
        unSentOrderList = FXCollections.observableArrayList();
        credential = new JSONObject();
        bindMap = new Hashtable<>();
    }
    
    /**
     * Order must be validated before add to the container. Only online order add to container
     * through this method.
     * @param order 
     */
    public void addOnlineOrder(Order order){
        System.out.println("received online order, send to all active pos");
        activeOrders.put(order.getUniqueId(), order);
        // prepare protocol and send to pos
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(PROTOCOL, ProtocolType.ADD_ORDER);
        jSONObject.put("order", order);
        
        try{
            for(Session session:activeSessions){
                session.getBasicRemote().sendObject(jSONObject);
            }
        }catch(IOException ioe){}catch(EncodeException ee){
            System.out.println("PosMessageContainer Failed to send order to POS, Error:" + ee.getMessage());
            unSentOrderList.add(jSONObject);
        }
    }

    public ObservableList<Session> getActiveSessions() {
        return activeSessions;
    }

    public ObservableMap<String, Order> getActiveOrders() {
        return activeOrders;
    }

    public JSONObject getCredential() {
        return credential;
    }
    
    /**
     * Get the Business ID of this MessageContainer
     * @return Unique Business ID
     */
    public String getBusinessID(){
        return credential.optString(BUS_ID);
    }
    
    public void setCredential(JSONObject cred){
        credential = cred;
    }

    public Session getServerSession() {
        return SERVER_SESSION;
    }

    public void setMenu(Categories menu){
        this.menu = menu;
    }
    
    public Categories getMenu(){
        return this.menu;
    }
    
    public Time getLastMenuUpdateTime(){
        return lastMenuUpdateTime;
    }

    public void setLastMenuUpdateTime(Time lastMenuUpdateTime) {
        this.lastMenuUpdateTime = lastMenuUpdateTime;
    }
    
    public ObservableMap<String, Lock> getOrderLocks() {
        return orderLocks;
    }
    
    public void setServerSession(Session SERVER_SESSION) {
        this.SERVER_SESSION = SERVER_SESSION;
    }

    public ObservableList<JSONObject> getUnSentOrderList() {
        return unSentOrderList;
    }
    
    public boolean isLocked(String orderId){
        return orderLocks.get(orderId) != null;
    }
    
    public void unLock(String orderId){
        orderLocks.remove(orderId);
    }
    
    public void createLock(String orderId, JSONObject lockerInfo, Session session){
        Lock lock = new Lock(session, lockerInfo, orderId);
        orderLocks.put(orderId, lock);
    }    

    public Bind getServerBind(String id) {
        System.out.println("request user bind id==>" + id);
        if(bindMap.get(id) == null)
            System.out.println("does not exist");
        return bindMap.get(id);
    }

    public void createBind(Session request, Session resp) {
        String userId = request.getUserProperties().get("user_id").toString();
        Bind bind = new Bind(resp, resp);
        bindMap.put(userId, bind);
        
        System.out.println("user bind: user id=" + userId);
    }
    
    public void releaseBind(String id){
        Bind bind = bindMap.get(id);
        if(bind != null){
            bind.releaseBinding();
            bindMap.remove(id);
        }
    }
    
    public static class Lock {
        private Session lockSession;
        private JSONObject lockerInfo;
        private String orderId;

        public Lock(Session lockSession, JSONObject lockerInfo, String orderId) {
            this.lockSession = lockSession;
            this.lockerInfo = lockerInfo;
            this.orderId = orderId;
        }

        public Session getLockSession() {
            return lockSession;
        }

        public void setLockSession(Session lockSession) {
            this.lockSession = lockSession;
        }

        public JSONObject getLockerInfo() {
            return lockerInfo;
        }

        public void setLockerInfo(JSONObject lockerInfo) {
            this.lockerInfo = lockerInfo;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
    }
    
    // Bind class used to bind two session, usualy a client session to server session for secure 
    // communication between two pos endpoints. Bind setup request and response protocol between
    // two endpoints. Onece the request and response communication finished, the bind is released
    public static class Bind {
        private Session requestSession;
        private Session responseSession;
        
        public Bind(Session req, Session resp){
            requestSession = req;
            responseSession = resp;
        }
        
        public void releaseBinding(){
            requestSession = null;
            responseSession = null;
        }
        
        public Session getRequestSession(){
            return requestSession;
        }
            
        public Session getResponseSession(){
            return responseSession;
        }
    }
}
