package com.hp.daily.service;

import com.hp.daily.util.NameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class VisitService {

    @Autowired
    private RedisTemplate redisTemplate;

    public void drawVistPage(ModelMap map) {
        List<String> urls = new ArrayList<>();
        Set<Integer> nums = NameUtil.getNumberWithoutRepetition(NameUtil.getRandomNumber(6,12),140);
        for (Integer i : nums){
            urls.add("http://laybarbarian.cn/film/"+String.valueOf(i)+".jpg");
        }
        map.addAttribute("urls",urls);
    }

    public void test(){
        String key = "doorplate_1";
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
//        Long doorplate_1 = redisTemplate.getExpire("doorplate_1");
        // 缓存存在
        boolean hasKey = redisTemplate.hasKey(key);
        String access_token ;
        if (hasKey) {
            String s1 = operations.get(key);
        }else {
            access_token= "access_token";
            // 插入缓存
            operations.set(key, access_token, 6000, TimeUnit.MILLISECONDS);
        }
//        Long doorplate_2 = redisTemplate.getExpire("doorplate_1");
    }
}
