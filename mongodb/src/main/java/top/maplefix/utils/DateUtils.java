package top.maplefix.utils;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description: 日期转换工具类
 * @author: Maple on 2019/10/9 14:27
 * @editored:
 */
public class DateUtils {
    /**
     * 按照yyyy-MM-dd HH:mm:ss的格式，日期转字符串
     * @param date
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String date2Str(Date date){
        return date2Str(date,"yyyy-MM-dd HH:mm:ss");
    }
    /**
     * 按照yyyy-MM-dd HH:mm:ss的格式，字符串转日期
     * @param date
     * @return
     */
    public static Date str2Date(String date){
        if(!StringUtils.isEmpty(date)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new Date();
        }else{
            return null;
        }
    }
    /**
     * 按照参数format的格式，日期转字符串
     * @param date
     * @param format
     * @return
     */
    public static String date2Str(Date date,String format){
        if(date != null){
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        }else{
            return "";
        }
    }

}
