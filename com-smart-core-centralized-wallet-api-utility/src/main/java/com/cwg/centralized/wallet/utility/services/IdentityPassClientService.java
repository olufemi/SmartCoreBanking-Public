/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwg.centralized.wallet.utility.services;

import com.cwg.centralized.wallet.utility.config.IdentityPassClientConfig;
import com.cwg.centralized.wallet.utility.identity.exceptions.RestTemplateResponseErrorHandler;
import com.cwg.centralized.wallet.utility.models.BaseResponse;
import com.cwg.centralized.wallet.utility.models.BvnIdentity;
import com.cwg.centralized.wallet.utility.models.BvnRequest;
import com.cwg.centralized.wallet.utility.util.MemoryCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.HttpMethod.POST;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author SmartCore Contributors
 */
@Component
@Slf4j
public class IdentityPassClientService {

    private final IdentityPassClientConfig identityPassClientConfig;

    private final RestTemplate restTemplate;

    private final MemoryCache cache;

    private HttpHeaders headers;

    @Value("${gen.idpass.token}")
    private String demoPayIdentityPassToken;

    public IdentityPassClientService(MemoryCache cache, IdentityPassClientConfig identityPassClientConfig, RestTemplateBuilder restTemplateBuilder) {
        this.identityPassClientConfig = identityPassClientConfig;
        this.restTemplate = restTemplateBuilder.errorHandler(new RestTemplateResponseErrorHandler()).build();
        this.cache = cache;
        headers = setDefaultHeaders();
    }

    /*
       BVN Verification (No Image)
     */
    public ResponseEntity<?> identityVerificationBvnNoImage(BvnRequest bvnRequest) {
        log.info("identityVerificationBvnNoImage data: BvnRequest: {}", bvnRequest);

        String httpUrl = cache.getApplicationSetting(identityPassClientConfig.getBvnNoImage());
        log.info("getBvnNoImage URL: {}", httpUrl);

        HttpEntity<BvnRequest> request = new HttpEntity<>(bvnRequest, headers);
        URI uri = UriComponentsBuilder.fromHttpUrl(httpUrl).build().toUri();

        log.info("identityVerificationBvnNoImage headers: {}", headers.toString());
        restTemplate.setMessageConverters(messageConverters());
        ResponseEntity<BaseResponse> baseRsponse = restTemplate.exchange(uri, POST, request, BaseResponse.class);

        ObjectMapper mapper = new ObjectMapper();

        BaseResponse baseBody = baseRsponse.getBody();
        Map<String, Object> mapData = baseBody.getData();
        if (mapData != null && !mapData.isEmpty()) {
            BvnIdentity bvnIdentity = mapper.convertValue(mapData.get("data"), BvnIdentity.class);
            //BvnIdentity bvnIdentity = ((BvnIdentity) mapData.get("data"));
            log.info("bvnIdentityResponse =>>>>>  {}", bvnIdentity);
            return ResponseEntity.ok().body(bvnIdentity);
        } else {
            return ResponseEntity.status(baseBody.getStatusCode()).body(baseBody);
        }
    }

    private HttpHeaders setDefaultHeaders() {
        demoPayIdentityPassToken = "gen.idpass.token";
        System.out.println("demoPay IdentityPass Token  :::::::::::::::::::::::::::::::::::::::::::::::::: %S     " + demoPayIdentityPassToken);

        headers = new HttpHeaders();
        headers.add("cache-control", "no-cache");
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
        headers.setBearerAuth(cache.getApplicationSetting(demoPayIdentityPassToken));
        return headers;
    }

    public List<HttpMessageConverter<?>> messageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        return messageConverters;
    }

    /*
     BVN Verification (with Image)
     */
    public ResponseEntity<BvnIdentity> identityVerificationBvnWithImage(BvnRequest bvnRequest) {
        log.info("identityVerificationBvnWithImage data:  BvnRequest: {}", bvnRequest);

        HttpEntity<BvnRequest> request = new HttpEntity<>(bvnRequest, headers);
        URI uri = UriComponentsBuilder
                .fromHttpUrl(cache.getApplicationSetting(identityPassClientConfig.getBvnWithImage()))
                .build().toUri();
        restTemplate.setMessageConverters(messageConverters());
        return restTemplate.exchange(uri, POST, request, BvnIdentity.class);
    }

}
