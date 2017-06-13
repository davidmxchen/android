/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

import java.util.ArrayList;
import java.util.Locale;

/**
 *
 * @author Mingxing Chen
 */
public interface MenuData {
    
    public static Locale locale = new Locale("en");
    /**
     * Get category as String array
     * @return String[] of category
     */
    public String[] getCategoryNames();    
    
    /**
     * Get category as ArrayList<String>
     * @return ArrayList<String> of category    
     */
    public ArrayList<String> getCategoryNamesList();
    
    /**
     * Get a specific category for catName
     * @param catName
     * @return Category Object, null if not found
     */
    public Category getCategory(String catName);  
}
