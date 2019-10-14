package com.edu.cat.customer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;


@RestController
public class AcmeFinancialCustomerController {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @RequestMapping("/startOfCustomer-Microservice")
    public String custommerServiceMethodInController() throws InterruptedException {
        Thread.sleep(2000);
        log.info("Hello from Acme's Customer Microservice");
        return "Hello from Acme's Customer Microservice";
    }
}
