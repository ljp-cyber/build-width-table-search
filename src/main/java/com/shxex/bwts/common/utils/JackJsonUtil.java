package com.shxex.bwts.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JackJsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void initBySpring() {
        objectMapper = SpringUtil.getBean(ObjectMapper.class);
    }

    public static <T> T parse(byte[] data, Class<T> tClass) {
        try {
            return objectMapper.readValue(data, tClass);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
