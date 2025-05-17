package com.karrardelivery.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ErrorListDto {

    private List<ErrorDto> errorDtoList = new ArrayList<>();
}
