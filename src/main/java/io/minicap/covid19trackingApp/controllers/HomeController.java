package io.minicap.covid19trackingApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

//Simple controlled designed for general pages accessible by all users.
@Controller
public class HomeController 
{  

    @GetMapping("/")
    public String home(Model model)
    {
        return "homepage";
    }
    
    @GetMapping("/test")
    public String test (Model model)
    {    
        model.addAttribute("testName", "This is a string passed to the attribute \"testName\"");
        return "test";
    }

    @GetMapping("/error")
    public String error(Model model)
    {
        model.addAttribute("errormessage", "This is a string passed to the attribute \"testName\"");
        return "error";
    }

    @GetMapping("/chat")
    public String chat(Model model)
    {
        model.addAttribute("errormessage", "This is a string passed to the attribute \"testName\"");
        return "chat";
    }

}
