package com.shop.controller;

import com.shop.dto.NaverShopItemDto;
import com.shop.dto.UsedItemDto;
import com.shop.dto.UsedItemFormDto;
import com.shop.dto.UsedItemSearchDto;
import com.shop.service.NaverShopService;
import com.shop.service.UsedItemService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UsedItemController {

    private final UsedItemService usedItemService;
    private final NaverShopService naverShopService;

    @GetMapping(value = { "/uitems", "/uitems/{page}" })
    public String usedItemList(UsedItemSearchDto usedItemSearchDto, Optional<Integer> page, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<UsedItemDto> usedItemList = usedItemService.getAllUsedItemPage(usedItemSearchDto, pageable);

        model.addAttribute("usedItemList", usedItemList);
        model.addAttribute("usedItemSearchDto", usedItemSearchDto);
        model.addAttribute("maxPage", 5);

        return "usedItem/usedItemList";
    }

    @GetMapping(value = { "/uitem/manage", "/uitem/manage/{page}" })
    public String usedItemMng(UsedItemSearchDto usedItemSearchDto, Optional<Integer> page, Principal principal, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<UsedItemDto> usedItemList = usedItemService.getUserUsedItemPage(principal.getName(), usedItemSearchDto, pageable);

        model.addAttribute("usedItemList", usedItemList);
        model.addAttribute("usedItemSearchDto", usedItemSearchDto);
        model.addAttribute("maxPage", 5);

        return "usedItem/usedItemMng";
    }

    @GetMapping(value = "/uitem/new")
    public String usedItemForm(Model model) {
        model.addAttribute("usedItemFormDto", new UsedItemFormDto());

        return "usedItem/usedItemForm";
    }

    @PostMapping(value = "/uitem/new")
    public String usedItemNew(@Valid UsedItemFormDto usedItemFormDto, BindingResult bindingResult, @RequestParam("usedItemImgFile") List<MultipartFile> usedItemImgFileList, Principal principal, Model model) {
        if(bindingResult.hasErrors()) {
            return "usedItem/usedItemForm";
        }

        if(usedItemImgFileList.get(0).isEmpty() && usedItemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "????????? ?????? ???????????? ?????? ?????? ??? ?????????.");

            return "usedItem/usedItemForm";
        }

        String email = principal.getName();

        try {
            usedItemService.saveUsedItem(usedItemFormDto, usedItemImgFileList, email);
        } catch(Exception e) {
            model.addAttribute("errorMessage", "?????? ?????? ??? ????????? ?????????????????????.");

            return "usedItem/usedItemForm";
        }

        return "redirect:/uitems";
    }

    @GetMapping(value = "/uitem/update/{usedItemId}")
    public String usedItemUpdateForm(@PathVariable("usedItemId") Long usedItemId, Principal principal, Model model) {
        if(!usedItemService.validateUsedItem(usedItemId, principal.getName())) {
            model.addAttribute("message", "????????? ????????? ????????????.");
            model.addAttribute("location", "/");

            return "redirect";
        }

        try {
            UsedItemFormDto usedItemFormDto = usedItemService.getUsedItemDtl(usedItemId);

            model.addAttribute("usedItemFormDto", usedItemFormDto);
        } catch(EntityNotFoundException e) {
            model.addAttribute("errorMessage", "???????????? ?????? ?????? ?????? ?????????.");
            model.addAttribute("usedItemFormDto", new UsedItemFormDto());

            return "usedItem/usedItemForm";
        }

        return "usedItem/usedItemForm";
    }

    @PostMapping(value = "/uitem/update/{usedItemId}")
    public String usedItemUpdate(@Valid UsedItemFormDto usedItemFormDto, BindingResult bindingResult, @RequestParam("usedItemImgFile") List<MultipartFile> usedItemImgFileList, Principal principal, Model model) {
        if(!usedItemService.validateUsedItem(usedItemFormDto.getId(), principal.getName())) {
            model.addAttribute("message", "????????? ????????? ????????????.");
            model.addAttribute("location", "/");

            return "redirect";
        }

        if(usedItemImgFileList.get(0).isEmpty() && usedItemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "????????? ?????? ???????????? ?????? ?????? ??? ?????????.");

            return "usedItem/usedItemForm";
        }

        try {
            usedItemService.updateUsedItem(usedItemFormDto, usedItemImgFileList);
        } catch(Exception e) {
            model.addAttribute("errorMessage", "?????? ?????? ??? ????????? ?????????????????????.");

            return "usedItem/usedItemForm";
        }

        return "redirect:/uitem/manage";
    }

    @GetMapping(value = "/uitem/{usedItemId}")
    public String usedItemDtl(@PathVariable("usedItemId") Long usedItemId, Model model) {
        UsedItemFormDto usedItemFormDto = usedItemService.getUsedItemDtl(usedItemId);

        model.addAttribute("usedItem", usedItemFormDto);

        return "usedItem/usedItemDtl";
    }

    @GetMapping(value = "/uitem/naverShopItems")
    public @ResponseBody List<NaverShopItemDto> getMarketItems(@RequestParam("name") String name) {
        return naverShopService.search(name);
    }

}
