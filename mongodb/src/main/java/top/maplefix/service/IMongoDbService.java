package top.maplefix.service;

import top.maplefix.entity.Book;

import java.util.List;

/**
 * @description: mongodb测试接口
 * @author: Maple on 2019/10/9 14:06
 * @editored:
 */
public interface IMongoDbService {
    /**
     * 保存对象
     * @param book
     * @return
     */
    public String saveObj(Book book);

    /**
     * 查询所有
     * @return
     */
    public List<Book> findAll();

    /***
     * 根据id查询
     * @param id
     * @return
     */
    public Book getBookById(String id);

    /**
     * 根据名称查询
     *
     * @param name
     * @return
     */
    public Book getBookByName(String name);

    /**
     * 更新对象
     *
     * @param book
     * @return
     */
    public String updateBook(Book book);

    /***
     * 删除对象
     * @param book
     * @return
     */
    public String deleteBook(Book book);

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    public String deleteBookById(String id);

    /**
     * 模糊查询
     * @param search
     * @return
     */
    public List<Book> findByLikes(String search);
}
