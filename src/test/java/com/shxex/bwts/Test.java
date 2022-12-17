package com.shxex.bwts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shxex.bwts.dome.entity.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        User user = new User();
        user.setId(1L);
        user.setUserName("ljp1");

        Map map = new HashMap<>();
        map.put("id", "1");
        map.put("userName", "ljp2");

        ObjectNode x = objectMapper.convertValue(user, ObjectNode.class);
        System.out.println(x);
        ObjectNode x1 = objectMapper.convertValue(map, ObjectNode.class);
        System.out.println(x1);

        Map x2 = objectMapper.convertValue(x1, Map.class);
        System.out.println(x2);

        long start = System.currentTimeMillis();
        int i1 = 1000;
        for (int i = 0; i < i1; i++) {
            Map x3 = objectMapper.readValue(objectMapper.writeValueAsBytes(x), Map.class);
        }
        System.out.println(System.currentTimeMillis()-start);

        long start1 = System.currentTimeMillis();
        for (int i = 0; i < i1; i++) {
            Map x3 = objectMapper.convertValue(x, Map.class);
        }
        System.out.println(System.currentTimeMillis()-start1);
    }
}
