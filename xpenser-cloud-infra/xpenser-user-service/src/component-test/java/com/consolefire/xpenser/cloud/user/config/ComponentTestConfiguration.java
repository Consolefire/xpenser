package com.consolefire.xpenser.cloud.user.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration(proxyBeanMethods = false)
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false"
})
public class ComponentTestConfiguration {

}
