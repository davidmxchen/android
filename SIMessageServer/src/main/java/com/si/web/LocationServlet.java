/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mingxing Chen
 */
public class LocationServlet extends HttpServlet {

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
        //response.setContentType("text/html;charset=UTF-8");
        /**
        try (PrintWriter out = response.getWriter()) {
            // TODO output your page here. You may use following sample code. 
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LocationServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LocationServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
        */
        // get user geolocation, use this location as map center and find around area's business from database
        // and send back all businesses infos
        String latitude = request.getParameter("latutude");
        String longitude = request.getParameter("longitude");
        
        JSONObject userInfo = new JSONObject();
        if( request.getParameter("id") != null)
            userInfo.put("id",  request.getParameter("id"));
        
        
        // query business info around the position and sen to user
        // testing code
        JSONArray geosArray = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("latitude", 40.7510);
        object.put("longitude", -73.8210);
        geosArray.put(object);
        object = new JSONObject();
        object.put("latitude", 40.7506);
        object.put("longitude", -73.8204);
        geosArray.put(object);
        object = new JSONObject();
        object.put("latitude", 40.7516);
        object.put("longitude", -73.8217);
        geosArray.put(object);
        object = new JSONObject();
        object.put("latitude", 40.7516);
        object.put("longitude", -73.8237);
        geosArray.put(object);
        userInfo.put("geo_locations", geosArray);
        
        System.out.println("user info: " + userInfo.toString());
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(userInfo.toString());
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
        processRequest(request, response);
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
        processRequest(request, response);
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
