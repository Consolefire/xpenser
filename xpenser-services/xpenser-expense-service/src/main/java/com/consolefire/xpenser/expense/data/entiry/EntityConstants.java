package com.consolefire.xpenser.expense.data.entiry;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EntityConstants {

    public static final String TABLE_NAME_ITEMS = "items";
    public static final String SEQ_NAME_ITEMS = "seq_items_id";

    public static final String TABLE_NAME_PAYMENTS = "payments";
    public static final String SEQ_NAME_PAYMENTS = "seq_payments_id";

    public static final String TABLE_NAME_RECEIPTS = "receipts";
    public static final String SEQ_NAME_RECEIPTS = "seq_receipts_id";

    public static final String TABLE_NAME_TAXES = "taxes";
    public static final String SEQ_NAME_TAXES = "seq_taxes_id";

    public static final String TABLE_NAME_TAX_DETAILS = "tax_details";
    public static final String SEQ_NAME_TAX_DETAILS = "seq_tax_details_id";

    public static final int SEQ_DEFAULT_ALLOCATION_SIZE = 100;

}
