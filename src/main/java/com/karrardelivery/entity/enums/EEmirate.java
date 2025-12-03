package com.karrardelivery.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EEmirate {

    ABU_DHABI("Abu Dhabi", "أبو ظبي"),
    DUBAI("Dubai", "دبي"),
    SHARJAH("Sharjah", "الشارقة"),
    AJMAN("Ajman", "عجمان"),
    UMM_AL_QUWAIN("Umm Al Quwain", "أم القيوين"),
    RAS_AL_KHAIMAH("Ras Al Khaimah", "رأس الخيمة"),
    FUJAIRAH("Fujairah", "الفجيرة"),
    AL_AIN("Al Ain", "العين"),
    WESTERN("Western Emirates", "الغربية");

    private final String english;
    private final String arabic;
}
