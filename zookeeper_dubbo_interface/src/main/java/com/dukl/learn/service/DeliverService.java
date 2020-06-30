package com.dukl.learn.service;

/**
 * Created by adu on 2018/08/16.
 */
public interface DeliverService {
    boolean deliverUserSaleProduct(String orderId,String receiveAddressId);
    
    boolean queryDeliverStatus(String orderId);
}
