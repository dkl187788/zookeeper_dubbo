package com.dukl.learn.entity;

import lombok.Data;

/**
 * Created by adu on 2019/6/22.
 */
@Data
public class ProductOrder {
    private String platformUserId;
    private String productId;
    private String receiveAddressId;
    private int count;
    private long orderTotalMoney;
    private String orderId;
    private long price;
    private Integer orderStatus;    
}
