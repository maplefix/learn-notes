package top.maplefix.util;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @description: junit工具类
 * @author: Maple on 2019/10/9 15:11
 * @editored:
 */
public class MockUtils {

    /**
     * post请求
     * @param url
     * @param params
     * @param mockMvc
     * @return
     * @throws Exception
     */
    public static MvcResult mockPostTest(String url, String params, MockMvc mockMvc) throws Exception{
        MvcResult result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(params))
                .andExpect(status().isOk())
                //.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        return result;
    }

    /**
     * get请求
     * @param url
     * @param mockMvc
     * @throws Exception
     */
    public static void mockGetTest(String url, MockMvc mockMvc) throws Exception{
        MvcResult result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println("GET请求结果-->"+result.getResponse().getContentAsString());
    }

}
