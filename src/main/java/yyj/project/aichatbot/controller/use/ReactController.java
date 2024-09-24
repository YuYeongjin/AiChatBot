package yyj.project.aichatbot.controller.use;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactController {
    @GetMapping(value = {"/"})
    public String redirect() {
        return "forward:/index.html";
    }
}
