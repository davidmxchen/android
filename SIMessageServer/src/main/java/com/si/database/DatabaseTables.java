/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.si.database;

import com.si.data.Order;
import com.si.data.TableOrderItem;
import com.si.resource.OrderType;
import com.si.resource.OrderType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author Mingxing Chen
 */
public class DatabaseTables {

    private Connection databaseConnection;
    private String orderTable = "CREATE TABLE ORDERS ("
            + "BUSINESS_ID VARCHAR(20) NOT NULL, "
            + "UNIQUE_ID VARCHAR(20) NOT NULL, "
            + "ORDER_ID VARCHAR(10), "
            + "ORDER_TIME BIGINT(15), " // "YYYY-MM-DD HH:MM:SS"
            + "UPDATE_TIME BIGINT(15) DEFAULT 0, "// USE "YYYY=MM-DD HH:MM:SS[FRACTION]" IN VERSION 5.6 UP INSTEAD
            + "ORDER_LIST TEXT CHARACTER SET utf8 COLLATE utf8_general_ci, "
            + "ORDER_TYPE ENUM('DINE_IN', 'DELIVERY', 'PICK_UP', 'WALK_IN', 'ONLINE_PICK_UP', 'ONLINE_DELIVERY'), "
            + "ORDER_STATUS VARCHAR(20), "
            //+ "CUSTOMER_ID INT, "
            + "TOTAL_AMOUNT DECIMAL(10, 2), "
            + " CONSTRAINT PK_ORDERS PRIMARY KEY (BUSINESS_ID, UNIQUE_ID)"
            + ") CHARACTER SET utf8 COLLATE utf8_general_ci";
    private String customerTable = "CREATE TABLE CUSTOMER_INFO ("
            + "CUSTOMER_ID INT NOT NULL AUTO_INCREMENT, "
            + "PHONE VARCHAR(20) NOT NULL unique, "
            + "EXTENSION VARCHAR(10), "
            + "EMAIL VARCHAR(30), "
            + "NAME VARCHAR(20), "
            + "ADDRESS TEXT CHARACTER SET utf8 COLLATE utf8_general_ci, "
            + "PRIMARY KEY (CUSTOMER_ID)"
            + ")CHARACTER SET utf8 COLLATE utf8_general_ci";
    private String cardTable = "CREATE TABLE CREDIT_CARD("
            + "NAME VARCHAR(20), "
            + "CARD_NUMBER VARCHAR(20), "
            + "EXPIRATION_DATE VARCHAR(10), "
            + "PHONE VARCHAR(20), "
            + "STREET VARCHAR(50), "
            + "CITY VARCHAR(25), "
            + "STATE VARCHAR(2), "
            + "ZIPCODE VARCHAR(10),"
            + "CUSTOMER_ID INT"
            + ")";

    private String businessTable = "CREATE TABLE BUSINESS_INFO("
            + "BUSINESS_ID VARCHAR(20) NOT NULL UNIQUE, "
            + "CREDENTIAL TEXT CHARACTER SET utf8 COLLATE utf8_general_ci, "
            + "MENU TEXT CHARACTER SET utf8 COLLATE utf8_general_ci, "
            + "LAST_UPDATE BIGINT(20), "
            + "PRIMARY KEY (BUSINESS_ID)"
            + ")CHARACTER SET utf8 COLLATE utf8_general_ci";
    
    public DatabaseTables() {

    }

    public void createAllTables() throws SQLException {
        Statement stmt = null;
        try {
            stmt = databaseConnection.createStatement();
            if(!isTableCreated("orders", stmt))
                stmt.executeUpdate(orderTable);
            if(!isTableCreated("customer_info", stmt))
                stmt.executeUpdate(customerTable);
            if(!isTableCreated("credit_card", stmt))
                stmt.executeUpdate(cardTable);
            if(!isTableCreated("business_info", stmt))
                stmt.executeUpdate(businessTable);
        } catch (SQLException exception) {
            JDBCUtilities.printSQLException(exception);
            throw exception;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public Connection getDatabaseConnection() {
        return databaseConnection;
    }

    public void setDatabaseConnection(Connection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void addOrder(Order order, String businessID){
//        StringBuffer orderStr = new StringBuffer();
//        for(TableOrderItem item:order.getOrderItems()){
//            orderStr.append(item.toJSONObject().toString());
//        }
        String sql = "insert into orders ("
                + "BUSINESS_ID, " //VARCHAR(20)
                + "UNIQUE_ID, " // VARCHAR(20)
                + "ORDER_ID, " // VARCHAR(10)
                + "ORDER_TIME, " // BIGINT(15)
                + "UPDATE_TIME, " // BIGINT(15)
                + "ORDER_LIST, " // TEXT
                + "ORDER_TYPE, " // ENUM
                + "ORDER_STATUS, " // VARCHAR(20)
              //  + "CUSTOMER_ID, "
                + "TOTAL_AMOUNT" // BIGDECIMAL
                + ") "
                + "values("    
                + "'" + businessID + "', "
                + "'" + order.getUniqueId() + "', "
                + "'" + order.getOrderId().toString() + "', "
                + order.getOrderTime().getTimeInMilis() + ", "
                + order.getUpdateTime().getTimeInMilis() + ", "
                + "'" + order.toJSONObject().toString() + "', "
                + "'" + OrderType.getName(order.getOrderType()) + "', "
                + "'" + order.getOrderStatus().toString() + "', "
                
                + order.getTotal().toString()
                + ")";
        // + "'" + orderList + "', '" + type + "', " + total + ")";
        System.out.println("inserting order to dbs, stmt:");
        executeSQL(sql);
    }

    public void updateOrder(Order order, String businessID){
        String sql = "update orders set "
                + "UPDATE_TIME = " + order.getUpdateTime().getTimeInMilis() + ", "
                + "ORDER_LIST = '" + order.toJSONObject().toString() + "', "
                + "ORDER_TYPE = '" + OrderType.getName(order.getOrderType()) + "', "
                + "ORDER_STATUS = '" + order.getOrderStatus().toString() + "', "
                + "TOTAL_AMOUNT = " + order.getTotal().toString() + " "
                + "where UNIQUE_ID = '" + order.getUniqueId()+ "' AND "
                + "BUSINESS_ID = '" + businessID + "'";
               // + "AND ORDER_ID = " + order.getOrderId().toString();

        System.out.println("updating order to dbs, sql:" + sql);
        executeSQL(sql);
    }

    public void deleteOrder(Order order, String businessID) {
        String sql = "delete from orders where UNIQUE_ID=" 
                + order.getUniqueId() + " AND "
                + "BUSINESS_ID = '" + businessID + "'";
        System.out.println("deleting order, sql=" + sql);
        executeSQL(sql);
    }
    
    public void addBusinessInfo(String busId, String credential, String menu, String time){
        
        String sql ="INSERT INTO BUSINESS_INFO("
                + "BUSINESS_ID, "// VARCHAR(20) NOT NULL UNIQUE, "
                + "CREDENTIAL, " // TEXT CHARACTER SET utf8 COLLATE utf8_general_ci, "
                + "MENU, " // TEXT CHARACTER SET utf8 COLLATE utf8_general_ci, "
                + "LAST_UPDATE) VALUES("
                + "'" + busId + "', "
                + "'" + credential + "',"
                + "'" + menu + "', "
                + time
                + ")";
        System.out.println("add new business info, sql:" + sql);
        executeSQL(sql);                
    }
    
    public void updateBusinessInfo(String busId, Hashtable<String, String> table){
        StringBuffer sql = new StringBuffer("UPDATE BUSINESS_INFO SET ");
        Enumeration<String> enumeration = table.keys();
        while(enumeration.hasMoreElements()){
            String key = enumeration.nextElement();
            sql.append( key + " = '" + table.get(key) + "', ");
        }
        sql.replace(sql.length()-2, sql.length()-1, " "); // remove last ","
        sql.append( "WHERE BUSINESS_ID='" + busId + "'");
        System.out.println("update business info, sql:" + sql);
        
        executeSQL(sql.toString());
    }
        
    public void deleteBusinessInfo(String busId){
        String sql = "DELETE FROM BUSINESS_INFO WHERE BUSINESS_ID = '" + busId + "'";
        System.out.println("DELETE BUSINESS RECORD, sql:" + sql);
        
        executeSQL(sql);
    }
    
    private void executeSQL(String sql){
        System.out.println(sql);
        Statement stmt = null;
        try {
            stmt = databaseConnection.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException exception) {
            JDBCUtilities.printSQLException(exception);
        } finally {
        }
    }
    
    private boolean isTableCreated(String tbName, Statement stmt) throws SQLException{
        String sql = "show tables";
        ResultSet resultSet = null;
        resultSet = stmt.executeQuery(sql);
        while (resultSet.next()) {
            String rsStr = resultSet.getString(1);
            if(rsStr.equalsIgnoreCase(tbName)){
                System.out.println("Table " + tbName + " already existed");
                return true;
            }            
        }
        System.out.println("Table " + tbName + " not existed");
        return false;        
    }

}
