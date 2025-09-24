package com.consolefire.xpenser.cloud.user.config.props;

import java.time.Duration;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

@Getter
@Setter
@ConfigurationProperties(prefix = "commons")
public class CommonApplicationProperties {

    public static final String KEY_POSTGRESQL = "postgresql";

    private Infra infra;

    @Getter
    @Setter
    public static class Infra {

        private Map<String, Database> database;

        public Database postgresql() {
            return database.get(KEY_POSTGRESQL);
        }
    }

    @Getter
    @Setter
    public static class Database {

        private String databaseName;
        private String driverClassName;
        private String host;
        private int port;
        private String username;
        private String password;
        private String currentSchema;
        @Nullable
        private Map<String, String> driverOptions;
        private ConnectionPool pool;
    }

    @Getter
    @Setter
    public static class ConnectionPool {

        private String name;
        private int maxSize;
        private int initialSize;
        private Duration maxIdleTime;
        private String validationQuery;
    }
}
