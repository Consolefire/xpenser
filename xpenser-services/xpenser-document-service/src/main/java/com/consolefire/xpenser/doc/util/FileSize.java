package com.consolefire.xpenser.doc.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSize {

    private static final String REGEX = "^\\d+.*[KMG]?B$";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

    public enum Unit {
        B, KB, MB, GB
    }

    private final long length;
    private final Unit unit;

    private FileSize(long length, Unit unit) {
        this.length = length;
        this.unit = unit;
    }

    @JsonCreator
    public static FileSize of(@JsonProperty(value = "size") String size) {
        if (size == null || size.trim().isEmpty()) {
            throw new IllegalArgumentException("Size string cannot be null or empty.");
        }

        Matcher matcher = PATTERN.matcher(size.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid file size format.");
        }

        String unitStr = size.replaceAll("[\\d\\s.,]+", "").trim().toUpperCase();
        String lengthStr = size.replaceAll("[A-Za-z]+", "").trim();

        long length = (long) Double.parseDouble(lengthStr);
        Unit unit = Unit.valueOf(unitStr);

        return new FileSize(length, unit);
    }

    public static FileSize of(int size, String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            throw new IllegalArgumentException("Unit string cannot be null or empty.");
        }

        try {
            Unit parsedUnit = Unit.valueOf(unit.trim().toUpperCase());
            return new FileSize(size, parsedUnit);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid file unit: " + unit, e);
        }
    }

    @Override
    @JsonValue
    public String toString() {
        return this.length + this.unit.name();
    }
}