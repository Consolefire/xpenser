// interfaces.ts

export interface UnitValueDto {
  unitCode: string;
  value: number;
}

export interface ItemDto {
  code: string;
  title: string;
  quantity: number;
  price: number;
  categoryCode: string;
  unitValue: UnitValueDto;
}

export interface TaxUnitDto {
  value: number;
  unit: string;
}

export interface TaxDto {
  type: string;
  taxUnit: TaxUnitDto;
  taxableAmount: number;
  amount: number;
  itemCategory: string;
}

export interface TaxSectionDto {
  total: number;
  taxes: TaxDto[];
}

export interface CardDto {
  type: string;
  number: string;
}

export interface CouponDto {
  type: string;
  identifier: string;
}

export interface PaymentDto {
  amount: number;
  mode: 'CARD' | 'CASH' | 'COUPON' | 'OTHER'; // Enumerate possible modes
  currency: string;
  card: CardDto | null;
  coupon: CouponDto | null;
}

export interface MetadataDto {
  countryCode: string;
  terminalNumber: string;
  storeNumber: string;
  serialNumber: string;
}

// The main POJO structure
export interface ReceiptImportDto {
  identifier: string; // Mandatory
  metadata: MetadataDto;
  marketPlaceCode: string; // Mandatory
  storeCode: string; // Mandatory
  timestamp: string; // Mandatory
  total: number; // Mandatory
  currency: string; // Mandatory (default EUR)
  payments: PaymentDto[];
  tax: TaxSectionDto;
  items: ItemDto[];
}