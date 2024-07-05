package com.mysite.travelo.hyo.place;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/place/test_like")
    public String showTestLikePage() {
        return "place/test_like"; // templates 폴더 아래의 경로를 리턴
    }
    @GetMapping("/place/test_bookmark")
    public String showTestBookmarkPage() {
        return "place/test_bookmark"; // templates 폴더 아래의 경로를 리턴
    }

}
