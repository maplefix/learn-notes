package top.maplefix.controller;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import top.maplefix.entity.Book;
import top.maplefix.util.MockUtils;


/**
 * @description: controller测试类
 * @author: Maple on 2019/10/9 15:00
 * @editored:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoDbControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    MongoDbController mongoDbController;

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /**
     * 保存对象
     * @return
     */
    @Test
    public void saveTest() throws Exception{
        Book book = new Book();
        book.setId("4");
        book.setPrice(130);
        book.setName("VUE从入门到放弃");
        book.setInfo("前端框架");
        book.setPublish("人名大学出版社");
        Gson gson = new Gson();
        String params = gson.toJson(book);
        MvcResult mvcResult = MockUtils.mockPostTest("/mongo/save",params,mockMvc);
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("保存对象-->" + result);
    }

    /**
     * 查询所有
     * @return
     */
    @Test
    public void findAllTest() throws Exception{
        MockUtils.mockGetTest("/mongo/findAll",mockMvc);
    }

    /***
     * 根据id查询
     * @return
     */
    @Test
    public void findByIdTest() throws Exception{
        MockUtils.mockGetTest("/mongo/findOne?id=1",mockMvc);
    }

    /**
     * 根据名称查询
     * @return
     */
    @Test
    public void findByNameTest() throws Exception{
        MockUtils.mockGetTest("/mongo/findOneByName?name=Python从入门到放弃",mockMvc);
    }

    /**
     * 更新对象
     * @return
     */
    @Test
    public void updateTest() throws Exception{
        Book book = new Book();
        book.setId("4");
        book.setPrice(130);
        book.setName("VUE从入门到放弃");
        book.setInfo("前端框架--更新");
        Gson gson = new Gson();
        String params = gson.toJson(book);
        MvcResult mvcResult = MockUtils.mockPostTest("/mongo/delOne",params,mockMvc);
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("更新对象-->" + result);
    }

    /***
     * 根据id删除对象
     * @return
     */
    @Test
    public void deleteByIdTest() throws Exception{
        Book book = new Book();
        book.setId("5");
        Gson gson = new Gson();
        String params = gson.toJson(book);
        MvcResult mvcResult = MockUtils.mockPostTest("/mongo/delById",params,mockMvc);
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println("删除对象-->" + result);
    }


    /**
     * 模糊查询
     * @return
     */
    @Test
    public void findByLikesTest() throws Exception{
        MockUtils.mockGetTest("/mongo/findLikes?search=从",mockMvc);
    }
}
