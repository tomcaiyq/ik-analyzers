package com.tomcai.solr.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tomcai.solr.pojo.Book;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Controller
@Slf4j
@RequestMapping("/book")
public class BookController {

    @Resource
    private SolrClient client;

    @RequestMapping("search")
    @ResponseBody
    public Object search(String q) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Book> list;
        Map<String, Map<String, List<String>>> highlighting;
        try {
            SolrQuery query = new SolrQuery();
            if (Objects.isNull(q))
                query.setQuery("*:*");
            else {
                query.setQuery(q);
                query.set("df", "book_keywords");
            }
            query.setHighlight(true);
            query.addHighlightField("name");
            query.addHighlightField("describe");
            query.setHighlightSimplePre("<span style='color:red'>");
            query.setHighlightSimplePost("</span>");
            query.setHighlightFragsize(2147483647);

            QueryResponse response = client.query("book_core", query);
            highlighting = response.getHighlighting();
            SolrDocumentList results = response.getResults();
            if (!results.isEmpty()) {
                Gson gson = new Gson();
                String s = gson.toJson(results);
                list = gson.fromJson(s, new TypeToken<List<Book>>() {
                }.getType());
            } else {
                list = new ArrayList<>();
            }
            result.put("docs", list);
            result.put("highlight", highlighting);
        } catch (SolrServerException | IOException e) {
            log.error(e.getMessage());
        }

        return result;
    }

    @RequestMapping("add")
    @ResponseBody
    public void add() {
        List<Book> list = new ArrayList<>();
        try {
            list.add(new Book(1L, "倚天屠龙记", 38.8d, "好书"));
            list.add(new Book(2L, "射雕英雄传", 28.8d, "坏书"));
            list.add(new Book(3L, "雪山飞狐", 18.8d, "黄书"));
            list.add(new Book(4L, "笑傲江湖", 58.8d, "鬼书"));
            list.add(new Book(5L, "鹿鼎记", 48.8d, "小书"));
            client.addBeans("book_core", list);
            client.commit("book_core");
        } catch (SolrServerException | IOException e) {
            log.error(e.getMessage());
        }
        log.info("数据导入完毕");
    }
}
