package com.wstro.virtuallocation.ui.adapter;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * ClassName: PersonModel
 * Function:
 * Date:     2017/8/25 15:03
 *
 * @author pengl
 * @see
 */
public class PinnedHeaderEntity<T> implements MultiItemEntity {

    private final int itemType;

    private T data;

    private String pinnedHeaderName;

    public PinnedHeaderEntity(T data, int itemType, String pinnedHeaderName) {
        this.data = data;
        this.itemType = itemType;
        this.pinnedHeaderName = pinnedHeaderName;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setPinnedHeaderName(String pinnedHeaderName) {
        this.pinnedHeaderName = pinnedHeaderName;
    }

    public T getData() {
        return data;
    }

    public String getPinnedHeaderName() {
        return pinnedHeaderName;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
