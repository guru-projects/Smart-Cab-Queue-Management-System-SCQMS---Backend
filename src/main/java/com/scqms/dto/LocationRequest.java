package com.scqms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationRequest {
    private Long cabId;
    private double latitude;
    private double longitude;
}
