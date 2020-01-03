package top.maplefix.encry.utils;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @Description: HmacSHA256签名工具
 * @Author: wangjg on 2019/7/18 17:17
 * @Editored:
 */
public class HmacSHA256Utils {

    private static Logger logger = LoggerFactory.getLogger(HmacSHA256Utils.class);
    /**
     * 字符串HmacSHA256加密后base编码
     * @param stringToSign 待签名字符串
     * @param secret
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String HmacSHA256Str(String stringToSign, String secret,String encoding) throws Exception{
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(encoding),"HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            return new String(Base64Utils.base64Encode(signData),"UTF-8");
        }catch (NoSuchAlgorithmException e){
            throw new IllegalArgumentException(e.toString());
        }catch (UnsupportedEncodingException e){
            throw new IllegalArgumentException(e.toString());
        }catch (InvalidKeyException e){
            throw new IllegalArgumentException(e.toString());
        }
    }

    /**
     * 定义签名根据参数字段的ASCII码值进行排序 加密签名,故使用SortMap进行参数排序
     * @param characterEncoding 编码
     * @param parameters 参与签名的参数串
     * @return
     */
    public static String createSign(String characterEncoding, SortedMap<String, String> parameters,String accesskeySecret) {
        String sign = "";
        try {
            StringBuffer sb = new StringBuffer();
            //升序
            Set es = parameters.entrySet();
            Iterator it = es.iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String k = (String) entry.getKey();
                Object v = entry.getValue();
                if (null != v && !"".equals(v)
                        && !"sign".equals(k) && !"key".equals(k)) {
                    sb.append(k + "=" + v + "&");
                }
            }
            String strA = sb.toString().substring(0, sb.toString().length() - 1);
            //使用HMAC-SHA256进行签名后再将签名进行base64编码
            sign = HmacSHA256Utils.HmacSHA256Str(strA,accesskeySecret,characterEncoding);
        } catch (Exception e) {
            logger.error("计算签名失败", e);
        }
        return sign;
    }

    public static void main(String[] args) {
        Map map = new HashMap(16);
        map.put("access_key_id", "111");
        map.put("request_msg", "0.01");
        map.put("method", "push_msg");
        map.put("request_timestamp", "20190730102259014");
        map.put("device_id", "06000001");
        map.put("push_template", "03");
        map.put("request_id", "3031020190718152053107778");
        map.put("nonce", "ca89b33067db45ae9654cff621af8471");
        SortedMap<String, String> sort = new TreeMap<String, String>(map);
        String checkSign = createSign("UTF-8", sort,"Landipushnxfjpush123");
        System.out.println(checkSign);
    }
}
