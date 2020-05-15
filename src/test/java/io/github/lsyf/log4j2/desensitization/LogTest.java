package io.github.lsyf.log4j2.desensitization;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.IntStream;


public class LogTest {
    static Logger log = LogManager.getLogger(LogTest.class);

    public static void main(String[] args) {


        long start2 = System.currentTimeMillis();
        IntStream.range(1, 500000).forEach(i -> log.info("phone=123412341234123412341234"));
        long end2 = System.currentTimeMillis();

        System.out.println(end2 - start2);

    }
}
