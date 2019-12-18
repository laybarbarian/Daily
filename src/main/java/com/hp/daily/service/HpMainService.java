package com.hp.daily.service;

import com.alibaba.fastjson.JSONObject;
import com.hp.daily.dao.HpImgMapper;
import com.hp.daily.dao.HpNoteMapper;
import com.hp.daily.dao.HpUserMapper;
import com.hp.daily.entity.*;
import com.hp.daily.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class HpMainService {

    protected final Log log = LogFactory.getLog(getClass());

//    @Autowired
//    private RedisTemplate redisTemplate;

    @Resource
    private HpUserMapper hpUserMapper;

    @Resource
    private HpNoteMapper hpNoteMapper;

    @Resource
    private HpImgMapper hpImgMapper;

    public HpUser login(HpUser hpUser ){
        HpUserExample hpUserExample = new HpUserExample();
        HpUserExample.Criteria criteria = hpUserExample.createCriteria();
        criteria.andNameEqualTo(hpUser.getName().trim());
        criteria.andPasswordEqualTo(hpUser.getPassword().trim());
        return hpUserMapper.selectByExample(hpUserExample).isEmpty()?null:hpUserMapper.selectByExample(hpUserExample).get(0);
    }

    public void write(Map<String, String> reqMap, ResponseResult responseResult) {
        log.info("enter write");
        try {
            String note = reqMap.get("note");
            String picsStr = reqMap.get("pics");
            HpNote hpNote =new HpNote(note);
            hpNote.setDate(MyDateUtil.getNowDateYYYYMMdd());
            hpNote.setTime(new Date());
            hpNoteMapper.insertSelective(hpNote);
            int id = hpNote.getId();
            int num=0;
            if (picsStr.length()>10){
                JSONObject jo = JSONObject.parseObject(picsStr);
                Iterator it =jo.entrySet().iterator();
                HpImg hpImg = new HpImg();
                hpImg.setNoteid(id);
                while (it.hasNext()) {
                    Map.Entry<String,String> entry = (Map.Entry<String,String>)it.next();
                    hpImg.setUrl(entry.getValue().toString());
                    hpImgMapper.insertSelective(hpImg);
                    num++;
                }
            }
            WXmessageOnOneMoment(note,num);//remind
        } catch (ParseException e) {
            log.info("parseException at "+e);
        }
    }

    public void getHomePage(String datestr, ModelMap map) throws ParseException {
        Date sqldate = MyDateUtil.getNowDateYYYYMMdd();
        try {
            if (!"1".equals(datestr)){
                sqldate =MyDateUtil.getDateByPatternStr("yyyy-MM-dd",datestr);
            }
        } catch (Exception e) {
            sqldate = MyDateUtil.getNowDateYYYYMMdd();
        }
        HpNoteExample hpNoteExample = new HpNoteExample();
        HpNoteExample.Criteria criteria = hpNoteExample.createCriteria();
        criteria.andDateEqualTo(sqldate);
        List<HpNote> notes = hpNoteMapper.selectByExampleWithBLOBs(hpNoteExample);
        List<HpNoteRes> resotes = new ArrayList<>();
        if (notes!=null&&notes.size()>0){
            for (HpNote note :notes){
                List<String> hpimgss = new ArrayList<>();
                HpNoteRes hpNoteRes = new HpNoteRes();
//                HpImgExample hpImgExample = new HpImgExample();
//                HpImgExample.Criteria criteria1 = hpImgExample.createCriteria();
//                criteria1.andNoteidEqualTo(note.getId());
//                List<HpImg> hpImgs = hpImgMapper.selectByExampleWithBLOBs(hpImgExample);
//                hpNoteRes.setNote(note.getNote());
//                hpNoteRes.setTime(MyDateUtil.getDateStrByPatternDate("HH:mm:ss",note.getTime()));
//                    if (hpImgs!=null&&hpImgs.size()>0) {
//                        for (HpImg img :hpImgs) {
//                            hpimgss.add(img.getBasesf());
//                        }
//                    }
//                hpNoteRes.setBase64(hpimgss);
//                resotes.add(hpNoteRes);
            }
        }else {
            map.addAttribute("notes","");
        }
        map.addAttribute("notes",resotes);
    }


    public ResponseResult getDetails(Map<String, String> reqMap) throws ParseException {
        ResponseResult responseResult = new ResponseResult(1);
        String datestr = reqMap.get("date");
        Date sqldate ;
        try {
            if (!"".equals(datestr)) {
                sqldate =MyDateUtil.getDateByPatternStr("yyyy-MM-dd",datestr);
            }else {
                sqldate = MyDateUtil.getNowDateYYYYMMdd();
            }
        } catch (Exception e) {
            sqldate = MyDateUtil.getNowDateYYYYMMdd();
        }
        HpNoteExample hpNoteExample = new HpNoteExample();
        HpNoteExample.Criteria criteria = hpNoteExample.createCriteria();
        criteria.andDateEqualTo(sqldate);
        List<HpNote> notes = hpNoteMapper.selectByExampleWithBLOBs(hpNoteExample);
        List<HpNoteRes> resotes = new ArrayList<>();
        if (notes!=null&&notes.size()>0){
            for (HpNote note :notes){
                List<String> hpimgss = new ArrayList<>();
                HpNoteRes hpNoteRes = new HpNoteRes();
                HpImgExample hpImgExample = new HpImgExample();
                HpImgExample.Criteria criteria1 = hpImgExample.createCriteria();
                criteria1.andNoteidEqualTo(note.getId());
                List<HpImg> hpImgs = hpImgMapper.selectByExample(hpImgExample);
                hpNoteRes.setNote(note.getNote());
                hpNoteRes.setTime(MyDateUtil.getDateStrByPatternDate("HH:mm:ss",note.getTime()));
                if (hpImgs!=null&&hpImgs.size()>0) {
                    for (HpImg img :hpImgs) {
                        hpimgss.add(img.getUrl());
                    }
                }
                hpNoteRes.setUrls(hpimgss);
                resotes.add(hpNoteRes);
            }
        }else {
            responseResult.setState(0);
            responseResult.setMessage("0");
        }
        responseResult.setData(resotes);
        responseResult.setMessage(String.valueOf(resotes.size()));
        return  responseResult;
    }

//    @Async("taskExecutor")
    public void uploadPicToSFTP(InputStream file, String path, String name) throws Exception {
        SFTPUtil SFTPUtil = new SFTPUtil();
        SFTPUtil.login();
        SFTPUtil.upload(path,name ,file);
        SFTPUtil.logout();
    }

//    @Async("taskExecutor")
    public void asynTest(String s){
        log.info(Thread.currentThread()+s);
    }

    public void WXmessageOnOneMoment(String note, int num){
        log.info("enter WXmessageOnOneMoment");
        String key = "access_token";
//        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String access_token;
//        try {
//            // 缓存存在
//            boolean hasKey = redisTemplate.hasKey(key);
//            if (hasKey) {
//                access_token = operations.get(key);
//            }else {
//                // 插入缓存
//                String res1 = HttpUtil.doGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx4381fbe297d47f0d&secret=c4ccd88fca7b1168cec6e9483945b69f");
//                JSONObject jsonObject = JSONObject.parseObject(res1);
//                access_token= jsonObject.getString("access_token");
//                operations.set(key, access_token, 7200, TimeUnit.SECONDS);
//            }
//        } catch (Exception e) {
//            log.info("redis exception catch "+e);
        String res1 = null;
        try {
            res1 = HttpsUtil.doGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx4381fbe297d47f0d&secret=c4ccd88fca7b1168cec6e9483945b69f");
        } catch (Exception e) {
            log.error(e);
        }
        JSONObject jsonObject = JSONObject.parseObject(res1);
            access_token= jsonObject.getString("access_token");
//        }
        log.info("access_token= "+access_token);
        /*JSONObject json = new JSONObject();
        json.put("touser","oZe9ow-YpzmD1qQTQcTArvWANw0Y"); // mine openid
        json.put("msgtype","text");
        JSONObject jsonmsg = new JSONObject();
        jsonmsg.put("content","one new moment");
        json.put("text",jsonmsg);
        StringBuilder url2 = new StringBuilder();
        url2.append("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=")
        .append(access_token);
        String res2 = HttpUtil.doPost(url2.toString(),json.toJSONString());*/  // 发送客服消息 需要用户保持活跃

        // 发送模板消息
        JSONObject json = new JSONObject();
        json.put("touser","oZe9ow-YpzmD1qQTQcTArvWANw0Y"); // mine openid
        json.put("template_id","VvYSQcJFV0aIzh9rP2dUumtNqXSl7J3OgnJfH2QOTck");
        JSONObject jsondata = new JSONObject();
        JSONObject notejsondata = new JSONObject();
        notejsondata.put("value",note);
        notejsondata.put("color","#173177");
        jsondata.put("note",notejsondata);
        JSONObject picjsondata = new JSONObject();
        picjsondata.put("value",num);
        picjsondata.put("color","#173177");
        jsondata.put("pic",picjsondata);
        json.put("data",jsondata);
        StringBuilder url3 = new StringBuilder();
        url3.append("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=")
                .append(access_token);
        String res3 = null;
        try {
            res3 = HttpsUtil.doPost(url3.toString(),json.toJSONString());
        } catch (Exception e) {
            log.error(e);
        }
        log.info("res3= "+res3);
    }
}
