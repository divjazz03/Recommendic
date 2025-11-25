package com.divjazz.recommendic;

import com.divjazz.recommendic.config.BaseTestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {BaseTestConfiguration.class})
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestContainersInitializer.class)
public abstract class BaseIntegrationTest {


}
