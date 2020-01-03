package top.maple.qrcode.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.maple.qrcode.util.QrCodeUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * @author: wangjg on 2019/11/14 15:41
 * @description: 二维码生成接口
 * @editored:
 */
@Controller
@Slf4j
public class QrCodeController {
    private static int width = 300;
    private static int height = 300;

    @Autowired
    HttpServletRequest request;
    /**
     * 根据 url 生成 普通二维码
     */
    @RequestMapping(value = "/createCommonQRCode")
    public void createCommonQrCode(HttpServletResponse response, String url) throws Exception {
        ServletOutputStream stream = null;
        try {
            stream = response.getOutputStream();
            //使用工具类生成二维码
            QrCodeUtils.encode(url, stream);
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            if (stream != null) {
                stream.flush();
                stream.close();
            }
        }
    }
    /**
     * 返回BufferedImage
     */
    @RequestMapping(value = "/createBufferedImage")
    @ResponseBody
    public void createBufferedImage (HttpServletResponse response, String content){
        try {
            //表示到项目的根目录下，要是想到目录下的子文件夹，修改"/"即可
            String  pathh= ResourceUtils.getURL("classpath:").getPath()+"static/image/dark1.png";
            System.out.println(pathh);
            String path = System.getProperty("user.dir")+request.getContextPath()+"/src/main/resources/static/image/dark1.png";

            System.out.println(path);
            //String imgPath = "G:\\BlogData\\TestFile\\dark1.png";

            BufferedImage bufferedImage = QrCodeUtils.encode(content,path,true);
            ImageIO.write(bufferedImage, "png", response.getOutputStream());
            response.setContentType("image/png");
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("transform image stream error",e);
        }
    }

    /**
     * 返回带有前缀的Base64编码内容
     * @param response 响应
     * @param content 二维码内容
     * @return base64
     * @throws Exception
     */
    @RequestMapping(value = "/createBase64String")
    @ResponseBody
    public String createBase64String(HttpServletResponse response, String content) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            String imgPath = "G:\\BlogData\\TestFile\\dark1.png";
            BufferedImage bufferedImage = QrCodeUtils.encode(content,imgPath,true);
            ImageIO.write(bufferedImage, "png", os);
            //原生转码前面没有 data:image/png;base64 这些信息
            return "data:image/png;base64," + Base64.encode(os.toByteArray());
        }catch (Exception e){
            log.error("二维码生成异常,异常原因为：{}",e.getMessage());
            e.printStackTrace();
        }finally {
            if(null != os){
                os.flush();
                os.close();
            }
        }
        return null;
    }

    /**
     * 原生的zxing直接生成base64码
     * @param response 响应
     * @param content 二维码内容
     * @return base64码
     * @throws Exception
     */
    @RequestMapping(value = "/createBase64String2")
    @ResponseBody
    public String createBase64String2(HttpServletResponse response, String content) throws Exception {
        String resultImage;
        if (!StringUtils.isEmpty(content)) {
            ServletOutputStream stream = null;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            @SuppressWarnings("rawtypes")
            HashMap<EncodeHintType, Comparable> hints = new HashMap<>(16);
            // 指定字符编码为“utf-8”
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 指定二维码的纠错等级为中级
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            // 设置图片的边距
            hints.put(EncodeHintType.MARGIN, 2);

            try {
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

                BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
                ImageIO.write(bufferedImage, "png", os);
                //原生转码前面没有 data:image/png;base64 这些字段，返回给前端是无法被解析，可以让前端加，也可以在下面加上
                resultImage = "data:image/png;base64," + Base64.encode(os.toByteArray());
                return resultImage;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    stream.flush();
                    stream.close();
                }
            }
        }
        return null;
    }
    /**
     * 根据 url 生成 带有logo二维码
     */
    @RequestMapping(value = "/createLogoQRCode")
    public void createLogoQrCode(HttpServletResponse response, String url) throws Exception {
        ServletOutputStream stream = null;
        try {
            stream = response.getOutputStream();
            String logoPath = "G:\\BlogData\\TestFile\\favicon.png";
            //使用工具类生成二维码
            QrCodeUtils.encode(url, logoPath, stream, true);
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            if (stream != null) {
                stream.flush();
                stream.close();
            }
        }
    }
}
