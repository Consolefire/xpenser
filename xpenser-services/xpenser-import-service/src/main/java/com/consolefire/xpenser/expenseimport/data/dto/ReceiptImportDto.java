package com.consolefire.xpenser.expenseimport.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiptImportDto {

    private String identifier;
    private MetadataDto metadata;
    private String marketPlaceCode;
    private String storeCode;
    private String timestamp;
    private Double total;
    private String currency;
    private List<PaymentDto> payments;
    private TaxSectionDto tax;
    private List<ItemDto> items;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MetadataDto {

        private String countryCode;
        private String terminalNumber;
        private String storeNumber;
        private String serialNumber;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PaymentDto {

        private Double amount;
        private String mode;
        private String currency;
        private CardDto card;
        private CouponDto coupon;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CardDto {

        private String type;
        private String number;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CouponDto {

        private String type;
        private String identifier;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TaxSectionDto {

        private Double total;
        private List<TaxDto> taxes;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TaxDto {

        private String type;
        private TaxUnitDto taxUnit;
        private Double taxableAmount;
        private Double amount;
        private String itemCategory;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TaxUnitDto {

        private Double value;
        private String unit;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ItemDto {

        private String code;
        private String title;
        private Double quantity;
        private Double price;
        private String categoryCode;
        private UnitValueDto unitValue;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UnitValueDto {

        private String unitCode;
        private Double value;
    }
}
