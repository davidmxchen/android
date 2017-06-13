/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.message;

import com.si.data.Categories;
import com.si.data.Category;
import com.si.data.ImageData;
import com.si.data.Order;
import com.si.data.OrderID;
import com.si.data.Time;
import com.si.order.client.MessageDecoder;
import com.si.order.client.MessageEncoder;
import com.si.resource.ProtocolType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
@ServerEndpoint(value="/pos_message_server_endpoint", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class PosMessageServerEndPoint {

    /**
     * Maintain a lists PosMessageContainer for all registered business, one container for each business
     * Key: business ID
     * Value: PosMessageContainer of the business
     */
    public static final ObservableMap<String, PosMessageContainer> BUS_LIST = FXCollections.observableHashMap();
    static{ // testing code
        BUS_LIST.addListener(new MapChangeListener<String, PosMessageContainer>() {

            @Override
            public void onChanged(MapChangeListener.Change<? extends String, ? extends PosMessageContainer> change) {
                System.out.println("new business id=" + change.getKey() + " added");                
            }
        });
    }

    static final ArrayList<Session> testSession = new ArrayList();
    private final String PROTOCOL = "protocol_type";
    private final String ACK = "ack_id";
    private final String BUS_ID = "business_id";
    private final String UNIQUE_ID = "unique_id";
    private final String USER_ID = "user_id";
    
    @OnOpen
    public void onOpen(Session session) {  
        /**
         * OnOpen, session created, send request to client endpoint to get client credential
         * for authentication, after authentication, the session is set with user's property with
         * business id and added to sessionMap using the business id as key
         */
        try{
            // handshaking to client,
            JSONObject object = new JSONObject().put(PROTOCOL, ProtocolType.AUTH)
                    .put("request", "credential");
            session.getBasicRemote().sendText(object.toString()); 
        }catch(IOException e){
            System.out.println("error onOpen: " + e.getMessage());
        }
    }     
    
    @OnClose
    public void onClose(Session session) {
        System.out.println("close session called");
        String id = session.getUserProperties().get(BUS_ID).toString();
        BUS_LIST.get(id).getActiveSessions().removeAll(session);
    }  
    
    @OnMessage
    public void onMessage(JSONObject object, Session session) throws IOException, EncodeException{
        ProtocolType type = ProtocolType.valueOfName(object.optString(PROTOCOL));  
        System.out.println("protocol type====>" + type);
 //       System.out.println("object is: ==>" + object.toString());
        // This is the time stamp set to order's updateTime, it's created and maintained by this 
        //PosMessageServerEndpoint. No other class should allowed to set or reset the updateTime.
        long updateTime = System.currentTimeMillis();
        PosMessageContainer container = null;
        String uniqueId = null;        
        Session serverSession = null;
        JSONObject ackObj = null;
        String ackId = object.optString(ACK);
        if(!ackId.isEmpty()){
            ackObj = new JSONObject().put(PROTOCOL, ProtocolType.ACK).put(ACK, ackId);
            session.getBasicRemote().sendObject(ackObj);
        }
        switch(type){
            case AUTH:
                System.out.println("switch AUTH");
                // get the credential of client
                JSONObject credential = object.optJSONObject("credential");
                // no credential, no connection
                if(credential == null){
                    session.getBasicRemote().sendObject(new JSONObject().put("connection", "refused"));
                    session.close();
                    return;
                }   
                /**
                 * Testing code for image string to file
                 * imageObj.put("name", "jpg");
            imageObj.put("file_name", "zoom_plus.jpg");
            imageObj.put("image_string", imageString);
            CREDENTIAL.put("image_json", imageObj);
                 */
                
                System.out.println("testing to write image to file");
                 JSONObject imageObj = credential.optJSONObject("image_json");
                 if(imageObj != null){
                     String name = imageObj.optString("name");
                     String fileName = imageObj.optString("file_name");
                     String imageString = imageObj.optString("image_string");
                     try {
                         File imageFile = new File(fileName);
                         ImageData.writeImageStringToFile(imageString, name, imageFile);
                         System.out.println("write success to " + imageFile.getAbsolutePath());
                     } catch (IOException e) {
                         System.out.println("Filed to write image file");
                     }
                 }
                 /* 
                 */
                // get business id from credential           
                String busId = credential.optString(BUS_ID);  
                // user id from every pos should be unique, this is used to identify session
                String userId = credential.optString(USER_ID);
                if(userId.isEmpty())
                    userId = System.currentTimeMillis() + "";
                session.getUserProperties().put(BUS_ID, busId);
                session.getUserProperties().put(USER_ID, userId);
                //--------------------------------------------
                // add auth procedure here for authentication, use database connection

                //--------------------------------------------
                container =  BUS_LIST.get(busId);
                if(container == null){
                    container = new PosMessageContainer();
                    container.setCredential(credential);
                    BUS_LIST.put(busId, container);
                }
                // for easy access to container
                session.getUserProperties().put("container", container);
                container.getActiveSessions().add(session);            

                // testing for server session , must change later*************
                if(credential.optString("role").equals("server"))
                    container.setServerSession(session);

                // send back auth to this client
                JSONObject auth = new JSONObject().put(PROTOCOL, ProtocolType.AUTH).put("status_code", "authenticated");
                System.out.println("sending back ack to this session");
                session.getBasicRemote().sendObject(auth);

                System.out.println("id=" + busId + " has " + container.getActiveSessions().size() + " sessions"); 

                break;
            case SEND_ORDER:                
                System.out.println("preparing sending send_order message");           
                // process case for delayed SEND_ORDER message, in case when one pos have disconnected to this endpoint
                // then reconnected, the message is delayed and other pos may modify this order and sent message to change
                // in this case the delayed message is ignored. This dose not apply to the delayed new order which have valid
                // order id
                // peep message object==>JSONObject object = new JSONObject().put("order", currentOrder).put("items_to_send", itemsToSendArray);
                // object.put(PROTOCOL, ProtocolType.SEND_ORDER).put(ACK, id);
                Object orderObj = object.get("order");            
                Order peep = null;

                if(orderObj instanceof String){
                    peep = new Order(new JSONObject((String)orderObj));
                }else if(orderObj instanceof Order)
                    peep = (Order)orderObj;
                else if(orderObj instanceof JSONObject)
                    peep = new Order((JSONObject)orderObj);

                long orderLastUpdate = peep.getUpdateTime().getTimeInMilis();
                //String orderId = peep.getOrderId().toString();
                uniqueId = peep.getUniqueId();
                // update time in the list of the order (find by order id)
                container = BUS_LIST.get(session.getUserProperties().get(BUS_ID));
                if(container.getActiveOrders().get(uniqueId) != null){
                    long lastUpdateTime = container.getActiveOrders()
                            .get(uniqueId).getUpdateTime().getTimeInMilis();

                    System.out.println("sender order's lastUpdateTime=" + orderLastUpdate + ", time in endpoint is:" + lastUpdateTime );

                    // new created order may be delayed for update the time, by default it's 0.
                    if(orderLastUpdate != 0 && orderLastUpdate < lastUpdateTime){
                        System.out.println("this message is out of date, it's dropped");
                        System.out.println("sender order's lastUpdateTime=" + orderLastUpdate + ", time in endpoint is:" + lastUpdateTime );
                        return;
                    }
                }// else it's new order, just forward

                // this endpoint won't process the "SEND_ORDER" message, it just forward the message to POS Server.
                // SEND TO POS server endpoint only
                // find the POS server, if POS server is not up, que the message
                // PosMessageContainer container = BUS_LIST.get(session.getUserProperties().get(BUS_ID));
                serverSession = container.getServerSession();
                if(serverSession != null && serverSession.isOpen())
                    serverSession.getBasicRemote().sendObject(object);

                // for tesing
                //session.getBasicRemote().sendObject(object);      
                break;
            case SYNC_ORDERS:                
                System.out.println("prepare to syn(push orders to caller-client)");
                // pull out all orders and send to client pos
                container = BUS_LIST.get(session.getUserProperties().get(BUS_ID));                
                syncOrders(container.getActiveOrders(), session);  
                break;
            case LOCK_ORDER:

                 // when one pos send lock and is disconnected after lock the order, this order should be unclocked
                 // for other to use, but need to give warning
                 // check the connection status for lock pos

                // processing the request for locking order, if the order is locked, the request is rejected
                // with Locker's user info-lockerInfo
                JSONObject response = new JSONObject().put(PROTOCOL, ProtocolType.LOCK_ORDER);
                container = BUS_LIST.get(session.getUserProperties().get(BUS_ID));
                //String orderId = new OrderID(object.optJSONObject("order_id")).toString();
                uniqueId = object.optString(UNIQUE_ID);
                System.out.println("request lock order unique_id is:" + uniqueId);
                
                // check if the order is locked
                if(container.isLocked(uniqueId)){
                    // check if the session holding locker is closed
                    PosMessageContainer.Lock lock = container.getOrderLocks().get(uniqueId); // lock is not null here
                    Session sn = lock.getLockSession();
                    if(sn != null && sn.isOpen()){
                        // contruct ack to reject request, check if the locker session is still open
                        JSONObject lockerInfo = lock.getLockerInfo();
                        response.put("lock_response", "rejected").put("profile", lockerInfo);
                    }else{
                        // session lock the order may be closed anytime and won't available forever,
                        // and no one can unlock the order, remove locker
                        container.unLock(uniqueId);
                        // create new lock and ack for grant
                        container.createLock(uniqueId, object.optJSONObject("profile"), session);
                        response.put("lock_response", "granted");
                    }
                }else{
                    // create lock and ack for lock granted
                    container.createLock(uniqueId, object.optJSONObject("profile"), session);
                    response.put("lock_response", "granted");
                }
                System.out.println("server order lock response is: " + response);      
                session.getBasicRemote().sendObject(response);
                break;
            case UNLOCK_ORDER:                     
                container = BUS_LIST.get(session.getUserProperties().get(BUS_ID));
                uniqueId = object.optString(UNIQUE_ID);
                container.unLock(uniqueId);
                System.out.println("order unlocked for id " + uniqueId);
                break;
            case REQUEST_ORDER_ID:               // depricated 
                // get server session and forward request
                container = BUS_LIST.get(session.getUserProperties().get(BUS_ID));
                serverSession = container.getServerSession();
// server session may have been closed
                //????????? what to do
                System.out.println("creating binding ...session user id must exist==>" + session.getUserProperties().get(USER_ID));
                container.createBind(session, serverSession);

                object.put(USER_ID, (String)session.getUserProperties().get(USER_ID));
                serverSession.getBasicRemote().sendObject(object);
                break;
            case RESPONSE_ORDER_ID:                
                // response from server session
                container = BUS_LIST.get(session.getUserProperties().get(BUS_ID));
                // response object must contain USER_ID key
                userId = object.optString(USER_ID);
                // get the request session for orderId request
                PosMessageContainer.Bind bind = container.getServerBind(userId);
                if(bind != null){
                    Session clientSession = bind.getRequestSession();
                    clientSession.getBasicRemote().sendObject(object);
                    bind.releaseBinding();
                }else{
                    System.out.println("response to client bind lost, can do nothing");
                    return;
                } 

                System.out.println("releasing binging");
                break;
            case SYNCH_MENU:
                // 1. When new POS start, it need to check if the menu has already changed from somewhere,
                // So the POS send SYNC_MENU protocol to this endpoint with its Menu's timeStamp.
                // By checking the lastUpdateTime of menu in POS message and in this Endpoint, if POS menu outdated, 
                // POS will send DOWNLOAD_MENU protocol with menu and lastUpdateTime in this endpoint to POS.
                // If POS find the menu on the Endpoint is outdated, the POS must initial an UPDATE_MENU 
                // protocol to update the menu of the Endpoint. The Endpoint shall always be passive.
                
                // get timestamp of POS menu
                long posTimeStamp = object.optLong("last_update_time");
                if(posTimeStamp == 0){ // it is POS's responsibility to handle this case
                    System.out.println("There is no time stamp provided for synchronize menu");
                    return;
                }
                container = BUS_LIST.get(session.getUserProperties().get(BUS_ID));
                System.out.println("synch_menu protocol, total session is: " + container.getActiveSessions().size());
                Time lastUpdate = container.getLastMenuUpdateTime();
    
                System.out.println("pos menu update time==>" + posTimeStamp + ", Server Endpoint update time==>" + lastUpdate.getTimeInMilis());
                // For Sync_menu, just send timeStamp of this endpoint, the pos will decided what to send
                // for the nect protocol
                JSONObject synObject = new JSONObject().put(PROTOCOL, ProtocolType.SYNCH_MENU)
                        .put("last_update_time", lastUpdate.getTimeInMilis());
                    
                session.getBasicRemote().sendObject(synObject);                
                System.out.println("message sent==>" + synObject.toString());  
    /*
                if(posTimeStamp >= lastUpdate.getTimeInMilis()){
                    System.out.println("requesting pos for upload menu");
                    // pos time stamp is newer
                    // if no menu on the Endpoint, lastUpdate time is zero for longValue()
                    // pos need to upload menu to Endpoint
                    // call for update
                    JSONObject synObject = new JSONObject().put(PROTOCOL, ProtocolType.SYNCH_MENU)
                        .put("last_update_time", lastUpdate.getTimeInMilis());
                    
                    session.getBasicRemote().sendObject(synObject);                
                    System.out.println("message sent==>" + synObject.toString());   
                }else {
                    // pos download menu from Endpoint
                    JSONObject downObject = new JSONObject().put(PROTOCOL, ProtocolType.DOWNLOAD_MENU)
                        .put("last_update_time", lastUpdate.getTimeInMilis())
                        .put("menu", container.getMenu().toJSONObject()); // must converted to JSONObject to be sent
                    session.getBasicRemote().sendObject(downObject);
                    
                    System.out.println("Send download menu msg==> " + downObject);            
                    System.out.println("menu downloaded from this Endpoint");
                }*/
                break;
            case UPLOAD_MENU:
                // add or replace the menu in container, put time stamp for lastUpdateTime of menu
                // notify all registered POS sessesio. this protocol must be called from POS. this EndPoint
                // cannot initial this protocol
                
                if(object.get("menu") == null || object.get("last_update_time") == null){
                    System.out.println("upload protocol format error");
                    return;
                }            
                Object menuObject = object.get("menu");
                // checking menu Object type, menu is Categories
                Categories cats = null;
                if(menuObject instanceof String){
                     System.out.println("menu object is String");
                    // this is most the case, because of encoding and decoding process
                    cats = new Categories(new JSONObject((String)menuObject));                   
                }else if(menuObject instanceof JSONObject){  
                    System.out.println("menu object is JSONObject, before create Cats==>" + ((JSONObject)menuObject) );
                    cats = new Categories((JSONObject)menuObject); 
                    System.out.println("menu object is JSONObject, created cats is(string)==>" + cats.toJSONObject());
                }else if(menuObject instanceof Categories){
                    System.out.println("menu object is Categories");
                    cats = (Categories)menuObject;
                    
                }
                long posTime = object.optLong("last_update_time");   
                container = BUS_LIST.get(session.getUserProperties().get(BUS_ID));
                //container.setMenu(menuJSONObject);
                container.setMenu(cats);
                container.setLastMenuUpdateTime(new Time(posTime));  

                // checking what we get here
                System.out.println("------------------received categorys-----------------");
                int index = 0;
                for(Category cat:cats.getCategorys()){
                    System.out.println("category-" + index + "===>" + cat.toJSONObject());
                    index++;
                }
                System.out.println("---------------------end---------------------------");
                /*
                // notify all active session to download new menu
                JSONObject synDnObject = new JSONObject().put(PROTOCOL, ProtocolType.DOWNLOAD_MENU)
                        .put("last_update_time", posTime)
                        .put("menu", cats.toJSONObject());
         System.out.println("created menu object is: ==>" + synDnObject);
                // send new order to other hosts
                for(Session se:container.getActiveSessions()){
                    if(se != session){
                        System.out.println("\n\nsending download menu other registered pos endpoint....");
                        se.getBasicRemote().sendObject(synDnObject);
                        System.out.println("......message sent");
                    }
                }  */                            
                break;
            case DOWNLOAD_MENU:
                System.out.println("pos request menu from this server endpoint");
                // the request for downloading menu from the Server EndPoint
                container = BUS_LIST.get(session.getUserProperties().get(BUS_ID));
                JSONObject downObject = new JSONObject().put(PROTOCOL, ProtocolType.DOWNLOAD_MENU);
                
                for(Category cat:container.getMenu().getCategorys()){
                   // System.out.println("Sending: " + cat.toJSONObject().toString(1));
                    downObject.put("menu", cat.toJSONObject()).put("last_update_time", container.getLastMenuUpdateTime().getTimeInMilis());
        
                 session.getBasicRemote().sendObject(downObject);
        }
                break;
            default:
                    processOther(object, session);
        
        }
    }
        
    private void processOther(JSONObject object, Session session) throws IOException, EncodeException{
        ProtocolType type = ProtocolType.valueOfName(object.optString(PROTOCOL)); 
        long updateTime = System.currentTimeMillis();
        // process order
        Order order = null;
        Object orderObject = object.get("order");
        if(orderObject == null)  // no order available
            return;

        if(orderObject instanceof JSONObject){
            order = new Order(object.optJSONObject("order"));
            System.out.println("received order in JSONObject format");
        }else if(orderObject instanceof String){
            order = new Order(new JSONObject(object.optString("order")));

            System.out.println("checking order's update time==>" + order.getUpdateTime().getTimeInMilis());
            System.out.println("checking order id(json)==>" + order.getOrderId().toJSONObject());
        }
          
        switch(type){
            case ADD_ORDER:
                // adding new order, make sure two order in sending pos is the same as in server
                System.out.println("add new order");
                JSONObject uObject = new JSONObject().put(UNIQUE_ID, order.getUniqueId())
                        .put("update_time", updateTime+"").put(PROTOCOL, ProtocolType.UPDATE_TIME);                                
                session.getBasicRemote().sendObject(uObject);
                System.out.println("add_order, update time sent==>" + updateTime);
                order.setUpdateTime(new Time(updateTime));
                addOrder(order, session);
                break;
            case UPDATE_ORDER:
                System.out.println("update order");
                // find existing order on the endpoint
                String busId = session.getUserProperties().get(BUS_ID).toString();
                // order in the end point
                Order epOrder = BUS_LIST.get(busId).getActiveOrders()
                        .get(order.getUniqueId());
                System.out.println("updating order unique_id=" + order.getUniqueId());
                // end point time
                long lastUpdateTime = epOrder.getUpdateTime().getTimeInMilis();
                // find from received order
                long uTime = order.getUpdateTime().getTimeInMilis();
                
                System.out.println("update order time==>" + uTime + " in endpoint time==>" + lastUpdateTime);
                // must be equal before reset updateTime
                if(lastUpdateTime != uTime){
                    // this is outdated message, will be ignored
                    // for example, a pos sending update message, but is delayed due to failed connection, but other pos
                    // sent update for this order, updateTime is newer. this case is very rare but have to be handled
                    // send ack without reset order's updateTime
                    System.out.println("UPDATE ORDER MESSAGE IGNORED!");
                   
                    // also update the order in the client with the order on this end point
                    JSONObject toObject = new JSONObject().put(PROTOCOL, ProtocolType.UPDATE_ORDER).put("order", epOrder);
                    session.getBasicRemote().sendObject(toObject);
                    System.out.println("update_order, order is sent to replace older one");
                    return;
                }
                // else same time, then update to new time
                JSONObject upObject = new JSONObject().put(UNIQUE_ID, order.getUniqueId())
                        .put("update_time", updateTime+"").put(PROTOCOL, ProtocolType.UPDATE_TIME);                                
                session.getBasicRemote().sendObject(upObject);
                System.out.println("update_order, update time sent==>" + updateTime);
                
                // set current time as new update time and replace order in this endpoint
                order.setUpdateTime(new Time(updateTime));    
                BUS_LIST.get(busId).getActiveOrders().put(order.getUniqueId(), order);
                // send update to other pos
                updateOrder(order, session);
                break;
            case DELETE_ORDER:
                System.out.println("delete order");
                removeOrder(order, session);  
                break;
            case LOCK_TABLE:
                System.out.println("lock table");
                break;
           default:
                break;
        }
    }
    
    private void addOrder(Order order, Session session) throws IOException, EncodeException{
        String busid = session.getUserProperties().get(BUS_ID).toString();

        PosMessageContainer container = BUS_LIST.get(busid);
        container.getActiveOrders().put(order.getUniqueId(), order);
        System.out.println("order unique_id=" + order.getUniqueId() + " added to endpoint");      
        JSONObject object = new JSONObject().put(PROTOCOL, ProtocolType.ADD_ORDER)
                .put("order", order);
        
        // send new order to other hosts
        for(Session se:container.getActiveSessions()){
            if(se != session){
                System.out.println("sending update--add order ot other registered pos endpoint....");
                se.getBasicRemote().sendObject(object);
            }
        }
        
        System.out.println("order added, total orders==>" + container.getActiveOrders().size());
    }

  
    private void updateOrder(Order order, Session session) throws IOException, EncodeException{        
        String busId = session.getUserProperties().get(BUS_ID).toString();

        JSONObject object = new JSONObject().put(PROTOCOL, ProtocolType.UPDATE_ORDER)
                .put("order", order);
        
        // send updated order to other hosts
        for(Session se:BUS_LIST.get(busId).getActiveSessions()){
            if(se != session){ 
                se.getBasicRemote().sendObject(object);
            }
        }  
        System.out.println("order UPDATED, total orders==>" + BUS_LIST.get(busId).getActiveOrders().size());
    }
    
    private void removeOrder(Order order, Session session) throws IOException, EncodeException{
        String busId = session.getUserProperties().get(BUS_ID).toString();
        BUS_LIST.get(busId).getActiveOrders().remove(order.getUniqueId());

        JSONObject object = new JSONObject().put(PROTOCOL, ProtocolType.DELETE_ORDER)
                .put("order", order);
        
        // send updated order to other hosts
        for(Session se:BUS_LIST.get(busId).getActiveSessions()){
            if(se != session){
                se.getBasicRemote().sendObject(object);
            }
        } 
    }
    
    private void syncOrders(ObservableMap<String, Order> activeOrders, Session session) 
            throws IOException, EncodeException {
        System.out.println("Synchonizing.... total orders=" + activeOrders.size());   
        Object[] keys = activeOrders.keySet().toArray();
        JSONArray orders = null;
        JSONObject message = null;
        int totalOrders = keys.length;
        int index = 0, max_orders = 10;
        int loops = totalOrders/max_orders + (totalOrders % max_orders == 0?0:1); // mode!=0 need extra loop
        
        System.out.println("loop =" + loops);
        for(int i=0; i<loops; i++){
            orders = new JSONArray();
            for(int j=0; j<max_orders; j++){ // must put as JSONObject into JSONArray
                orders.put(activeOrders.get(keys[index].toString()).toJSONObject());
                index++;
                if(index >= totalOrders)
                    break;
            }
            System.out.println("order size:" + orders.length() + " sending now");   
            // send batch orders
            message = new JSONObject().put(PROTOCOL, ProtocolType.SYNC_ORDERS).put("orders_list", orders);
            session.getBasicRemote().sendObject(message);   
            
            System.out.println("message=====>" + message);
        }
       System.out.println("add order sent, total orders are==>" + keys.length);    
    }

}