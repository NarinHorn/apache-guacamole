package com.hunesion.gucamole.service;

import com.hunesion.gucamole.model.AccessCheckRequest;
import com.hunesion.gucamole.model.AccessCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessPolicyService {
    private final RestTemplate restTemplate;
    @Value("${policy.api.url:http://localhost:8081/api/access/check}")
    private String policyApiUrl;
    @Value("${policy.api.enabled:true}")
    private boolean policyCheckEnabled;
    /**
     * Check if user is allowed to access the target via SSH
     */
    public boolean checkAccess(String username, List<String> userRoles,
                               String targetHost, String targetPort,
                               String sshUsername) {
        log.info("checkAccess in AccessPolicyService");
        if (!policyCheckEnabled) {
            log.warn("Policy check is disabled - allowing access by default");
            return true;
        }
        // Build attributes map
        Map<String, String> attributes = new HashMap<>();
        attributes.put("protocol", "ssh");
        attributes.put("hostname", targetHost);
        attributes.put("port", targetPort);
        attributes.put("username", sshUsername);
        // Build request matching API format
        AccessCheckRequest request = AccessCheckRequest.builder()
                .username(username)
                .userRoles(userRoles)
                .endpoint("/api/guacamole/connect")
                .httpMethod("POST")
                .department(null)
                .userLevel(null)
                .attributes(attributes)
                .build();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Username", "admin");

            HttpEntity<AccessCheckRequest> entity = new HttpEntity<>(request, headers);

            log.info("Checking access policy for user: {}, roles: {}, target: {}:{}",
                    username, userRoles, targetHost, targetPort);
            ResponseEntity<AccessCheckResponse> response = restTemplate.postForEntity(
                    policyApiUrl,
                    entity,
                    AccessCheckResponse.class
            );
            if (response.getBody() != null) {
                AccessCheckResponse body = response.getBody();
                log.info("Policy check result: allowed={}, reason={}",
                        body.isAllowed(), body.getDenialReason());
                return body.isAllowed();
            }
            log.warn("Empty response from policy API - denying access");
            return false;
        } catch (RestClientException e) {
            log.error("Failed to check access policy: {}", e.getMessage());
            return false; // Fail-closed
        }
    }
}
