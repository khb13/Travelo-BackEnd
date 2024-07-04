package com.mysite.travelo.hyo.place;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

/* *
 * 장소 컨트롤러
 * list : 장소 전체 리스트를 가져오는 기능
 * url 형식 : /place/list?page=&keyword=&sorts=&content=&area=
 *
 * distance : 거리 기준으로 장소 리스트를 가져오는 기능
 * url 형식 : /place/distance?page=&keyword=&contentId=&distance=&content=
 */

@RequiredArgsConstructor
@RequestMapping("/place")
@Controller
public class PlaceController {

    private final PlaceService placeService;

    // 기본 장소 리스트를 가져오는 기능
    // url 형식 : /place/list?page=&keyword=&sorts=&content=&area=
    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value="page", defaultValue = "0") int page,
                       @RequestParam(value = "keyword", defaultValue = "") String keyword,
                       @RequestParam(value="sorts", defaultValue = "") String sort,
                       @RequestParam(value= "content", defaultValue="") String content,
                       @RequestParam(value= "area", defaultValue="") String area
    ){

        System.out.println("sorts" + sort);

        List<String> content_list = List.of();
        List<String> area_list = List.of();

        if (!content.isEmpty() && content != null) {
            content_list = Arrays.asList(content.split(","));
        }
        if (!area.isEmpty() && area != null) {
            area_list = Arrays.asList(area.split(","));
        }

        Page<Place> paging = this.placeService.getPage(page,keyword,sort, content_list, area_list);
        model.addAttribute("paging", paging);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("content", content_list);
        model.addAttribute("areas", area_list);

        return "place/list";
    }

    // 거리 기반으로 장소 리스트를 가져오는 기능
    // url 형식 : /place/distance?page=&keyword=&contentId=&distance=&content=
    @GetMapping("/distance")
    public String distance(Model model,
                           @RequestParam(value="page", defaultValue = "0") int page,
                           @RequestParam(value = "keyword", defaultValue = "") String keyword,
                           @RequestParam(value = "contentId", defaultValue = "2733967") String contentId,
                           @RequestParam(value="distance", defaultValue = "20.0") double distance,
                           @RequestParam(value = "content", defaultValue = "") String content)
    {

        Place place = placeService.getPlaceByContentId(contentId);

        double longitude = 126.984662;
        double latitude = 37.582086;

        if (place != null) {
            longitude = place.getLongitude();
            latitude = place.getLatitude();
        } else {
            model.addAttribute("error", "Place not found");
            return "place/distance";
        }

        List<String> content_list = List.of();

        if (!content.isEmpty() && content != null) {
            content_list = Arrays.asList(content.split(","));
        }

        Page<Place> paging = this.placeService.getPage(page, keyword, latitude, longitude, distance, content_list);
        model.addAttribute("paging", paging);
        model.addAttribute("keyword", keyword);
        model.addAttribute("longitude", longitude);
        model.addAttribute("latitude", latitude);
        model.addAttribute("distance", distance);
        model.addAttribute("content", content_list);

        return "place/distance";
    }




}
