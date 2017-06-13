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
public class PrintOrderEvent<T> extends Event {
    
    public static final EventType<PrintOrderEvent> PRINT_TYPE = new EventType<>("PRINT_ORDER");
    private final T eventSource;

    public PrintOrderEvent(EventType<? extends Event> et, T source) {
        super(et);
        eventSource = source;
    }

    public PrintOrderEvent(T source){
        super(PRINT_TYPE);
        eventSource = source;
    }
    
    public T getSource(){
        return eventSource;
    }
}
