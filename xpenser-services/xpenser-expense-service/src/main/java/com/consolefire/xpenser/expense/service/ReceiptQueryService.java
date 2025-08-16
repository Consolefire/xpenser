package com.consolefire.xpenser.expense.service;

import com.consolefire.xpenser.expense.data.dto.query.QueryRequestDto;
import com.consolefire.xpenser.expense.data.dto.query.ReceiptQueryProperties;
import com.consolefire.xpenser.expense.data.dto.response.ReceiptResponseDto;
import java.util.List;
import java.util.Optional;

public interface ReceiptQueryService extends QueryService<ReceiptQueryProperties, ReceiptResponseDto> {

    Optional<ReceiptResponseDto> fetchWithIdentifier(String marketPlaceCode, String storeCode, String identifier);

    List<ReceiptResponseDto> fetchAllReceipts(QueryRequestDto queryRequestDto);

}
