package top.maplefix.encry.utils;

import org.apache.tomcat.util.codec.binary.Base64;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * About:
 * Other:
 * Created: xiaoming wang on 2018/7/13 19:16.
 * Editored:
 */
public class Base64Utils {

    /**
     * 返回base64位编码
     * @param keyBytes
     * @return
     */
    public static String encryptBASE64(byte[] keyBytes){
        return new String(Base64.encodeBase64(keyBytes));
    }

    /**
     * BASE64解码
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decoderBASE64(String key) throws IOException {
        return Base64.decodeBase64(key);
    }



    /**
     * 对指定的明文采用Base64编码，未遵循RFC 2045
     *
     * @param data        明文
     * @param charsetName 将明文转成字节数组时采用的字符集
     * @return 密文
     * @throws Exception
     */
    public static String encode(String data, String charsetName){
        try {
            byte[] bytes = Base64.encodeBase64(data.getBytes(charsetName));
            return new String(bytes, charsetName);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 对指定的密文采用Base64解码
     *
     * @param data        密文
     * @param charsetName 将密文转成字节数组时采用的字符集
     * @return 明文
     * @throws Exception
     */
    public static String decode(String data, String charsetName) {
        try {
            byte[] bytes = Base64.decodeBase64(data.getBytes(charsetName));
            return new String(bytes, charsetName);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Base64字符串转 二进制流
     *
     * @param base64String Base64
     * @return base64String
     * @throws IOException 异常
     */
    public static byte[] baseToBin(String base64String) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return base64String != null ? decoder.decodeBuffer(base64String) : null;
    }

    /**
     * 将byte数组进行base64解码
     * @param inputByte 待解码字节数组
     * @return
     * @throws IOException
     */
    public static byte[] base64Decode(byte[] inputByte) throws IOException{
        return Base64.decodeBase64(inputByte);
    }

    /**
     * 将byte数组进行base64编码
     * @param inputByte 待编码字节数组
     * @return
     * @throws IOException
     */
    public static byte[] base64Encode(byte[] inputByte) throws IOException{
        return Base64.encodeBase64(inputByte);
    }
}
