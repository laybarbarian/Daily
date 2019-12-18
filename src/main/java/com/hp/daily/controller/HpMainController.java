package com.hp.daily.controller;

import com.hp.daily.entity.HpUser;
import com.hp.daily.entity.ResponseResult;
import com.hp.daily.ex.LoginOverTimeException;
import com.hp.daily.service.HpMainService;
import com.hp.daily.util.BeanUtil;
import com.hp.daily.util.MyDateUtil;
import com.hp.daily.util.NameUtil;
import com.hp.daily.util.SFTPUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

@Controller
@RequestMapping("/hp")
public class HpMainController {

    protected final Log log = LogFactory.getLog(getClass());

    @Resource
    private HpMainService mainService;

    @RequestMapping({"/index","","/"})
    public String index(){return "hp/index";}

    @RequestMapping("/add")
    public String add(HttpSession session){
        HpUser hpUser =(HpUser)session.getAttribute("hpuser");
        if(hpUser!=null){
            return "hp/add";
        }
        return "hp/index";
    }

    @RequestMapping("/home")
    public String home(HttpSession session) {
//        mainService.getHomePage(datestr,map);
        HpUser hpUser =(HpUser)session.getAttribute("hpuser");
        if(hpUser!=null){
            return "hp/home";
        }
        return "hp/index";
    }

    @RequestMapping("/details")
    @ResponseBody
    public ResponseResult details(@RequestBody Map<String,String> reqMap) throws ParseException {
        return mainService.getDetails(reqMap);
    }

    @RequestMapping("/login")
    @ResponseBody
    public ResponseResult login(@RequestBody Map<String,String> reqMap, HttpSession session){
        HpUser targethpUser = (HpUser)BeanUtil.getBean(reqMap,HpUser.class);
        HpUser hpuser = mainService.login(targethpUser);
        ResponseResult responseResult = new ResponseResult(1);
        if (hpuser==null){
            responseResult.setState(0);
            return responseResult;
        }
        session.setAttribute("hpuser",hpuser);
        session.setMaxInactiveInterval(60*60*4);
        return  responseResult;
    }

    @RequestMapping("/write")
    @ResponseBody
    public ResponseResult write(@RequestBody Map<String,String> reqMap, HttpSession session){
        ResponseResult responseResult = new ResponseResult(1);
//        try {
//            HpUser hpUser = getHPUser(session);
//        } catch (LoginOverTimeException e) {
//            responseResult.setState(0);
//            responseResult.setMessage("请重新登录");
//        }
        mainService.write(reqMap,responseResult);
        return  responseResult;
    }

    @RequestMapping("/upPics")
    @ResponseBody
    public ResponseResult upPic(@RequestParam("file") MultipartFile file){
        log.info("enter controller upPics");
        ResponseResult responseResult = new ResponseResult(1);
        if (file==null){
            responseResult.setState(0);
            responseResult.setMessage("图片为空");
        }else {
            String filePath="";
            try {
//                mainService.asynTest("999999");
                StringBuffer sf = new StringBuffer();
                sf.append("pic/").append(MyDateUtil.getNowDateStr("yyyyMMdd")).append("/");
                String fileName= NameUtil.getNamePrefix(4,2)+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

                mainService.uploadPicToSFTP(file.getInputStream(),sf.toString(),fileName);
                filePath = sf.append(fileName).toString();
            } catch (Exception e) {
                log.info("uppicsException:"+e);
                responseResult.setState(0);
                responseResult.setMessage("上传异常");
            }
            responseResult.setMessage(filePath);
        }
        return  responseResult;
    }

    public HpUser getHPUser(HttpSession session) throws LoginOverTimeException {
        HpUser hpUser =(HpUser)session.getAttribute("hpuser");
        if(hpUser!=null){
            return hpUser;
        }else{
            throw new LoginOverTimeException();
        }
    }

//    @RequestMapping("/test")
//    public String test(){
//        mainService.WXmessage();
//        return "test";
//    }

}
