package com.karrardelivery.common.utility;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BeanUtilsHelper {

    public static void copyNonNullProperties(Object source, Object target) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }

        String[] result = new String[emptyNames.size()];
        BeanUtils.copyProperties(source, target, emptyNames.toArray(result));
    }

    public static <T extends Enum<T>> T fromString(Class<T> enumType, String value) {
        return fromString(enumType, value, true);
    }

    public static <T extends Enum<T>> T fromString(Class<T> enumType, String value, boolean ignoreCase) {
        if (value == null || enumType == null) {
            return null;
        }

        for (T constant : enumType.getEnumConstants()) {
            if (ignoreCase) {
                if (constant.name().equalsIgnoreCase(value)) {
                    return constant;
                }
            } else {
                if (constant.name().equals(value)) {
                    return constant;
                }
            }
        }

        return null;
    }

    public static String getLocalizedEnumLabel(Enum<?> enumValue) {
        if (enumValue == null) return null;

        Locale locale = LocaleContextHolder.getLocale();
        String methodName = "getEnglish"; // Default

        if ("ar".equalsIgnoreCase(locale.getLanguage())) {
            methodName = "getArabic";
        }

        try {
            Method method = enumValue.getClass().getMethod(methodName);
            return (String) method.invoke(enumValue);
        } catch (Exception e) {
            // Log the issue and fallback to enum name
            return enumValue.name();
        }
    }

    public static LocalDateTime[] getDeliveryDateRange(HttpServletRequest request) {
        String deliveryDateParam = request.getParameter("fromDeliveryDate");
        LocalDate baseDate;

        try {
            baseDate = (deliveryDateParam != null && !deliveryDateParam.isBlank())
                    ? LocalDate.parse(deliveryDateParam.trim())
                    : LocalDate.now().minusDays(1);
        } catch (Exception e) {
            baseDate = LocalDate.now().minusDays(1);
        }

        LocalDateTime startOfDay = baseDate.atStartOfDay();
        LocalDateTime endOfDay = baseDate.plusDays(1).atStartOfDay().minusNanos(1);

        return new LocalDateTime[]{startOfDay, endOfDay};
    }

}