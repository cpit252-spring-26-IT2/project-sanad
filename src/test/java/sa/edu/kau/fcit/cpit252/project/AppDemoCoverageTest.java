package sa.edu.kau.fcit.cpit252.project;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class AppDemoCoverageTest {

    @Test
    void appMainRunsAndPrintsKeySections() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        try {
            App.main(new String[0]);
        } finally {
            System.setOut(originalOut);
        }

        String text = output.toString(StandardCharsets.UTF_8);
        assertThat(text).contains("Welcome to the demo of the project!");
        assertThat(text).contains("Strictly testing composite design pattern");
        assertThat(text).contains("Chain of Responsibility Demo - Advanced Filtering System");
        assertThat(text).contains("Reviews System Demo - 5-Star Ratings");
    }
}
