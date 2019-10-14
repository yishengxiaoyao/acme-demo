package com.edu.cat.backoffice.controller;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Transaction;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.invoke.MethodHandles;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@RestController
public class AcmeFinancialBackOfficeController {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired RestTemplate restTemplate;
    @Value("${account.service.address:localhost:8083}") String accountServiceAddress;
    @Value("${customer.service.address:localhost:8084}") String customerServiceAddress;
    private static final int MOCK_PORT = 8765;

    WireMock wireMock = new WireMock(MOCK_PORT);
    WireMockServer wireMockServer = new WireMockServer(MOCK_PORT);

    @PostConstruct
    public void setup() {
        wireMockServer.start();
        wireMock.register(any(urlMatching(".*")).willReturn(aResponse().withFixedDelay(3000)));
    }

    @PreDestroy
    public void shutdown() {
        wireMock.shutdown();
        wireMockServer.shutdown();
    }

    @RequestMapping("/startOfBackOffice-Service")
    public String service2MethodInController() throws InterruptedException {
        Thread.sleep(200);
        log.info("Hello from Acme Financial's Backend service. Calling Acme Financial's Account Microservice and then Customer Microservice");
        String service3 = restTemplate.getForObject("http://" + accountServiceAddress + "/startOfAccount-Microservice", String.class);
        log.info("Got response from Acme Financial's Account Service [{}]", service3);
        String service4 = restTemplate.getForObject("http://" + customerServiceAddress + "/startOfCustomer-Microservice", String.class);
        log.info("Got response from Acme Financial's Customer Service [{}]", service4);
        return String.format("Hello from Acme Financial's Backend service. Calling Acme Financial's Account Service [%s] and then Customer Service [%s]", service3, service4);
    }

    @RequestMapping("/readtimeout")
    public String connectionTimeout() throws InterruptedException {
        Transaction t = Cat.newTransaction(CatConstants.TYPE_CALL, "connectionTimeout");
        Thread.sleep(500);
        try {
            log.info("Calling a missing service");
            restTemplate.getForObject("http://localhost:" + MOCK_PORT + "/readtimeout", String.class);
            return "Should blow up";
        } catch(Exception e) {
            t.setStatus(e);
            Cat.getProducer().logError(e);
            throw e;
        } finally {
            t.complete();
        }
    }

}
