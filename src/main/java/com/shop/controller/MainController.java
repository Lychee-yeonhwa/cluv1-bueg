package com.shop.controller;

import com.shop.dto.*;
import com.shop.entity.Category;
import com.shop.entity.UsedItem;
import com.shop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final CategoryService categoryService;

    private final BestItemService bestItemService;

    private final ReverseAuctionService reverseAuctionService;

    private final UsedItemService usedItemService;

    @GetMapping(value = "/")
    public String main(Model model) {
        Pageable pageable = PageRequest.of(0, 5);

        List<BestItemDto> bestItemList = bestItemService.getBestOfDayItem();

        Page<ReverseAuctionDto> reverseAuctionPages = reverseAuctionService.getUserReverseAuctionPage(new ReverseAuctionSearchDto(), pageable);
        List<ReverseAuctionDto> reverseAuctionList = reverseAuctionPages.getContent();

        Page<UsedItemDto> usedItemPages = usedItemService.getAllUsedItemPage(new UsedItemSearchDto(), pageable);
        List<UsedItemDto> usedItemList = usedItemPages.getContent();

        List<Category> categoryList = categoryService.getCategoryList();

        model.addAttribute("categoryList", categoryList);
        model.addAttribute("bestItemList", bestItemList);
        model.addAttribute("reverseAuctionList", reverseAuctionList);
        model.addAttribute("usedItemList", usedItemList);

        return "main";
    }

}
