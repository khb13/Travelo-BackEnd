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
 * list : 전체 리스트를 가져오는 기능
 */

@RequiredArgsConstructor
@RequestMapping("/place")
@Controller
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value="page", defaultValue = "0") int page,
                       @RequestParam(value = "keyword", defaultValue = "") String keyword,
                       @RequestParam(value="sorts", defaultValue = "") String sort,
                       @RequestParam(value= "content", defaultValue="") String content,
                       @RequestParam(value= "area", defaultValue="") String area
    ){

        // url 형식 : /place/list?page=&keyword=&sorts=&content=&area=
        
        List<String> content_list = List.of();
        List<String> area_list = List.of();

        if (!content.isEmpty() && content != null){
            content_list = Arrays.asList(content.split(","));
        }
        if (!area.isEmpty() && area != null){
            area_list = Arrays.asList(area.split(","));
        }

        System.out.println("카테고리:"+content_list);

        Page<Place> paging = this.placeService.getPage(page,keyword,sort, content_list, area_list);
        model.addAttribute("paging", paging);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("categories", content_list);
        model.addAttribute("areas", area_list);

        return "place/list";
    }




}
