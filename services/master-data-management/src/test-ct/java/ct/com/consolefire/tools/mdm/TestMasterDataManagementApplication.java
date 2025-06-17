package ct.com.consolefire.tools.mdm;

import com.consolefire.tools.mdm.MasterDataManagementApplication;
import ct.com.consolefire.tools.mdm.config.TestProfileConfiguration;
import ct.com.consolefire.tools.mdm.config.TestcontainersConfiguration;
import org.springframework.boot.SpringApplication;

public class TestMasterDataManagementApplication {

    public static void main(String[] args) {
        SpringApplication.from(MasterDataManagementApplication::main)
            .with(TestProfileConfiguration.class)
            .with(TestcontainersConfiguration.class)
            .run(args);
    }

}
