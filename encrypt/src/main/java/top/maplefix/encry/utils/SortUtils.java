package top.maplefix.encry.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @Description: 参数进行字典升序排列
 * @Author: wangjg on 2019/8/2 15:30
 * @Editored:
 */
public class SortUtils {
    /**
     * @param param 参数
     * @param encode 编码
     * @return
     */
    public static String formatUrlParam(Map<String, String> param, String encode) {
        if(StringUtils.isEmpty(encode)){
            encode = "UTF-8";
        }
        String params = "";
        Map<String, String> map = param;

        try {
            List<Map.Entry<String, String>> items = new ArrayList<Map.Entry<String, String>>(map.entrySet());

            //对所有传入的参数按照字段名从小到大排序
            //Collections.sort(items); 默认正序
            //可通过实现Comparator接口的compare方法来完成自定义排序
            Collections.sort(items, new Comparator<Map.Entry<String, String>>() {
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    // TODO Auto-generated method stub
                    return (o1.getKey().toString().compareTo(o2.getKey()));
                }
            });

            //构造URL 键值对的形式
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> item : items) {
                if (StringUtils.isNotEmpty(item.getKey())) {
                    String key = item.getKey();
                    String val = item.getValue();
                    //val = URLEncoder.encode(val, encode);
                    if ( null != val && !"".equals(val) && !"sign".equals(key) && !"signMethod".equals(key)) {
                        sb.append(key + "=" + val);
                    }
                    sb.append("&");
                }
            }

            params = sb.toString();
            if (!params.isEmpty()) {
                params = params.substring(0, params.length() - 1);
            }
        } catch (Exception e) {
            return "";
        }
        return params;
    }
    public static void main(String[] args) throws Exception{
        Map<String, String> map = new HashMap<>(16);
        JSONArray deviceList = new JSONArray();
        JSONObject list = new JSONObject();
        list.put("deviceId", "012018121000000080");
        list.put("deviceIndex", "1");
        deviceList.add(list);
        map.put("deviceList", deviceList.toJSONString());
        map.put("deviceId", "012018121000000080");
        map.put("timestamp","20190802102259014");
        map.put("sign","oiMCIsImFtdCI6IjAuMDIifQ==");
        map.put("reqId","3031020190718152053107778");
        map.put("bcType","1");
        JSONObject voiceData = new JSONObject();
        voiceData.put("payType", "0");
        voiceData.put("amt", "0.02");
        voiceData.put("discountAmt", "0.01");
        map.put("voiceData",Base64Utils.encode(voiceData.toJSONString(),"UTF-8"));

        String url = SortUtils.formatUrlParam(map, "utf-8");
        System.out.println(url);
    }

}
