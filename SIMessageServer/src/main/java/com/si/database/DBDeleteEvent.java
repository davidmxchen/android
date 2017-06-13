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
public class DBDeleteEvent <T> extends Event {
    public static final EventType<DBDeleteEvent> DELETE_TYPE = new EventType<>("DB_DELETE");
    private final T eventSource;
    
    public DBDeleteEvent(EventType<? extends Event> et, T source) {
        super(et);
        eventSource = source;
    }
    
    public DBDeleteEvent(T source){
        super(DELETE_TYPE);
        eventSource = source;
    }
    
    public T getSource(){
        return eventSource;
    }
}
