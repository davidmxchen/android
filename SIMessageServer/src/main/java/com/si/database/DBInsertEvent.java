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
public class DBInsertEvent<T> extends Event {
    public static final EventType<DBInsertEvent> INSERT_TYPE = new EventType<>("DB_INSERT");
    private final T eventSource;
    
    public DBInsertEvent(EventType<? extends Event> et, T source) {
        super(et);
        eventSource = source;
    }
    
    public DBInsertEvent(T source){
        super(INSERT_TYPE);
        eventSource = source;
    }
    
    public T getSource(){
        return eventSource;
    }
}
