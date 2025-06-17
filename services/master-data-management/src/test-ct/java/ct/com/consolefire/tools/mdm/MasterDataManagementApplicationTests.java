package ct.com.consolefire.tools.mdm;

import ct.com.consolefire.tools.mdm.config.ComponentTest;
import ct.com.consolefire.tools.mdm.config.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;


@ComponentTest
@Import(TestcontainersConfiguration.class)
class MasterDataManagementApplicationTests {

	@Test
	void contextLoads() {
	}

}
