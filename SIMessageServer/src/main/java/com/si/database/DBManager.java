package com.si.database;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.sun.javafx.event.EventHandlerManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 * DBManager class is used for database inserting, updating and delete of
 * certain type of data by fire certain type of event to execute transaction.
 * Detail database transaction is provided through EventHandler. Register
 * EventHandler with EventType through DBManager use
 * addEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) method,
 * multiple handlers can be added to the DBManager. DBManager is created by
 * calling static method DBManager.create()
 *
 * @param <T>
 */
public class DBManager<T> implements EventTarget {

    /**
     * EventHandlerManager is a EventDispatcher
     */
    private final EventHandlerManager eventHandlerManager = new EventHandlerManager(this);
    /**
     * ObjectProperty for DBUpdateEvent Handler only. initialized when in use
     */
    private ObjectProperty<EventHandler<DBUpdateEvent>> onDBUpdate;

    private Connection connection;
    private static DBManager manager;
    private static DatabaseTables dbTables;

    /**
     * Default Constructor, just to initialize.
     */
    private DBManager() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/si_pos_db?useUnicode=yes&characterEncoding=UTF-8",
                            "root", "admin");
            System.out.println("success, connect is not null? " + connection != null);
            System.out.println("creating order table now");
            dbTables = new DatabaseTables();
            dbTables.setDatabaseConnection(connection);
            dbTables.createAllTables();
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (Exception e) {
            // handle the error
            System.out.println("error: " + e.getMessage());
        } finally {

        }
    }

    private DBManager(String dbName, String user, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/" + dbName + "?useUnicode=yes&characterEncoding=UTF-8",
                            user, password);
            System.out.println("success, connect is not null? " + connection != null);
            System.out.println("creating order table now");
            dbTables = new DatabaseTables();
            dbTables.setDatabaseConnection(connection);
            dbTables.createAllTables();
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (Exception e) {
            // handle the error
            System.out.println("error: " + e.getMessage());
        } finally {

        }
    }

    /**
     * Close Database connection pool before leave app
     */
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            // silently close connection
        }
    }

    /**
     * Call this method to get the only one instance of DBManager
     *
     * @return instance of DBManager
     */
    public static DBManager create() {
        if (manager == null) {
            manager = new DBManager();
        }
        return manager;
    }

    /**
     * Create Database with known parameters
     *
     * @param dbName
     * @param user
     * @param password
     * @return
     */
    public static DBManager create(String dbName, String user, String password) {
        if (manager == null) {
            manager = new DBManager(dbName, user, password);
        }
        return manager;
    }

    public Connection getDatabaseConnection() {
        return connection;
    }

    public static DatabaseTables getDbTables() {
        return dbTables;
    }
    
    /**
     * Fire an DBUpdateEvent to this DBManager EventTarget. There are two ways
     * to handle the DBUpdateEvent: 1. Register External EventHandler to the
     * DBUpdateEvent through addEventHandler method with
     * DBUpdateEvent.UPDATE__TYPE 2. Call setOnDBUpdate method with your
     * EventHandler<DBUpdateEvent>. Call this method to pass your dataSource to
     * your handler
     *
     * @param dataSource, the source you want to update to your DataBase.
     */
    public void fireDBUpdateEvent(T dataSource) {
        Event.fireEvent(this, new DBUpdateEvent(dataSource));
    }

    /**
     * Fire DBInsertEvent to this EventTarget. Add handler to addEventHandler to
     * register to the event first.
     *
     * @param dataSource
     */
    public void fireDBInsertEvent(T dataSource) {
        Event.fireEvent(this, new DBInsertEvent(dataSource));
    }

    /**
     * Fire DBDeleteEvent to this EventTarget. Add handler to addEventHandler to
     * register to the event first.
     *
     * @param dataSource
     */
    public void fireDBDeleteEvent(T dataSource) {
        Event.fireEvent(this, new DBDeleteEvent(dataSource));
    }

    /**
     * Fire PrintOrderEvent to this EventTarget. Add handler to addEventHandler
     * to register to the event first.
     *
     * @param dataSource
     */
    public void firePrintOrderEvent(T dataSource) {
        Event.fireEvent(this, new PrintOrderEvent(dataSource));
    }

    public void fireLockOrderEvent(T dataSource) {
        Event.fireEvent(this, new LockOrderEvent(dataSource));
    }

    public void fireUnLockOrderEvent(T dataSource) {
        Event.fireEvent(this, new UnLockOrderEvent(dataSource));
    }

    public void fireRequestEvent(T dataSource) {
        Event.fireEvent(this, new RequestEvent(dataSource));
    }

    /**
     * Registers an event handler to this EventTarget. The handler is called
     * when the DBManager receives an {@code Event} of the specified event type
     * during the bubbling phase of event delivery.
     *
     * @param <E> the specific event class of the handler
     * @param eventType the type of the events to receive by the handler
     * @param eventHandler the handler to register
     * @throws NullPointerException if the event type or handler is null
     */
    public <E extends Event> void addEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) {
        eventHandlerManager.addEventHandler(eventType, eventHandler);

        System.out.println("addEventHandler: new eventHandler added");
    }

    /**
     * Unregisters a previously registered event handler from this EventTarget.
     * One handler might have been registered for different event types, so the
     * caller needs to specify the particular event type from which to
     * unregister the handler.
     *
     * @param <E> the specific event class of the handler
     * @param eventType the event type from which to unregister
     * @param eventHandler the handler to unregister
     * @throws NullPointerException if the event type or handler is null
     */
    public <E extends Event> void removeEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) {
        eventHandlerManager.removeEventHandler(eventType, eventHandler);
    }

    /**
     * EventTarget Implementation. Add the EventDispatcher to the
     * EventDispatchChain.
     *
     * @param tailChain
     * @return
     */
    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tailChain) {
        return tailChain.prepend(eventHandlerManager);
    }

//    
//    public ObjectProperty<EventHandler<DBUpdateEvent>> onDBUpdateProperty(){
//        if(onDBUpdate == null){
//            onDBUpdate = new ObjectPropertyBase<EventHandler<DBUpdateEvent>>(){
//
//                @SuppressWarnings({ "rawtypes", "unchecked" })
//                @Override protected void invalidated() {
//                    eventHandlerManager.setEventHandler(DBUpdateEvent.UPDATE_TYPE, (EventHandler<DBUpdateEvent>)(Object)get());                    
//                }
//                @Override
//                public Object getBean() {
//                    return DBManager.this;
//                }
//
//                @Override
//                public String getName() {
//                    return "updating DB";
//                }
//                
//            };
//        }
//        return onDBUpdate;
//    }
//    
//    public final void setOnDBUpdate(EventHandler<DBUpdateEvent> value){
//        onDBUpdateProperty().set(value);        
//    }
}
