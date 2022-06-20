package com.example.websocketdemo.web.v1.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResponseDto {
    private String profileId;
    private String pageCode;
}
