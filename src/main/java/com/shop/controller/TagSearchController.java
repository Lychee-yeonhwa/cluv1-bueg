package com.shop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Tag;
import com.shop.service.ItemService;
import com.shop.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TagSearchController {

    private final ItemService itemService;
    private final TagService tagService;

    @GetMapping(value = "/detailSearch")
    public String detailSearch(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model, @RequestParam(value = "filter", required = false) String filter){
        String[] filters = new String[] {};

        if(filter != null && !filter.equals("")) {
            filters = filter.split(",");
        }

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<MainItemDto> items = itemService.getDetailSearchPage(filters, itemSearchDto, pageable);

        model.addAttribute("filters", filters);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "tagSearch/dtlSearch";
    }

    @GetMapping(value = "/admin/showTagSell")
    public String showTagSell(Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Integer> graphData = new HashMap<>();

        List<Tag> tags = tagService.getTagList();

        for(Tag t : tags) {
            graphData.put(t.getTagNm(), t.getTotalSell());
        }

        String json = objectMapper.writeValueAsString(graphData);

        model.addAttribute("chartData", json);

        return "tagSearch/showSellDemo";
    }

}
