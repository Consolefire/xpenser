// src/clients/expense/ExpenseClient.ts

import { ReceiptRequestDto } from '../../application/types/ReceiptTypes';

/**
 * Defines the minimal data structure needed for the Receipts list view.
 * This is derived from the full ReceiptImportDto.
 */
export type SimpleReceiptDto = Pick<
    ReceiptRequestDto,
    'identifier' | 'timestamp' | 'marketPlaceCode' | 'storeCode' | 'total' | 'currency'
>;

// Use the environment variable, defaulting to http://localhost:9030 as requested
const BASE_URL = process.env.EXPENSE_SERVICE_BASE_URL || 'http://localhost:9030';
const API_URL = `${BASE_URL}/query/receipts`;

/**
 * Fetches the list of receipts from the expense service backend.
 * NOTE: This uses POST as specified, which is unusual for a GET operation.
 * It assumes the POST request returns the list of receipts in the response body.
 * @returns A promise that resolves to an array of SimpleReceiptDto.
 */
export async function fetchAllReceipts(): Promise<SimpleReceiptDto[]> {
    console.log(`Fetching receipts using from: ${API_URL}`);

    try {
        const response = await fetch(API_URL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                // Add Authorization header if needed
            },
            mode: 'no-cors',
        })
        ;

        if (!response.ok) {
        throw new Error(`Response status: ${response.status}`);
        }

        const result = await response.json();
        console.log(result);

        // Assume the response body is an array of receipts 
        // and that it matches the SimpleReceiptDto shape.
        const data = await response.json();
        console.log(data);
        return data;
        
    } catch (error) {
        console.error("Error fetching receipts:", error);
        throw error; 
    }
}
