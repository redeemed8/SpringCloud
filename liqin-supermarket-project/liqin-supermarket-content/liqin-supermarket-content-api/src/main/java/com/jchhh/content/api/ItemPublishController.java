package com.jchhh.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ItemPublishController {

    @GetMapping("/itempreview/{itemId}")
    public ModelAndView preview(@PathVariable("itemId") String itemId) {
        return null;
    }

}
