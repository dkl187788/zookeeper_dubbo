package com.dukl.learn.service;

import com.dukl.learn.entity.ProductOrder;

/**
 * Created by adu on 2019/6/22.
 */
public interface ProductOrderService {
    ProductOrder createProductOrder(String platformUserId, String productId, String receiveAddressId, int count);
}
