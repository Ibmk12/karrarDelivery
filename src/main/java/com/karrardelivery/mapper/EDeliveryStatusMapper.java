package com.karrardelivery.mapper;

import com.karrardelivery.entity.enums.EDeliveryStatus;
import org.mapstruct.Named;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class EDeliveryStatusMapper {

    @Named("deliveryStatusToString")
    public String toString(EDeliveryStatus status) {
        if (status == null) return null;
        String lang = LocaleContextHolder.getLocale().getLanguage();
        return "ar".equals(lang) ? status.getArabic() : status.getEnglish();
    }

    @Named("stringToDeliveryStatus")
    public EDeliveryStatus toEnum(String value) {
        if (value == null) return null;
        for (EDeliveryStatus s : EDeliveryStatus.values()) {
            if (value.equalsIgnoreCase(s.getEnglish()) || value.equalsIgnoreCase(s.getArabic())) {
                return s;
            }
        }
        return null;
    }
}
