package com.karrardelivery.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EDeliveryStatus {

    PENDING("Pending", "قيد الانتظار"),
    IN_TRANSIT("In Transit", "قيد التوصيل"),
    DELIVERED("Delivered", "تم التوصيل"),
    CANCELED("Canceled", "أُلغيت"),
    RETURNED("Returned", "تم الإرجاع"),
    FAILED("Failed", "فشل التوصيل");

    private final String english;
    private final String arabic;
}
