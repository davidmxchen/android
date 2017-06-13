/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.web;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App2{
	
   public static void main(String[] args) throws IOException{
	
       String command = "ipconfig /all";
       Process p = Runtime.getRuntime().exec(command);
	    
       BufferedReader inn = new BufferedReader(new InputStreamReader(p.getInputStream()));
       Pattern pattern = Pattern.compile(".*Physical Addres.*: (.*)");
	    
       while (true) {
            String line = inn.readLine();

	    if (line == null)
	        break;

	    Matcher mm = pattern.matcher(line);
	    if (mm.matches()) {
	        System.out.println(mm.group(1));
	    }
	}	    
   }
}