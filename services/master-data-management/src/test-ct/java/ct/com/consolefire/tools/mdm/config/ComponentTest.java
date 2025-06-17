package ct.com.consolefire.tools.mdm.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@SpringBootConfiguration
@ComponentScan(basePackages = {
    "com.consolefire.tools.mdm",
    "ct.com.consolefire.tools.mdm"
})
@ActiveProfiles
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentTest {

    @AliasFor(annotation = ActiveProfiles.class, attribute = "profiles") String[] activeProfiles()
        default {"component-tests"};
}
