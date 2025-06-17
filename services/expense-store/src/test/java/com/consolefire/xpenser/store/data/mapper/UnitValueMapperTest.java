package com.consolefire.xpenser.store.data.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.consolefire.xpenser.store.PostgresTestcontainersTest;
import com.consolefire.xpenser.store.data.UnitValue;
import com.consolefire.xpenser.store.data.dto.request.UnitValueRequestDto;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UnitValueMapperTest implements PostgresTestcontainersTest {

    @Autowired
    private UnitValueMapper unitValueMapper;


    @Test
    void shouldMapFromRequestToUnitValues() {
        final UnitValueRequestDto expectedRequestDto = UnitValueRequestDto.builder()
            .value(BigDecimal.valueOf(12.34))
            .unitCode("UNIT")
            .build();
        final UnitValue expectedUnitValue = UnitValue.builder()
            .value(BigDecimal.valueOf(12.34))
            .unitCode("UNIT")
            .build();
        UnitValue actualUnitValue = unitValueMapper.toUnitValue(expectedRequestDto);
        assertNotNull(actualUnitValue);
        assertAll(
            () -> assertEquals(expectedUnitValue.getValue(), actualUnitValue.getValue()),
            () -> assertEquals(expectedUnitValue.getUnitCode(), actualUnitValue.getUnitCode())
        );
    }

    @Test
    void shouldMapFromUnitValuesToRequest() {
        final UnitValueRequestDto expectedRequestDto = UnitValueRequestDto.builder()
            .value(BigDecimal.valueOf(12.34))
            .unitCode("UNIT")
            .build();
        final UnitValue expectedUnitValue = UnitValue.builder()
            .value(BigDecimal.valueOf(12.34))
            .unitCode("UNIT")
            .build();
        UnitValueRequestDto actualUnitValue = unitValueMapper.toRequestDto(expectedUnitValue);
        assertNotNull(actualUnitValue);
        assertAll(
            () -> assertEquals(expectedRequestDto.getValue(), actualUnitValue.getValue()),
            () -> assertEquals(expectedRequestDto.getUnitCode(), actualUnitValue.getUnitCode())
        );
    }
}