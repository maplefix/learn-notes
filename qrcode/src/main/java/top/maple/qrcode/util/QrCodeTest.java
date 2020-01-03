package top.maple.qrcode.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: wangjg on 2019/11/14 15:21
 * @description: 测试类
 * @editored:
 */
@Slf4j
public class QrCodeTest {
    public static void main(String[] args)throws Exception {
        log.info("开始生成二维码图片...");
        long start = System.currentTimeMillis();
        try {
            String url = "https://www.maplefix.top";
            String path1 = "G:\\BlogData\\TestFile\\qrcode1.png";
            String path2 = "G:\\BlogData\\TestFile\\qrcode2.png";
            String path3 = "G:\\BlogData\\TestFile\\qrcode3.png";
            String logoPath1 = "G:\\BlogData\\TestFile\\maple.png";
            String logoPath2 = "G:\\BlogData\\TestFile\\maple-round.png";
            QrCodeUtils.encode(url, logoPath1, path1, true);
            QrCodeUtils.encode(url, logoPath2, path2, true);
            QrCodeUtils.encode(url, null, path3, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("生成二维码耗时：" + (end- start));
    }
}
