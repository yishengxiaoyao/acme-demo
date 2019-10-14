package com.edu.cat.ui.controller;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;

@RestController
public class AcmeFinancialUIController {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private RestTemplate restTemplate;
    @Value("${backoffice.service.address:localhost:8082}")
    private String backOfficeServiceAddress;

    @RequestMapping("/start")
    public String start() throws InterruptedException {
        log.info("Welcome To Acme Financial. Calling Acme Financial's Back Office Microservice");
        String response = restTemplate.getForObject("http://" + backOfficeServiceAddress + "/startOfBackOffice-Service", String.class);
        Thread.sleep(100);
        log.info("Got response from Acme Financial's Back Office Microservice [{}]", response);
        return response;
    }

    @RequestMapping("/readtimeout")
    public String timeout() throws InterruptedException {
        //业务埋点
        Transaction t = Cat.newTransaction(CatConstants.TYPE_URL, "timeout");
        try {
            Thread.sleep(300);
            log.info("Hello from service1. Calling service2 - should end up with read timeout");
            String response = restTemplate.getForObject("http://" + backOfficeServiceAddress + "/readtimeout", String.class);
            log.info("Got response from service2 [{}]", response);
            return response;
        } catch (Exception e) {
            Cat.getProducer().logError(e);
            t.setStatus(e);
            throw e;
        } finally {
            t.complete();
        }
    }

}
