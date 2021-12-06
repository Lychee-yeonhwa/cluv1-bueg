package com.shop.dto;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

@Getter
@Setter
public class NaverApiDto {

    private String title;
    private int lprice;
    private String slink;

    public NaverApiDto(JSONObject itemJson) {
        this.title = itemJson.getString("title");
        this.lprice = itemJson.getInt("lprice");
        this.slink = itemJson.getString("link");
    }

}



