package com.dukl.learn.controller;

import com.dukl.learn.entity.ProductOrder;
import com.dukl.learn.service.ProductOrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by adu on 2019/6/22.
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    private Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private ProductOrderService productOrderService;

    @RequestMapping("/createOrder")
    @ResponseBody
    public ProductOrder createProductOrder(String platformUserId,
                                           String productId,
                                           String receiveAddressId,
                                           Integer count) {
        logger.info("请求参数,platformUserId:{}",platformUserId);
        return productOrderService.createProductOrder(platformUserId, productId, receiveAddressId, count);
    }


}
