/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.si.servlet;

import com.si.data.Order;
import com.si.data.OrderID;
import com.si.message.PosMessageContainer;
import com.si.message.PosMessageServerEndPoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
@WebServlet(name = "OrderProcessServlet", urlPatterns = {"/OrderProcessServlet"})
public class OrderProcessServlet extends HttpServlet {

    // map of business name, online order list
    private HashMap<String, ArrayList<JSONObject>> table;
    private HashMap<String, ArrayList<Order>> onlineOrders;
    private String orderReceiverURL;
    private PosMessageContainer posContainer;
    private static int orderID = 0;

    public void init() throws ServletException {
        table = new HashMap<>();
        onlineOrders = new HashMap<>();
        orderReceiverURL = null;
        posContainer = PosMessageServerEndPoint.BUS_LIST.get("abcde_12345");
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /*
             * TODO output your page here. You may use following sample code.
             */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet OrderProcessServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet OrderProcessServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    protected void doRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // check parameter
        orderReceiverURL = request.getParameter("register");
        PrintWriter out = response.getWriter();
        if (orderReceiverURL != null) {
            out.print("OK");
        } else {
            out.print("no receiver URL provided");
        }

        System.out.println("Regisered receiver URL: " + orderReceiverURL);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getParameter("register") != null) {
            doRegister(request, response);
            return;
        }

        // processRequest(request, response);
        //  doPost(request, response);
        PrintWriter out = response.getWriter();
        String name = request.getParameter("name");
        if (name == null) {
            out.print("{no name exist}");
        }
        // get order numbers, single or multiple
        String number = request.getParameter("number");
        ArrayList<JSONObject> orderList = table.get(name);
        if (number != null && number.equals("single")) {
            out.print(orderList.get(orderList.size() - 1).toString());
        } else if (number != null && number.equalsIgnoreCase("multiple")) {
            for (JSONObject json : orderList) {
                out.print(json.toString());
                out.print("________________________________");
            }

        } else {
            out.print("{no order available}");
        }

        out.println("Remote Host: " + request.getRemoteHost());
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("json/application; charset=UTF-8");
        //processRequest(request, response);
        String context = getServletContext().getContextPath();
        System.out.println("servlet context path is: " + context + "ServletContext temp dir is " + ServletContext.TEMPDIR);
        
        System.out.print("the request from client is: id=" + request.getParameter("id"));
        String id = request.getParameter("id");
        if (id == null) {
            return;
        }
        JSONObject json = new JSONObject(id);
        String save = request.getParameter("save");
        String name = request.getParameter("name");
        System.out.println("parameters save=" + save + " name=" + name);
        if (save != null && save.equalsIgnoreCase("true")) {
            if (name != null && !name.isEmpty()) {
                // if business is not up yet
                ArrayList<JSONObject> list = table.get(name);
                if (list == null) {
                    list = new ArrayList<>();
                    table.put(name, list);
                }
                list.add(json);

                ArrayList<Order> orders = new ArrayList<>();
                Order order = new Order(json);
                order.setOrderId(new OrderID(orderID++));

                orders.add(order);
                System.out.println("testing order received, order total items:" + order.getTotalItem());
                System.out.println("testing order received, order total Amount:" + order.getTotal());
                System.out.println("assigned order id is(json): " + order.getOrderId().toJSONObject().toString());
                onlineOrders.put(name, orders);

                System.out.println(name + " has been add to table, total order now is: " + table.get(name).size());
            }
        }

        // already initialized, but double check because the list may change anytime
        posContainer = PosMessageServerEndPoint.BUS_LIST.get("abcde_12345");
        // checking if the business is open
        if (posContainer != null && posContainer.getActiveSessions().size() > 0) {
            for (Order order : onlineOrders.get(name)) {
                posContainer.addOnlineOrder(order); // this will trig order being send to POS
            }
        } else {// try again
            posContainer = PosMessageServerEndPoint.BUS_LIST.get("abcde_12345");
        }

        // preparing response
        PrintWriter out = response.getWriter();
        out.print(json.toString());

        /*
         // testing order
         try {
         InputStream input = getServletContext().getResourceAsStream("/json_files/test.json");
         BufferedReader br = new BufferedReader( new InputStreamReader(input, "utf-8" ) );//new InputStreamReader(input, "utf-8"));
           
         StringBuffer jsonStr = new StringBuffer();
         String line = null;
         while((line = br.readLine()) != null)
         jsonStr.append(line);
            
         json = new JSONObject(jsonStr.toString()); 
         } catch(Exception e) {            
         System.out.println(e.getMessage());
         throw new ServletException(e.getMessage());
         }     
        
        
         System.out.println("Remote Host: " + request.getRemoteHost());
        
         URL clientURL = new URL("http://localhost:8787/servlet/");
         InputStream input = clientURL.openStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(input));
         System.out.println("client server response:_____");
         String line = null;
         while((line = reader.readLine()) != null)
         System.out.println(line);
        
        
         if(orderReceiverURL != null){
         URL url = new URL(orderReceiverURL);
         URLConnection con = url.openConnection();
         con.setDoOutput(true);
         OutputStreamWriter output = new OutputStreamWriter(con.getOutputStream());
         // String id = URLEncoder.encode(request.getParameter("id"), "UTF-8");
         // output.write("id=" + id);
         output.write(json.toString()); //request.getParameter("id"));
         output.flush();
         output.close();
         System.out.println("new order sent to to " + orderReceiverURL + " with:\n" + json.toString());
            
         BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
         String decodedString;
         while ((decodedString = in.readLine()) != null) {
         System.out.println(decodedString);
         }
         in.close();            
         }
         */
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
