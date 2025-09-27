package com.consolefire.xpenser.cloud.auth.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "auth-server") // This prefix maps to auth-server in YAML
public class AuthServerProperties {

    private String issuerUri;
    private Endpoints endpoints = new Endpoints(); // Initialize to prevent NullPointerException

    public String getIssuerUri() {
        return issuerUri;
    }

    public void setIssuerUri(String issuerUri) {
        this.issuerUri = issuerUri;
    }

    public Endpoints getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Endpoints endpoints) {
        this.endpoints = endpoints;
    }

    public static class Endpoints {

        private String authorization = "/oauth2/authorize"; // Provide defaults if desired
        private String pushedAuthorizationRequest = "/oauth2/par";
        private String deviceAuthorization = "/oauth2/device_authorization";
        private String deviceVerification = "/oauth2/device_verification";
        private String token = "/oauth2/token";
        private String tokenIntrospection = "/oauth2/introspect";
        private String tokenRevocation = "/oauth2/revoke";
        private String jwkSet = "/oauth2/jwks";
        private String oidcLogout = "/connect/logout";
        private String oidcUserInfo = "/userinfo";
        private String oidcClientRegistration = "/connect/register";

        // Getters and Setters for all endpoint properties
        public String getAuthorization() {
            return authorization;
        }

        public void setAuthorization(String authorization) {
            this.authorization = authorization;
        }

        public String getPushedAuthorizationRequest() {
            return pushedAuthorizationRequest;
        }

        public void setPushedAuthorizationRequest(String pushedAuthorizationRequest) {
            this.pushedAuthorizationRequest = pushedAuthorizationRequest;
        }

        public String getDeviceAuthorization() {
            return deviceAuthorization;
        }

        public void setDeviceAuthorization(String deviceAuthorization) {
            this.deviceAuthorization = deviceAuthorization;
        }

        public String getDeviceVerification() {
            return deviceVerification;
        }

        public void setDeviceVerification(String deviceVerification) {
            this.deviceVerification = deviceVerification;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getTokenIntrospection() {
            return tokenIntrospection;
        }

        public void setTokenIntrospection(String tokenIntrospection) {
            this.tokenIntrospection = tokenIntrospection;
        }

        public String getTokenRevocation() {
            return tokenRevocation;
        }

        public void setTokenRevocation(String tokenRevocation) {
            this.tokenRevocation = tokenRevocation;
        }

        public String getJwkSet() {
            return jwkSet;
        }

        public void setJwkSet(String jwkSet) {
            this.jwkSet = jwkSet;
        }

        public String getOidcLogout() {
            return oidcLogout;
        }

        public void setOidcLogout(String oidcLogout) {
            this.oidcLogout = oidcLogout;
        }

        public String getOidcUserInfo() {
            return oidcUserInfo;
        }

        public void setOidcUserInfo(String oidcUserInfo) {
            this.oidcUserInfo = oidcUserInfo;
        }

        public String getOidcClientRegistration() {
            return oidcClientRegistration;
        }

        public void setOidcClientRegistration(String oidcClientRegistration) {
            this.oidcClientRegistration = oidcClientRegistration;
        }
    }
}