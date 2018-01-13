/*
package com.wstro.virtuallocation.ui.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wstro.app.data.DataConstants;
import com.wstro.app.data.model.ContactsInfo;
import com.wstro.virtuallocation.data.model.AppInfo;
import com.wstro.wstrochatdemo.R;

import java.util.List;

import static com.baidu.location.d.j.R;

*/
/**
 * ClassName: ContactsAdapter
 * Function:
 * Date:     2017/9/9 0009 14:06
 *
 * @author Administrator
 * @see
 *//*

public class AppListAdapter extends BaseHeaderAdapter<PinnedHeaderEntity<AppInfo>>{

    public AppListAdapter(List<PinnedHeaderEntity<AppInfo>> data) {
        super(data);
    }

    @Override
    protected void addItemTypes() {
        addItemType(TYPE_HEADER, R.layout.list_contacts_item_header);
        addItemType(TYPE_DATA,R.layout.list_contacts_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, PinnedHeaderEntity<ContactsInfo> item) {
        switch (item.getItemType()){
            case TYPE_HEADER:
                helper.setText(R.id.tv_name,item.getPinnedHeaderName());
                break;
            case TYPE_DATA:
                helper.setText(R.id.tv_name,item.getData().getNickname());
                ImageView imageView = helper.getView(R.id.civ_image);
                loadImage(imageView,item.getData().getAvatar());
                break;
        }
    }

    private void loadImage(ImageView imageView, String avatar){
        Glide.with(mContext)
                .load(DataConstants.BASE_FILE_URL+avatar)
                .placeholder(R.mipmap.ic_user_head)
                .error(R.mipmap.ic_user_head)
                .into(imageView);
    }
}
*/
