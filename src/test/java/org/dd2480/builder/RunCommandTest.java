package org.dd2480.builder;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.dd2480.builder.Builder.runCommand;
import static org.junit.jupiter.api.Assertions.*;

class RunCommandTest {

    @Test
    void shouldReturnFalse_whenInvalidDirectory() {
        List<String> output = new ArrayList<>();
        boolean result = runCommand("echo Test", "invalidDir", output);

        assertFalse(result, "Command should fail due to invalid directory");
        assertFalse(output.isEmpty(), "Output should contain error messages");
    }
    @Test
    void shouldReturnFalse_whenNullInput(){
        List<String> output = new ArrayList<>();
        boolean result = runCommand(null, ".", output);

        assertFalse(result, "Command should fail due to null input");
        assertFalse(output.isEmpty(), "Output should contain exception message");
    }
    @Test
    void shouldReturnFalse_whenNonExistentCommandGiven(){
        List<String> output = new ArrayList<>();
        boolean result = runCommand("nonexistentcommand", ".", output);

        assertFalse(result, "Command should fail");
        assertFalse(output.isEmpty(), "Output should contain error messages");
    }
    @Test
    void shouldReturnTrue_whenRightCommandGiven(){
        List<String> output = new ArrayList<>();
        boolean result = runCommand("echo Hello, World!", ".", output);

        assertTrue(result, "Command should execute successfully");
        assertFalse(output.isEmpty(), "Output should not be empty");
        assertEquals("Hello, World!", output.get(0).trim(), "Output should match expected text");
    }
}