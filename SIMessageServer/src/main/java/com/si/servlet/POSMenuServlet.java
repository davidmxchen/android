/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.servlet;

import com.si.message.PosMessageContainer;
import com.si.message.PosMessageServerEndPoint;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javafx.collections.ObservableMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Mingxing Chen
 */
@WebServlet(name = "POSMenuServlet", urlPatterns = {"/POSMenuServlet"})
public class POSMenuServlet extends HttpServlet {
    private PosMessageContainer posContainer =  PosMessageServerEndPoint.BUS_LIST.get("abcde_12345");
    private ObservableMap<String, PosMessageContainer> businessMap = PosMessageServerEndPoint.BUS_LIST;
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
        response.setContentType("application/json"); //("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        Enumeration<String> en = request.getParameterNames();
        while(en.hasMoreElements()){
            System.out.println("parameter-name: " + en.nextElement());
        }
        
        String id = request.getParameter("business_id");
        if(id == null){
            response.getWriter().println("{\"id\":\"null\"}");
            return;
        }
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
//            out.println("<!DOCTYPE html>");
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet POSMenuServlet</title>");            
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<h1>Servlet POSMenuServlet at " + request.getContextPath() + "</h1>");
            PosMessageContainer container = businessMap.get(id);
            if(container != null && container.getMenu() != null){
                out.println(container.getMenu().toJSONObject());
            }else{
                out.println("\"error\":\"POS menu is not available yet, refresh later\"");
            }
//            out.println("</body>");
//            out.println("</html>");
        }
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
