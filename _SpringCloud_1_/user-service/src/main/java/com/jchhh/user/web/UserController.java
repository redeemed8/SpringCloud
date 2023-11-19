package com.jchhh.user.web;

import com.jchhh.user.config.PatternProperties;
import com.jchhh.user.pojo.User;
import com.jchhh.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/user")
//@RefreshScope
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

//    @Value("${pattern.dataformat}")
//    private String dataformat;

    private PatternProperties properties;

    @Autowired
    public void setProperties(PatternProperties properties) {
        this.properties = properties;
    }

    @GetMapping("now")
    public String now(@RequestHeader(value = "ReferTo", required = false) String ReferTo) {
//        System.out.println("password:==" + ReferTo);
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(properties.getDataformat()));
    }

    @GetMapping("prop")
    public PatternProperties properties() {
        return properties;
    }

    /**
     * 路径： /user/110
     *
     * @param id 用户id
     * @return 用户
     */
    @GetMapping("/{id}")
    public User queryById(@PathVariable("id") Long id) {
        return userService.queryById(id);

    }
}
