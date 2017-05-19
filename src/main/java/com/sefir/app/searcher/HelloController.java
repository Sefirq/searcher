package com.sefir.app.searcher;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {
    /**
     * @return index view from the root address
     */
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
