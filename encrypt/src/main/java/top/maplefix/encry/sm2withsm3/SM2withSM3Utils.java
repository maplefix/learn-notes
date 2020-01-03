package top.maplefix.encry.sm2withsm3;

import cn.ehcase.oscca.SM2Utils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.maplefix.encry.utils.Base64Utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @Description: SM2签名验签工具类
 * @Author: wangjg on 2019/8/7 16:53
 *
 * 公私钥生产地址为：http://www.jonllen.com/upload/jonllen/case/jsrsasign-master/sample-sm2.html
 * @Editored:
 */
public class SM2withSM3Utils {
    private static Logger logger = LoggerFactory.getLogger(SM2withSM3Utils.class);

    /**
     * 加签
     * @param resultMap 待加签参数
     * @param privateKey 私钥
     * @return
     */
    public static Map<String, Object> addSign(Map<String, Object> resultMap, String privateKey) {

        if(resultMap.containsKey("deviceList")) {
            JSONArray jsonList = JSONArray.fromObject(resultMap.get("deviceList"));
            resultMap.put("deviceList", jsonList.toString());
        }
        String toSignStr = buildParams(resultMap, false);
        logger.info("待签名参数：" + toSignStr);
        String signData = null;
        try {
            signData = SM2Utils.signData(privateKey,toSignStr);
            resultMap.put("sign", signData);
        } catch (IOException e) {
            logger.error("加签失败，原因为:" + e);
        }
        return resultMap;
    }


    /**
     * 验签
     * @param receiveMap
     * @param publicKey
     * @return
     */
    public static boolean checkSign(Map<String, Object> receiveMap, String publicKey) {
        boolean result = false;
        String originSignValue = receiveMap.get("sign").toString();
        if(receiveMap.containsKey("deviceList")) {
            String deviceList =  receiveMap.get("deviceList").toString();
            deviceList = deviceList.replaceAll("=", ":").replaceAll(" ","");
            receiveMap.put("deviceList", deviceList);
        }

        // 构建传参
        String receiveStr = buildParams(receiveMap, false);

        logger.info("参数按字典排序后为："+receiveStr);
        try {
            result = SM2Utils.verifyData(publicKey, originSignValue, receiveStr);
        } catch (IOException e) {
            logger.error("验签失败，原因为:" + e);
        }
        return result;
    }

    public static void main(String[] args)throws Exception {
        String receiveStr = "bcType=1&deviceList=[{deviceId:06000001,deviceIndex:1}]&reqId=1565159483100&timestamp=1565159483&voiceData=eyJwYXlUeXBlIjoiMCIsImFtdCI6Ii4wLjAyIiwiZGlzY291bnRBbXQiOiIuMC4wMSJ9";
        String publicKey = "043474b03ee8004c79bf5f01347e4fd917f3cd5bca9b3c4e7cc74bdd1c53530b079f5b92106bd307f149eb7987bbdc61d6599e0a69bb280683fd6d91e0e17a7d63";
        String privateKey="3fb167f739b98ddde79dced7830d77e4a92a37c88a4b3a072653993430476f0f";
        String originSignValue = "MEYCIQCI6O/Lg0te9R2/Zu8lKiWErm5j4EFgQ7js4tFg7LiwxQIhAM/TWAa3fA+X6krrzD+OYkFqp6OfxcwJ3f63pEC3Rgq9";
        String signResponseData = SM2Utils.signData(privateKey,receiveStr);
        System.out.println(signResponseData);
        System.out.println(SM2Utils.verifyData(publicKey, signResponseData, receiveStr));
    }
    public static boolean checkSignCom(Map<String, Object> receiveMap, String publicKey) {
        boolean result = false;
        String originSignValue = receiveMap.get("sign").toString();

        // 构建传参
        String receiveStr = buildParams(receiveMap, false);
        logger.info("---signStr---"+receiveStr);

        try {
            result = SM2Utils.verifyData(publicKey, originSignValue, receiveStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 参数按字典序升序排序，且sign 和signMethod 不不做签名。
     * @param payParams 参数
     * @param encoding 是否encode
     * @return
     */
    public static String buildParams(Map<String, Object> payParams, boolean encoding) {
        StringBuilder sb = new StringBuilder();
        List<String> keys = new ArrayList<>(payParams.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            if ("sign".equals(key)||"".equals(payParams.get(key).toString())) {
                continue;
            }
            sb.append(key).append("=");
            if (encoding) {
                sb.append(urlEncode(payParams.get(key).toString()));
            } else {
                sb.append(payParams.get(key));
            }
            sb.append("&");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (Throwable e) {
            return str;
        }
    }

    public static void main2(String[] args) {
        String publicKey="043474b03ee8004c79bf5f01347e4fd917f3cd5bca9b3c4e7cc74bdd1c53530b079f5b92106bd307f149eb7987bbdc61d6599e0a69bb280683fd6d91e0e17a7d63";
        String privateKey="3fb167f739b98ddde79dced7830d77e4a92a37c88a4b3a072653993430476f0f";
        Map<String, Object> responseMap = new HashMap<>(16);
        //1.响应参数
        responseMap.put("code", "0");
        responseMap.put("message", "SUCCESS");
        responseMap.put("timestamp", "1565139853");
        //2.请求参数
        Map<String, Object> requestMap = new HashMap<>(16);
        requestMap.put("timestamp", "1565159483");
        requestMap.put("reqId", "1565159483100");
        requestMap.put("bcType", "1");

        JSONObject voiceData = new JSONObject();
        voiceData.put("payType","0");
        voiceData.put("amt",".0.02");
        voiceData.put("discountAmt",".0.01");
        requestMap.put("voiceData", Base64Utils.encode(voiceData.toString(),"UTF-8"));
        System.out.println("base64后的voiceData: " + Base64Utils.encode(voiceData.toString(),"UTF-8"));
        System.out.println("========================================");
        JSONArray deviceList = new JSONArray();
        JSONObject device = new JSONObject();
        device.put("deviceId","06000001");
        device.put("deviceIndex","1");
        deviceList.add(device);
        requestMap.put("deviceList",deviceList);


        String buildResponsePram = buildParams(responseMap, false);
        String buildRequestPram = buildParams(requestMap, false);

        String signResponseData = null;
        String signRequestData = null;
        try {
            signResponseData = SM2Utils.signData(privateKey,buildResponsePram);
            signRequestData = SM2Utils.signData(privateKey,buildRequestPram);
            System.out.println("待排序返回数据："+responseMap);
            System.out.println("待签名返回数据："+buildResponsePram);
            System.out.println("签名后返回数据："+signResponseData);
            System.out.println("========================================");
            System.out.println("待排序请求数据："+requestMap);
            System.out.println("待签名请求数据："+buildRequestPram);
            System.out.println("签名后请求数据："+signRequestData);
            System.out.println("========================================");
            responseMap.put("sign", signResponseData);
            requestMap.put("sign", signRequestData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String signResponseDataStr ="MEUCIDSmjQHSZ6bmRcsWCwGBYM2EvusAjcDwE3UnRTJw242YAiEA4UNGjy80U4fRNJPY1B38o7xgA/di1sRfkoxEuMu32mY=";
        String responseStr ="code=0&message=SUCCESS&timestamp=1565139853";

        String signRequestStr ="MEUCIDSmjQHSZ6bmRcsWCwGBYM2EvusAjcDwE3UnRTJw242YAiEA4UNGjy80U4fRNJPY1B38o7xgA/di1sRfkoxEuMu32mY=";
        String requestStr ="code=0&message=SUCCESS&timestamp=1565139853";
        try {
            boolean responseResult = SM2Utils.verifyData(publicKey, signResponseData, buildResponsePram);
            System.out.println(checkSign(requestMap,publicKey));
            boolean requestResult = SM2Utils.verifyData(publicKey, signRequestData, buildRequestPram);
            System.out.println("返回信息验签结果: " + responseResult);
            System.out.println("请求信息验签结果: " + requestResult);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}
