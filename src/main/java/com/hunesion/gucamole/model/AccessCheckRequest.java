package com.hunesion.gucamole.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor      // <-- ADD THIS (required for Jackson deserialization)
@AllArgsConstructor     // <-- ADD THIS (required for @Builder to work with @NoArgsConstructor)
@Data
@Builder
public class AccessCheckRequest {
    private String username;
    private List<String> userRoles;
    private String endpoint;
    private String httpMethod;
    private String department;
    private String userLevel;
    private Map<String, String> attributes;
}
