package com.hp.daily.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

//@Configuration
public class LoginConfig implements WebMvcConfigurer {

//    @Resource
    private HpLoginInterceptor hpLoginInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        List<String> excludes = new ArrayList<>();
//        excludes.add("/hp/login");
//        excludes.add("/hp/index");


        registry.addInterceptor(hpLoginInterceptor)
                .addPathPatterns("/hp/add","/hp","/hp/home","/hp/");
//                .excludePathPatterns(excludes);
    }
}
