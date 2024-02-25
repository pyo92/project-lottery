package com.example.projectlottery.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class HomeController {

    /**
     * index view
     * @return index view file name
     */
    @GetMapping
    public String home(HttpSession session, ModelMap map) {
        //session attribute 에서 response dto 체크
        Object resp = session.getAttribute("isDenied");
        if (resp != null) {
            map.addAttribute("isDenied", resp);
            session.removeAttribute("isDenied");
        }

        return "index";
    }
}
