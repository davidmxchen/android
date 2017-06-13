/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Mingxing Chen
 */
public final class Time {
    private final Date date;
    private final DateFormat format;
    private final String dateAndTime;
    private final String dayOfMonth;
    private final String monthNumber;
    private final String monthName;
    private final String year;
    private final String time;
    private final String hour;
    private final String minute;
    private final String second;
    private final String amPm;
    private final String dayOfWeek;
    
    public Time(long time){
        this(new Date(time));
    }
    
    public Time(Date date){
        this.date = date;
        format = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-aa");
        String timeString = format.format(date);
        
        int index = timeString.indexOf("-");
        year = timeString.substring(0, index);
        
        timeString = timeString.substring(index+1);
        index = timeString.indexOf("-");
        monthNumber = timeString.substring(0, index);
        
        timeString = timeString.substring(index+1);
        index = timeString.indexOf("-");
        dayOfMonth = timeString.substring(0, index);
        
        timeString = timeString.substring(index+1);
        index = timeString.indexOf("-");
        hour = timeString.substring(0, index);
        
        timeString = timeString.substring(index+1);
        index = timeString.indexOf("-");
        minute = timeString.substring(0, index);
        
        timeString = timeString.substring(index+1);
        index = timeString.indexOf("-");
        second = timeString.substring(0, index);
        
        amPm = timeString.substring(index+1);
        
        time = hour + ":" + minute + amPm;
        dateAndTime = new SimpleDateFormat("MM/dd/yyyy 'Time:'hh:mmaa").format(date);
                
        monthName = new SimpleDateFormat("MMM").format(date);
        dayOfWeek = new SimpleDateFormat("EEE").format(date);       
        
   }

    /**
     * Get date as format 05/14/2012 Time:09:05AM
     * @return Format as 05/14/2012 Time:09:05AM
     */
    public String getDateAndTime() {
        return dateAndTime;
    }

    /**
     * The day of a month in two digits eg. 02
     * @return  two digits for the day of month
     */
    public String getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * Day of month in number two digits eg.09
     * @return 2 digits of month
     */
    public String getMonthNumber() {
        return monthNumber;
    }
    
    /**
     * Day of month in three letter eg. Jul
     * @return first three letter of a month
     */
    public String getMonthName() {
        return monthName;
    }

    /**
     * Four digits of the year eg.2012
     * @return  four digits of a year
     */
    public String getYear() {
        return year;
    }
    /**
     * Time of the day in format eg. 09:23AM
     * @return hh:mmAM
     */
    public String getTime() {
        return time;
    }

    /**
     * Hour of 12 hours, 2 numbers from 01 to 12, no 00
     * @return 2 numbers from 01 to 12
     */
    public String getHour() {
        return hour;
    }

    /**
     * Two digits number of minute in hour
     * @return two numbers from 00 to 59
     */
    public String getMinute() {
        return minute;
    }
    
    /**
     * Two digits number of second in minute
     * @return two numbers from 00 to 59
     */
    public String getSecond() {
        return second;
    }

    /**
     * Am or PM of the day
     * @return AM/PM
     */
    public String getAmPm() {
        return amPm;
    }

    /**
     * Day of the week in three letters, eg. Mon, Sat
     * @return three letter of day of the week
     */
    public String getDayOfWeek() {
        return dayOfWeek;
    }   
    
    public Date getDate(){
        return date;
    }
    
    public long getTimeInMilis(){
        return this.date.getTime();
    }
    
    public boolean equal(Time another){
        return this.date.equals(another.getDate());
    }
}
