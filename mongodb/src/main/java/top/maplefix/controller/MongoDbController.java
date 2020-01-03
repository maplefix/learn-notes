package top.maplefix.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.maplefix.entity.Book;
import top.maplefix.service.IMongoDbService;

import java.util.List;

/**
 * @description: restful接口
 * @author: Maple on 2019/10/9 14:15
 * @editored:
 */
@RestController
public class MongoDbController {

    @Autowired
    private IMongoDbService mongoDbService;

    @PostMapping("/mongo/save")
    public String saveObj(@RequestBody Book book) {
        return mongoDbService.saveObj(book);
    }

    @GetMapping("/mongo/findAll")
    public List<Book> findAll() {
        return mongoDbService.findAll();
    }

    @GetMapping("/mongo/findOne")
    public Book findOne(@RequestParam String id) {
        return mongoDbService.getBookById(id);
    }

    @GetMapping("/mongo/findOneByName")
    public Book findOneByName(@RequestParam String name) {
        return mongoDbService.getBookByName(name);
    }

    @PostMapping("/mongo/update")
    public String update(@RequestBody Book book) {
        return mongoDbService.updateBook(book);
    }

    @PostMapping("/mongo/delOne")
    public String delOne(@RequestBody Book book) {
        return mongoDbService.deleteBook(book);
    }

    @GetMapping("/mongo/delById")
    public String delById(@RequestParam String id) {
        return mongoDbService.deleteBookById(id);
    }

    @GetMapping("/mongo/findLikes")
    public List<Book> findByLikes(@RequestParam String search) {
        return mongoDbService.findByLikes(search);
    }
}
