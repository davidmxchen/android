/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.resource;

/**
 *
 * @author Mingxing Chen
 * 
 */
public enum OrderStatus {
        NEW,
        SAVED,        
        SENT,
        CHECK_PRINTED,
        CHECK_PAID,
        CHECK_UNPAID,
        CARD_SWIPED,
        CARD_FINALIZED,
        CANCELLED,
        HOLDING,
        DELETED, 
        COMBINED,
        DELEAYED;
}
