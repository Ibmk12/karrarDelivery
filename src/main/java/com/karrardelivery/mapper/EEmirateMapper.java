package com.karrardelivery.mapper;

import com.karrardelivery.entity.enums.EEmirate;
import org.mapstruct.Named;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class EEmirateMapper {

    @Named("emirateToString")
    public String toString(EEmirate emirate) {
        if (emirate == null) return null;
        String lang = LocaleContextHolder.getLocale().getLanguage();
        return "ar".equals(lang) ? emirate.getArabic() : emirate.getEnglish();
    }

    @Named("stringToEmirate")
    public EEmirate toEnum(String value) {
        if (value == null) return null;
        for (EEmirate e : EEmirate.values()) {
            if (value.equalsIgnoreCase(e.getEnglish()) || value.equalsIgnoreCase(e.getArabic())) {
                return e;
            }
        }
        return null;
    }
}
