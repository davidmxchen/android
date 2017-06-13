/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.database;

import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * @author Mingxing Chen
 */
public class RequestEvent <T> extends Event {
    public static final EventType<RequestEvent> REQUEST_TYPE = new EventType<>("REQUEST_TYPE");
    private final T eventSource;
    
    public RequestEvent(EventType<? extends Event> et, T source) {
        super(et);
        eventSource = source;
    }
    
    public RequestEvent(T source){
        super(REQUEST_TYPE);
        eventSource = source;
    }
    
    public T getSource(){
        return eventSource;
    }
}
