package com.busfeedback.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
public class BrowserLauncher implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI("http://localhost:8080/index.html"));
            }
        } catch (Exception e) {
            System.out.println("Could not open browser automatically: " + e.getMessage());
            System.out.println("Please open manually: http://localhost:8080/index.html");
        }
    }
}
