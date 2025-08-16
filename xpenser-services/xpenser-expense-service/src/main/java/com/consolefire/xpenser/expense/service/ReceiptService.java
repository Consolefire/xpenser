package com.consolefire.xpenser.expense.service;

import com.consolefire.xpenser.expense.data.dto.request.ReceiptRequestDto;
import com.consolefire.xpenser.expense.data.dto.response.ReceiptCreateResponseDto;

public interface ReceiptService {

    ReceiptCreateResponseDto createReceipt(ReceiptRequestDto receiptRequestDto);

    ReceiptCreateResponseDto updateReceipt(ReceiptRequestDto receiptRequestDto);


}
