package com.shop.service;

import com.shop.dto.*;
import com.shop.entity.*;
import com.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;
    private final ItemTagRepository itemTagRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        Category category = categoryRepository.findByCateCode(itemFormDto.getCateCode());

        Item item = Item.createItem(itemFormDto, category);

        for(int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if(i == 0) {
                itemImg.setRepImgYn("Y");
            } else {
                itemImg.setRepImgYn("N");
            }

            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        for (Long tagId : itemFormDto.getTagIds()) {
            Tag tag = tagRepository.findById(tagId).orElseThrow(EntityNotFoundException::new);

            ItemTag itemTag = new ItemTag();
            itemTag.setItem(item);
            itemTag.setTag(tag);

            itemTagRepository.save(itemTag);
        }

        itemRepository.save(item);

        return item.getId();
    }

    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {
        List<ItemTag> itemTagList = itemTagRepository.findByItemId(itemId);
        List<Long> tagIdList = new ArrayList<>();

        for (ItemTag itemTag : itemTagList) {
            Tag tag = itemTag.getTag();

            tagIdList.add(tag.getId());
        }

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();

        for(ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);

            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);

        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setCateCode(item.getCategory().getCateCode());
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        itemFormDto.setTagIds(tagIdList);

        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);

        Category category = categoryRepository.findByCateCode(itemFormDto.getCateCode());

        item.updateItem(itemFormDto, category);

        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        for(int i = 0; i < itemImgFileList.size(); i++) {
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }

        List<ItemTag> savedItemTag = itemTagRepository.findByItemId(item.getId());

        itemTagRepository.deleteAll(savedItemTag);

        for(Long tagIds : itemFormDto.getTagIds()) {
            Tag tag = tagRepository.findById(tagIds).orElseThrow(EntityNotFoundException::new);

            ItemTag itemTag = new ItemTag();
            itemTag.setItem(item);
            itemTag.setTag(tag);

            itemTagRepository.save(itemTag);
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }

    // 선물하기 상품 데이터
    @Transactional(readOnly = true)
    public Page<GiftMainItemDto> getGiftItemPage(ItemSearchDto itemSearchDto, Pageable pageable, Long cateCode) {
        return itemRepository.getGiftItemPage(itemSearchDto, pageable, cateCode);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getDetailSearchPage(String[] filters, ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getDetailSearchPage(filters, itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getComplexSearchPage(ItemComplexSearchDto itemComplexSearchDto, Pageable pageable) {
        return itemRepository.getComplexSearchPage(itemComplexSearchDto, pageable);
    }

}
