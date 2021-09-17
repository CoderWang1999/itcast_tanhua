package com.tanhua.sso.service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.tanhua.sso.config.AliyunSMSConfig;
import com.tanhua.sso.vo.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @author ZJWzxy
 * @date 2021/04/07
 */
@Service
@Slf4j
public class SmsService {

    @Autowired
    private AliyunSMSConfig aliyunSMSConfig;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 发送短信验证码
     * 注意: 由于阿里云短信服务无法申请
     *
     * @param mobile 手机号
     * @return 返回生成的验证码
     */
    public String sendSms(String mobile) {
        DefaultProfile profile = DefaultProfile.getProfile(this.aliyunSMSConfig.getRegionId(),
                this.aliyunSMSConfig.getAccessKeyId(), this.aliyunSMSConfig.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);
        //生成随机验证码
        String code = RandomUtils.nextInt(100000, 999999) + "";

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(this.aliyunSMSConfig.getDomain());
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", this.aliyunSMSConfig.getRegionId());
        //目标手机号
        request.putQueryParameter("PhoneNumbers", mobile);
        //签名名称
        request.putQueryParameter("SignName", this.aliyunSMSConfig.getSignName());
        //短信模板code
        request.putQueryParameter("TemplateCode", this.aliyunSMSConfig.getTemplateCode());
        //模板中变量替换
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            String message = "\"Message\":\"OK\"";
            if (StringUtils.contains(response.getData(), message)) {
                return code;
            }
            log.info("发送验证码成功~ data" + data);

        } catch (Exception e) {
            log.error("发送验证码失败~ mobile" + mobile, e);
        }
        return null;
    }

    /**
     * 发送短信验证码
     * 实现:发送完成短信验证码后,需要将验证码保存到redis中
     *
     * @param phone 手机号
     * @return 返回信息
     */
    public ErrorResult sendCheckCode(String phone) {
        String redisKey = "CHECK_CODE_" + phone;
        //判断该手机号发送的验证码是否失效
        if (this.redisTemplate.hasKey(redisKey)) {
            String msg = "上一次发送的验证码还未失效";
            return ErrorResult.builder().errCode("000001").errMessage(msg).build();
        }
        //设置随机验证码
        String code = RandomUtils.nextInt(100000, 999999) + "";
        if (StringUtils.isEmpty(code)) {
            String msg = "发送短信验证码失败";
            //短信发送失败,向表现层返回错误代码以及错误信息
            return ErrorResult.builder().errCode("000000").errMessage(msg).build();
        }
        //短信发送成功,将验证码保存到redis中,5分钟有效期
        this.redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(5));
        return null;
    }
}
