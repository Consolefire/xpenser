package com.consolefire.xpenser.cloud.user.data.dto;

import java.time.Instant;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<String> roles; // Only role names
    private Set<String> privileges; // Only privilege names
}