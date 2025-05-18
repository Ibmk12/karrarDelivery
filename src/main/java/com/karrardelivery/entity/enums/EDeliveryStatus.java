package com.karrardelivery.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EDeliveryStatus {

    PENDING("Pending", "قيد الانتظار"),
    UNDER_DELIVERY("Under Delivery", "قيد التوصيل"),
    DELIVERED("Delivered", "تم التوصيل"),
    CANCELED("Canceled", "أُلغيت"),
    EXCHANGED("Exchanged", "تم الإرجاع"),
    FAILED("Failed", "فشل التوصيل");

    private final String english;
    private final String arabic;
}
