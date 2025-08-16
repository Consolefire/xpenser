package com.consolefire.xpenser.expense.service;

import com.consolefire.xpenser.expense.data.dto.request.ReceiptRequestDto;
import com.consolefire.xpenser.expense.data.dto.response.ReceiptResponseDto;

public interface ReceiptService {

    ReceiptResponseDto createReceipt(ReceiptRequestDto receiptRequestDto);

    ReceiptResponseDto updateReceipt(ReceiptRequestDto receiptRequestDto);


}
