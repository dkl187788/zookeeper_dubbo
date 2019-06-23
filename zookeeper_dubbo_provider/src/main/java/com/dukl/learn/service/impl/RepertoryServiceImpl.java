package com.dukl.learn.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dukl.learn.service.RepertoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by adu on 2018/08/16.
 */
@Service(version = "1.0.0",interfaceClass = RepertoryService.class)
public class RepertoryServiceImpl implements RepertoryService {
    private Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public boolean reduceRepertory(String productId, int reduceCount) {
        logger.info("根据订产品ID递减库存");
        return false;
    }
}
