package com.mysite.travelo.hyo.customcourse;

import com.mysite.travelo.hyo.place.Place;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CustomCourseRequest {

    @NotEmpty
    private String title;

    @NotEmpty
    private String description;

    @NotNull
    private int authorSeq;

    @NotEmpty
    private Map<String, Place> placeMap;

    @NotEmpty
    private String privateYn;

    @Getter
    @Setter
    public static class Place {
        private Integer placeSeq;
        private Double longitude;
        private Double latitude;
    }


}