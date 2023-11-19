package com.jchhh.content;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
public class EnumTest {

    public enum Color {
        RED(1,"red"), GREEN(2,"green"), BLANK(3,"blank"), YELLOW(4,"yellow");
        int num;
        String colour;

        Color() {
        }

        Color(int num, String colour) {
            this.num = num;
            this.colour = colour;
        }
    }

    @Test
    public void test01() {
        String c = Color.RED.colour;
        System.out.println(c);
    }

}
