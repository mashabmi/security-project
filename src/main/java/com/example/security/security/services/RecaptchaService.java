package com.example.security.security.services;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {
    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final String SECRET_KEY = "6LdqIfcqAAAAAL1yr2icvV6ys8t3VNpr9p3USb2s";

    public boolean verifyRecaptcha(String captchaResponse) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s?secret=%s&response=%s", RECAPTCHA_VERIFY_URL, SECRET_KEY, captchaResponse);
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);

        if (response.getBody() != null) {
            return (Boolean) response.getBody().get("success");
        }
        return false;
    }
}
