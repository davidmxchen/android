/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.message;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


/**
 *
 * @author Mingxing Chen
 */
@ServerEndpoint(value="/message_server_endpoint", encoders = {FigureEncoder.class}, decoders = {FigureDecoder.class})
public class MessageServerEndpoint {

    private static final Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    @OnMessage
    public void broadcastFigure(Figure figure, Session session) throws IOException, EncodeException {
        System.out.println("I have " + peers.size() + " clients now");
        for (Session peer : peers) {
            if (!peer.equals(session)) {
                peer.getBasicRemote().sendObject(figure);
                if(peer.getUserProperties().get("third_session") != null){
                    System.out.println("I have just reply to my best friend");
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("close session called");  
        peers.remove(session);
    }

    @OnOpen
    public void onOpen(Session session) {       
        System.out.println("onOpen");
        try{
            // handshaking to client, if exception happen, session is not added to peers
            if(session != null){
                peers.add(session);
                session.getBasicRemote().sendObject("hello:there, session created for you in server");
            }
            
        }catch(IOException e){
            System.out.println("error onOpen: " + e.getMessage());
        }catch(EncodeException ee){
            System.out.println("Error onOpen: " + ee.getMessage());
            
        }
        System.out.println("New client connection, new session created");
    }   
}
