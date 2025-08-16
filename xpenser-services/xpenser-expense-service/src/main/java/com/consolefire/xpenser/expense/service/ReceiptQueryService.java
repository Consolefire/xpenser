package com.consolefire.xpenser.expense.service;

import com.consolefire.xpenser.expense.data.dto.query.QueryRequestDto;
import com.consolefire.xpenser.expense.data.dto.query.ReceiptQueryProperties;
import com.consolefire.xpenser.expense.data.dto.response.ReceiptDetailResponseDto;
import java.util.List;
import java.util.Optional;

public interface ReceiptQueryService extends QueryService<ReceiptQueryProperties, ReceiptDetailResponseDto> {

    Optional<ReceiptDetailResponseDto> fetchWithIdentifier(String marketPlaceCode, String storeCode, String identifier);

    List<ReceiptDetailResponseDto> fetchAllReceipts(QueryRequestDto queryRequestDto);

}
