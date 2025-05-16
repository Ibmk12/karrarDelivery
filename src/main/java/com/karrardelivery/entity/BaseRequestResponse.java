package com.karrardelivery.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseRequestResponse {

    private Integer responseCode;
    private String responseMessage;
    private String status;
    private Integer paymentGatewayResponseCode;
    private String paymentGatewayResponseMessage;
    private String transactionStatus;

}
