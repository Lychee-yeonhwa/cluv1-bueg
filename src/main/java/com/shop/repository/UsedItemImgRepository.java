package com.shop.repository;

import com.shop.entity.UsedItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsedItemImgRepository extends JpaRepository<UsedItemImg, Long> {

    List<UsedItemImg> findByUsedItemIdOrderByIdAsc(Long usedItemId);

}
