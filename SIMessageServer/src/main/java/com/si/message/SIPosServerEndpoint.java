/**
 *
 * @author Mingxing Chen
 */

package com.si.message;

import com.si.data.Order;
import com.si.order.client.MessageDecoder;
import com.si.order.client.MessageEncoder;
import com.si.resource.ProtocolType;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.JSONObject;

@ServerEndpoint(value="/si_pos_server_endpoint", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class SIPosServerEndpoint {

    private static final Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());   

    /**
     * Maintain session lists for all registered business
     * Key: business ID
     * Value: ArrayList of active session from hosts of the business
     */
    static final ObservableMap<String, ObservableList<Session>> sessionMap = FXCollections.observableHashMap();
    static{
        sessionMap.addListener(new MapChangeListener<String, ObservableList<Session>>() {

            @Override
            public void onChanged(MapChangeListener.Change<? extends String, ? extends ObservableList<Session>> change) {
                System.out.println("sessionMap new key: " + change.getKey());                
            }
        });
    }
    //private static final HashMap<String, ArrayList<Session>> sessionMap = new HashMap<>();
    
    /**
     * Maintain an Order lists for all registered business
     * Key: business ID
     * Value: All active orders of the business, use HashMap for the orders list for easy access
     */
    static final ObservableMap<String, ObservableList<Order>> orderListMap = FXCollections.observableHashMap();
    static {
        orderListMap.addListener(new MapChangeListener<String, ObservableList<Order>>() {

            @Override
            public void onChanged(MapChangeListener.Change<? extends String, ? extends ObservableList<Order>> change) {
                System.out.println("new order added to orderListMap key = " + change.getKey());            
            }
        });
    }

    @OnOpen
    public String onOpen(Session session) {  
        /**
         * OnOpen, session created, send public key to client endpoint to get client credential
         * for authentication, after authentication, the session is set with user's property with
         * business id and added to sessionMap using the business id as key
         */
        try{
            // handshaking to client, if exception happen, session is not added to peers
            if(session != null){
                JSONObject object = new JSONObject().put("protocol_type", ProtocolType.AUTH)
                        .put("pub_key", "xxxxyyyyzzzz");
                
//                Map<String, Object> userProperty = session.getUserProperties();
//                userProperty.put("pub_key", "get_key");
                session.getBasicRemote().sendText(object.toString());
                
                peers.add(session);   
                System.out.println("one session added to " + peers.size() + "sessions");
            }
            
        }catch(IOException e){
            System.out.println("error onOpen: " + e.getMessage());
        }
        System.out.println("New client connection, public key send to client");
        return "message return from onOpen";
    }     
    
    @OnClose
    public void onClose(Session session) {
        System.out.println("close session called");
        peers.remove(session);
        String id = session.getUserProperties().get("business_id").toString();
        sessionMap.get(id).removeAll(session);
    }  
    
    @OnMessage
    public void onMessage(String message, Session session) throws IOException, EncodeException{
       // System.out.println("message is: \n" + message + "\n");
        
        JSONObject object = new JSONObject(message);
        ProtocolType type = ProtocolType.valueOfName(object.optString("protocol_type"));
        
        System.out.println("protocol type: " + type);
        
        if(type == ProtocolType.AUTH){
            String busId = object.optString("business_id");
            System.out.println("business ID is:" + busId);
            session.getUserProperties().put("business_id", busId);
            
            ObservableList<Session> list = sessionMap.get(busId);
            if(list == null){
                list = FXCollections.observableArrayList();
                sessionMap.put(busId, list);
                System.out.println("Session map add " + busId + " with new list");
            }
            list.add(session);
            
            //testing return object to session for debug
            if(list.size() < 2)
                session.getBasicRemote().sendObject(object);
            System.out.println("now this business (id=" + busId + ") has " + list.size() + " sessions"); 
            
        }else if(type == ProtocolType.ADD_ORDER){
            if(object.get("order") == null){
                System.out.println("wrong msg");
                return;
            }
            // adding order
            Order order = null;
            if(object.get("order") instanceof JSONObject){
                order = new Order(object.getJSONObject("order"));
                System.out.println("----is JSONObject");
            }else if(object.get("order") instanceof String){
                order = new Order(new JSONObject(object.optString("order")));
                System.out.println("----is String");
            }else {
                System.out.println("not know type");
                return;
            }
            String busid = session.getUserProperties().get("business_id").toString();//object.getString("business_id");
            System.out.println("business id for this session is: " + busid);
            System.out.println("received new order, order id is: " + order.getOrderId());
            
            ObservableList<Order> list = orderListMap.get(busid);
            if(list == null){
                System.out.println("business id " + busid + " has no order, adding now");
                list = FXCollections.observableArrayList();
                
                list.addListener(new ListChangeListener<Order>() {
                    @Override
                    public void onChanged(ListChangeListener.Change<? extends Order> change) {
                        for(Order o:change.getList()){
                            System.out.println("orders added order id = " + o.getOrderId());
                        }                        
                    }
                });
                
                list.add(order);
                orderListMap.put(busid, list);
                System.out.println("order list added to orderListMap");
            }else{
                System.out.println("business id -" + busid + "- has orders " + list.size());
                list.add(order);
            }
            
            System.out.println("No. of session active is " + sessionMap.get(busid).size());
            // send new order to other host through session
            for(Session se:sessionMap.get(busid)){
                if(se != session){
                    System.out.println("sending update....");
                    se.getBasicRemote().sendObject(object);
                }
            }
        }
        System.out.println("_____________________________________________");   
    }
    
    
    public void broadcastFigure(Order order, Session session) throws IOException, EncodeException {
        System.out.println("I received the order and is send back to client:\n" + order.toJSONObject().toString());
        
        for (Session peer : peers) {
                peer.getBasicRemote().sendObject(order);   
                if(peer == session){
                    System.out.println("same order sent back");
                }
        }
    }

}
