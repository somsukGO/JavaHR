package com.laoapps.utils;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Paging {

    private JsonObject data;

    public boolean isValid() {
        if (!data.has(Naming.page) || !data.has(Naming.limit) || !data.has(Naming.keyword)) {
            return false;
        }
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getKeywordId() {
        return keywordId;
    }
}
