package cn.duckflew.text_msg;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author DuckFlew
 */
@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Component
@Slf4j
public class SendTelephoneCode
{

    @Autowired
    Client client;

    @RabbitListener(queues = {"msg.text_msg_check"})
    public void send(Map<String,String> checkPhoneCodeMap)
    {
        String phoneNumber = checkPhoneCodeMap.get("phoneNumber");
        String checkCode = checkPhoneCodeMap.get("checkCode");
        log.info("短信推送到phoneNumber:{}",phoneNumber);
        log.info("手机验证码是{}",checkCode);
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName("阿里云短信测试")
                .setTemplateCode("SMS_154950909")
                .setPhoneNumbers(phoneNumber)
                .setTemplateParam("{\"code\":\""+checkCode+"\"}");
        RuntimeOptions runtime = new RuntimeOptions();
        SendSmsResponse sendSmsResponse = null;
        try
        {
            sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtime);
            log.info("短信发送状态码:{}", sendSmsResponse.statusCode);
            log.info(String.valueOf(sendSmsResponse.body));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
