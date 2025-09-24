package com.consolefire.xpenser.doc.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FileSizeTest {

    @Test
    void testOf_String_ValidFormats() {
        // Test various valid string formats
        assertEquals("100B", FileSize.of("100B").toString());
        assertEquals("12KB", FileSize.of("12Kb").toString());
        assertEquals("50MB", FileSize.of("50mb").toString());
        assertEquals("99GB", FileSize.of("99gb").toString());

        // Test with spaces and extra characters
        assertEquals("200MB", FileSize.of("200 MB").toString());
        assertEquals("1KB", FileSize.of("1.5 kB").toString());
    }

    @Test
    void testOf_String_InvalidFormats() {
        // Test invalid formats to ensure exceptions are thrown
        assertThrows(IllegalArgumentException.class, () -> FileSize.of("B100"));
        assertThrows(IllegalArgumentException.class, () -> FileSize.of("100"));
        assertThrows(IllegalArgumentException.class, () -> FileSize.of("100KB_"));
        assertThrows(IllegalArgumentException.class, () -> FileSize.of("invalid_format"));
    }

    @Test
    void testOf_String_NullAndEmpty() {
        // Test null and empty strings
        assertThrows(IllegalArgumentException.class, () -> FileSize.of(null));
        assertThrows(IllegalArgumentException.class, () -> FileSize.of(""));
        assertThrows(IllegalArgumentException.class, () -> FileSize.of("  "));
    }

    @Test
    void testOf_IntAndString_Valid() {
        // Test valid integer and string unit combinations
        assertEquals("100B", FileSize.of(100, "B").toString());
        assertEquals("50KB", FileSize.of(50, "kB").toString());
        assertEquals("10MB", FileSize.of(10, "mb").toString());
        assertEquals("1GB", FileSize.of(1, "GB").toString());
    }

    @Test
    void testOf_IntAndString_Invalid() {
        // Test invalid unit strings
        assertThrows(IllegalArgumentException.class, () -> FileSize.of(100, "TB"));
        assertThrows(IllegalArgumentException.class, () -> FileSize.of(100, "KB1"));
        assertThrows(IllegalArgumentException.class, () -> FileSize.of(100, "kilobytes"));
    }

    @Test
    void testToString() {
        // Test the toString() method directly
        FileSize sizeB = FileSize.of(10, "B");
        assertEquals("10B", sizeB.toString());

        FileSize sizeKB = FileSize.of(256, "KB");
        assertEquals("256KB", sizeKB.toString());
    }
}