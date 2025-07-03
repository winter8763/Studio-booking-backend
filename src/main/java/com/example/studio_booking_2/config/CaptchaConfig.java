package com.example.studio_booking_2.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfig {

    @Bean
    public DefaultKaptcha captchaProducer() { //設定驗證碼圖片產生器的樣式與屬性
        Properties props = new Properties();
        props.setProperty("kaptcha.border", "no"); 
        props.setProperty("kaptcha.textproducer.font.color", "black"); 
        props.setProperty("kaptcha.textproducer.char.space", "5");
        props.setProperty("kaptcha.image.width", "150");
        props.setProperty("kaptcha.image.height", "50");
        props.setProperty("kaptcha.textproducer.font.size", "40");

        Config config = new Config(props);
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
