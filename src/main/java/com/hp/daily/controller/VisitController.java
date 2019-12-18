package com.hp.daily.controller;

import com.hp.daily.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
public class VisitController {

    @Resource
    public VisitService visitService;

    @RequestMapping("/test")
    public String test(){
        visitService.test();
        return "test";
    }

    @RequestMapping({"/","/visit"})
    public String tovisit(ModelMap map){
        visitService.drawVistPage(map);
        return "visit";
    }
}
