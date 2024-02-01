package com.keepon;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class Demo {
    public static void main(String[] args) {
        log.info("hello world, date {}", new Date());
    }
}
