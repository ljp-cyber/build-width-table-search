package com.shxex.bwts.kafka;

import com.alibaba.fastjson.JSONObject;
import com.shxex.bwts.kafka.entity.Maxwell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ScriptType;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mta09
 */
public class MaxwellProcessor {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    public void process(Maxwell maxwell){

        String database = maxwell.getDatabase();
        String table = maxwell.getTable();
        JSONObject old = (JSONObject)maxwell.getOld();
        JSONObject data = (JSONObject)maxwell.getData();
        old.forEach((s, o) -> {
            Object newData = data.get(s);
            Map<String, Map<String, Map<String, Object>>> dbToEsMap = new HashMap<>();
            Object o1 = dbToEsMap.get(database).get(table).get(s);



            ArrayList<UpdateQuery> queries = new ArrayList<>();
            IndexCoordinates indexCoordinates = IndexCoordinates.of("");
            BulkOptions bulkOptions = BulkOptions.builder().build();

            /*
            第一层：es索引 第二层：索引类型 第三层：关联健 映射 字段列表
            逻辑：因为存在多表关联，该Map记录各个索引的关联关系
            其中第三层Map<String,List<String>> 的key为关联键，value为该关联键对应的字段
            */
            Map<String, Map<String, Map<String,List<String>>>> indexs = new HashMap<>();
            for (String index : indexs.keySet()) {
                Map<String, Map<String,List<String>>> types = indexs.get(index);
                for (String type : types.keySet()) {
                    Map<String,List<String>> keys = types.get(type);
                    for (String key : keys.keySet()) {
                        UpdateQuery.Builder updateQueryBuilder = null;
                        if("id".equals(key)){
                            updateQueryBuilder = UpdateQuery.builder(data.get("id").toString());
                        }else{
                            Criteria criteria = new Criteria(key).is(data.get("key"));
                            updateQueryBuilder = UpdateQuery.builder(new CriteriaQuery(criteria));
                        }


                        StringBuilder scriptBuilder = new StringBuilder();
                        List<String> fields = keys.get(key);
                        for (String field : fields) {
                            String idOrCode = "ctx._source."+field+"="+data.get(field)+"";
                            scriptBuilder.append(idOrCode);
                        }

                        Map<String, Object> params = new HashMap<>();
                        updateQueryBuilder.withScriptType(ScriptType.INLINE);
                        updateQueryBuilder.withScript(scriptBuilder.toString());
                        updateQueryBuilder.withScriptedUpsert(true);
                        updateQueryBuilder.withParams(params);
                        queries.add(updateQueryBuilder.build());

                    }
                }
            }
//            elasticsearchTemplate.bulkUpdate(queries, User.class);
            elasticsearchTemplate.bulkUpdate(queries, bulkOptions, indexCoordinates);


        });


    }

}
