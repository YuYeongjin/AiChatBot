package yyj.project.aichatbot.controller.use;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/use")
public class UseController {


    @PostMapping("/close")
    public void close(){
        System.out.println("close");
        System.exit(0);
    }

}
