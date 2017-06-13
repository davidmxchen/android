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
 * @param <T>
 */
public class DBUpdateEvent<T> extends Event {
    public static final EventType<DBUpdateEvent> UPDATE_TYPE = new EventType<>("DB_UPDATE");
    private final T eventSource;

    public DBUpdateEvent(EventType<? extends Event> et, T source) {
        super(et);
        eventSource = source;
    }

    public DBUpdateEvent(T source){
        super(UPDATE_TYPE);
        eventSource = source;
    }
    
    public T getSource(){
        return eventSource;
    }
}
