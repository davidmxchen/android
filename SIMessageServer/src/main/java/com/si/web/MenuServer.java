/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.si.web;


import java.io.*;
import java.net.URL;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
/**
 *
 * @author Mingxing Chen
 */

public class MenuServer extends HttpServlet {

    private JSONObject json;
    public void init() throws ServletException {
        try {
            InputStream input = getServletContext().getResourceAsStream("/json_files/MenuList.json");
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
    }
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
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
            out.println("<title>Servlet MenuServer</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MenuServer at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }

    protected void processJson(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();       
        out.println(json.toString());
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processJson(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processJson(request, response);
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
