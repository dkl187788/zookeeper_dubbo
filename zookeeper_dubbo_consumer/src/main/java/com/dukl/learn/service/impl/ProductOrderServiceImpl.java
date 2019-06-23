package com.dukl.learn.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dukl.learn.entity.ProductOrder;
import com.dukl.learn.service.DeliverService;
import com.dukl.learn.service.ProductOrderService;
import com.dukl.learn.service.RepertoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by adu on 2019/6/22.
 */
@Service("productOrderService")
public class ProductOrderServiceImpl implements ProductOrderService {
    private Logger logger = LogManager.getLogger(this.getClass());

    @Reference(version = "1.0.0", lazy = true, interfaceClass = DeliverService.class)
    private DeliverService deliverService;

    @Reference(version = "1.0.0", lazy = true, interfaceClass = RepertoryService.class)
    private RepertoryService repertoryService;

    public ProductOrder createProductOrder(String platformUserId, String productId, String receiveAddressId, int count) {
        logger.info("接受到订单请求开始创建订单");
        ProductOrder productOrder = new ProductOrder();
        //首先创建一笔订单
        String orderId = UUID.randomUUID().toString();
        // 递减库存
        repertoryService.reduceRepertory(productId, count);
        // 通知发货
        deliverService.deliverUserSaleProduct(orderId, receiveAddressId);
        logger.info("成功创建订单");
        productOrder.setOrderId(orderId);
        productOrder.setOrderTotalMoney(150 * count);
        productOrder.setPlatformUserId(platformUserId);
        productOrder.setCount(count);
        productOrder.setProductId(productId);
        productOrder.setReceiveAddressId(receiveAddressId);
        productOrder.setPrice(150);
        return productOrder;
    }
}
