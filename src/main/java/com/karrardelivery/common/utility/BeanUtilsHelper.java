package com.karrardelivery.common.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
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
}