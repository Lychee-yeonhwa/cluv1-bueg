package com.shop.repository;

import com.shop.entity.UsedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsedItemRepository extends JpaRepository<UsedItem, Long>, QuerydslPredicateExecutor<UsedItem>, UsedItemRepositoryCustom{

    List<UsedItem> findByItemName(String itemName);

    List<UsedItem> findByItemNameOrDetail(String itemName, String detail);

    List<UsedItem> findByPriceLessThan(Integer price);

    List<UsedItem> findByPriceLessThanOrderByPriceDesc(Integer price);

    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<UsedItem> findByItemDetail(@Param("itemDetail") String itemDetail);

    @Query(value = "select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<UsedItem> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
}