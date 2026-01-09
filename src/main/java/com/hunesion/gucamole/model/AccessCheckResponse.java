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
public class AccessCheckResponse {
    private boolean allowed = false;
    private boolean evaluated = false;
    private String matchedPolicyName;
    private String denialReason;
}
