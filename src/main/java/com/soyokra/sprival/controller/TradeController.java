package com.soyokra.sprival.controller;


import com.soyokra.sprival.dao.master.model.Trade;
import com.soyokra.sprival.dao.master.provider.TradeProvider;
import com.soyokra.sprival.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RequestMapping(value = "/trade")
@RestController
public class TradeController {

    @Resource
    TradeProvider tradeProvider;

    @GetMapping(value = "/query")
    @ResponseBody
    public ResponseUtils<?> query(){
        List<Trade> trades = tradeProvider.getTrades("1");


        return ResponseUtils.success(trades);
    }
}
