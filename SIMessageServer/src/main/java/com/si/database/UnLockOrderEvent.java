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
public class UnLockOrderEvent<T> extends Event {
    public static final EventType<UnLockOrderEvent> UNLOCK_ORDER_TYPE = new EventType<>("UNLOCK_ORDER_EVENT");
    private final T eventSource;
    
    public UnLockOrderEvent(EventType<? extends Event> et, T source) {
        super(et);
        eventSource = source;
    }
    
    public UnLockOrderEvent(T source){
        super(UNLOCK_ORDER_TYPE);
        eventSource = source;
    }
    
    public T getSource(){
        return eventSource;
    }
}
