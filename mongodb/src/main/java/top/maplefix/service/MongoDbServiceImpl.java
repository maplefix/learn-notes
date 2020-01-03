package top.maplefix.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import top.maplefix.entity.Book;
import top.maplefix.utils.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @description: mongodb接口实现类
 * @author: Maple on 2019/10/9 14:07
 * @editored:
 */
@Service
public class MongoDbServiceImpl implements IMongoDbService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存对象
     * @param book
     * @return
     */
    @Override
    public String saveObj(Book book) {
        book.setCreateTime(DateUtils.date2Str(new Date()));
        book.setUpdateTime(DateUtils.date2Str(new Date()));
        mongoTemplate.save(book);
        return "添加成功";
    }

    /**
     * 查询所有
     * @return
     */
    @Override
    public List<Book> findAll() {
        return mongoTemplate.findAll(Book.class);
    }

    /***
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public Book getBookById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, Book.class);
    }

    /**
     * 根据名称查询
     *
     * @param name
     * @return
     */
    @Override
    public Book getBookByName(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        return mongoTemplate.findOne(query, Book.class);
    }

    /**
     * 更新对象
     *
     * @param book
     * @return
     */
    @Override
    public String updateBook(Book book) {
        Query query = new Query(Criteria.where("_id").is(book.getId()));
        Update update = new Update().set("publish", book.getPublish())
                                    .set("info", book.getInfo())
                                    .set("updateTime", new Date());
        // updateFirst 更新查询返回结果集的第一条
        mongoTemplate.updateFirst(query, update, Book.class);
        // updateMulti 更新查询返回结果集的全部
        // mongoTemplate.updateMulti(query,update,Book.class);
        // upsert 更新对象不存在则去添加
        // mongoTemplate.upsert(query,update,Book.class);
        return "更新成功";
    }

    /***
     * 删除对象
     * @param book
     * @return
     */
    @Override
    public String deleteBook(Book book) {
        mongoTemplate.remove(book);
        return "删除成功";
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @Override
    public String deleteBookById(String id) {
        // findOne
        Book book = getBookById(id);
        // delete
        deleteBook(book);
        return "删除成功";
    }

    /**
     * 模糊查询
     * @param search
     * @return
     */
    @Override
    public List<Book> findByLikes(String search){
        Query query = new Query();
        Pattern pattern=Pattern.compile("^.*"+search+".*$", Pattern.CASE_INSENSITIVE);
        query.addCriteria(Criteria.where("name").regex(pattern));
        List<Book> lists = mongoTemplate.find(query, Book.class);
        return lists;
    }
}
