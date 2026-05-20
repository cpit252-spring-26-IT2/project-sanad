package sa.edu.kau.fcit.cpit252.project;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class ProjectSanadApplicationUnitTest {

    @Test
    void mainDelegatesToSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            ProjectSanadApplication.main(new String[]{"--spring.main.web-application-type=none"});
            mocked.verify(() -> SpringApplication.run(ProjectSanadApplication.class,
                    new String[]{"--spring.main.web-application-type=none"}));
        }
    }
}
