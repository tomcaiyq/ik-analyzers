package com.tomcai.solr.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tomcai.solr.pojo.Island;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/island")
public class IslandController {

    @Resource
    private SolrClient client;

    @GetMapping("search")
    @ResponseBody
    public Object search(@RequestParam(defaultValue = "*:*", required = false) String q,
                         @RequestParam(defaultValue = "1", required = false) int page) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Island> list;
        Map<String, Map<String, List<String>>> highlighting;
        try {
            SolrQuery query = new SolrQuery();
            // 设置查詢条件
            query.setQuery(q);
            query.set("df", "island_keywords");
            // 分页 偏移量和行数
            query.setStart((page - 1) * 5);
            query.setRows(5);
            //设置排序
            query.setSort("id", SolrQuery.ORDER.asc);
            // 设置高亮
            query.setHighlight(true);
            query.addHighlightField("name");
            query.addHighlightField("description");
            query.setHighlightSimplePre("<span style='color:red'>");
            query.setHighlightSimplePost("</span>");
            query.setHighlightFragsize(2147483647);
            // 处理结果
            QueryResponse response = client.query("island_core", query);
            highlighting = response.getHighlighting();
            SolrDocumentList results = response.getResults();
            if (!results.isEmpty()) {
                Gson gson = new Gson();
                String s = gson.toJson(results);
                list = gson.fromJson(s, new TypeToken<List<Island>>() {
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
        String fileName = "C:/Users/HTHJ_DEMO/Desktop/cms_island.xlsx";
        EasyExcel.read(fileName, Island.class, new IslandListener(client)).sheet().doRead();
    }
}

@Slf4j
class IslandListener extends AnalysisEventListener<Island> {

    private SolrClient client;

    private List<Island> list = new ArrayList<>();

    IslandListener(SolrClient client) {
        this.client = client;
    }

    @Override
    public void invoke(Island island, AnalysisContext analysisContext) {
        list.add(island);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        try {
            client.addBeans("island_core", list);
            client.commit("island_core");
        } catch (SolrServerException | IOException e) {
            log.error(e.getMessage());
        }
        log.info("数据导入完毕");
    }
}