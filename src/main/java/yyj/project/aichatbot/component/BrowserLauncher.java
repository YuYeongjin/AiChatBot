package yyj.project.aichatbot.component;


import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URI;

@Component
public class BrowserLauncher {

    @EventListener(ApplicationReadyEvent.class)
    public void launchBrowser() {
        if (!java.awt.GraphicsEnvironment.isHeadless()) {
            try {
                // React 화면을 브라우저에서 8000번 포트로 열기
                Desktop.getDesktop().browse(new URI("http://localhost:8000"));
            } catch (Exception e) {
               System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Headless 모드에서는 브라우저를 실행할 수 없습니다.");
        }
    }
}