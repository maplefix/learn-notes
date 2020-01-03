package top.maplefix.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @description: 文档对象
 * @author: Maple on 2019/10/9 14:01
 * @editored:
 */
@Document( collection = "book")
@Data
public class Book {
    @Id
    private String id;
    private Integer price;
    private String name;
    private String info;
    private String publish;
    private String createTime;
    private String updateTime;
}
