package com.mysite.travelo.hyo.customcourse;

import com.mysite.travelo.hyo.place.Place;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CustomCourseRequest {

    private Course course;

    private String title;

    private String description;

    private int authorSeq;

    private String privateYn;

    // 데이터 전송량을 줄이기 위해서 순차번호만 전송.
    private List<Integer> placeSeqs;

}