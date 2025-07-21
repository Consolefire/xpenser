package com.consolefire.xpenser.store.data.mapper;

import com.consolefire.xpenser.store.data.dto.request.ReceiptRequestDto;
import com.consolefire.xpenser.store.data.dto.response.ReceiptResponseDto;
import com.consolefire.xpenser.store.data.entity.ReceiptEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    uses = {PaymentMapper.class, ItemMapper.class}
)
public interface ReceiptMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "generatedAt", source = "timestamp") // DTO timestamp -> Entity generatedAt
    @Mapping(target = "metadata", source = "metadata") // Map metadata
    @Mapping(target = "total", source = "total") // Map total directly
    @Mapping(target = "currency", source = "currency") // Map currency directly
    @Mapping(target = "payment", source = "payment") // Map payment entity
    @Mapping(target = "items", source = "items")
        // Map items list
        // paymentMode from previous entity is no longer needed as PaymentEntity exists
    ReceiptEntity toEntity(ReceiptRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "generatedAt", source = "timestamp")
    @Mapping(target = "metadata", source = "metadata")
    @Mapping(target = "total", source = "total")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "payment", source = "payment")
    @Mapping(target = "items", source = "items")
    void updateEntityFromDto(ReceiptRequestDto dto, @MappingTarget ReceiptEntity entity);

    @AfterMapping
    default void setRelationships(@MappingTarget ReceiptEntity receiptEntity) {
        // Set bidirectional relationship for Payment
        if (receiptEntity.getPayment() != null) {
            receiptEntity.getPayment().setReceipt(receiptEntity);
        }

        // Set bidirectional relationship for Items
        if (receiptEntity.getItems() != null) {
            receiptEntity.getItems().forEach(item -> item.setReceipt(receiptEntity));
        }
    }

    // --- New Mapping from Entity to Response DTO ---
    @Mapping(target = "receiptId", source = "id")
    @Mapping(target = "identifier", source = "identifier")
    @Mapping(target = "createdAt", source = "createdAt")
    ReceiptResponseDto toResponseDto(ReceiptEntity entity);
}