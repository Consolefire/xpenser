// receipt-import-module.ts

// Read the BASE URL from the environment variable
const API_BASE_URL_RECEIPT_IMPORT = process.env.REACT_APP_API_BASE_URL_RECEIPT_IMPORT || 'http://localhost:8080';
const API_ENDPOINT_PATH = '/import/receipts';
const FULL_API_URL = `${API_BASE_URL_RECEIPT_IMPORT}${API_ENDPOINT_PATH}`;

// Define the expected structure for the minimal payload
interface MinimalReceiptPayload {
    identifier: string;
    marketPlaceCode: string;
    storeCode: string;
    timestamp: string;
    total: number;
    currency: string;
}

/**
 * Submits the minimal receipt data to the backend API.
 * @param payload The JSON object containing mandatory receipt fields.
 * @returns A promise that resolves to the API response object.
 */
export async function postMinimalReceipt(payload: MinimalReceiptPayload): Promise<any> {
    console.log(`Submitting minimal receipt to: ${FULL_API_URL}`);
    console.log('Payload:', JSON.stringify(payload, null, 2));

    try {
        const response = await fetch(FULL_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // Add any necessary authentication headers here
            },
            body: JSON.stringify(payload),
        });

        // Throw an error if the HTTP status is not successful
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(`API submission failed: ${response.status} - ${errorBody}`);
        }

        // Return the parsed JSON response
        return await response.json();

    } catch (error) {
        // Re-throw the error to be handled by the calling component
        console.error('Network or API Request Error:', error);
        throw error;
    }
}