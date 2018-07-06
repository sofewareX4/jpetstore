package org.csu.mypetstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller

@SessionAttributes({"account"})
public class IndexController {
    @GetMapping("/")

    public String viewIndex(){
        return "index";
    }

    @GetMapping("/help")
    public String viewHelp(){
        return "help";
    }
}
