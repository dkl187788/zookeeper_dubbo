package com.dukl.learn.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dukl.learn.service.DeliverService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by adu on 2018/08/16.
 */
@Service(version = "1.0.0",interfaceClass = DeliverService.class)
public class DeliverServiceImpl implements DeliverService {
    private Logger logger = LogManager.getLogger(this.getClass());


    @Override
    public boolean deliverUserSaleProduct(String orderId, String receiveAddressId) {
        logger.info("根据订单号及收获人地址准备快递单发货");
        return false;
    }
}
