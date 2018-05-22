package com.xzmc.airuishi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xzmc.airuishi.R;
import com.xzmc.airuishi.bean.PostModel;

public class CenterListAdp extends BaseAdapter {

	private DisplayImageOptions displayImageOptions;
	private LayoutInflater inflater;

	private List<PostModel> list;

	public CenterListAdp(List<PostModel> list, LayoutInflater inflater) {
		this.list = list;
		this.inflater = inflater;
		displayImageOptions = new DisplayImageOptions.Builder().cacheInMemory()
				.cacheOnDisc().showImageForEmptyUri(R.drawable.image_error)
				.showImageOnFail(R.drawable.image_error).build();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Containner holder;
		if (convertView == null) {
			holder = new Containner();
			convertView = inflater.inflate(R.layout.lay_post_lv_item, null);
			
			holder.item_layout = (RelativeLayout) convertView.findViewById(R.id.item_layout);
            holder.item_title = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.item_source = (TextView) convertView.findViewById(R.id.item_source);
            holder.publish_time = (TextView) convertView.findViewById(R.id.publish_time);
            holder.comment_count = (TextView) convertView.findViewById(R.id.comment_count);
            holder.right_image = (ImageView) convertView.findViewById(R.id.right_image);
            holder.item_image_layout = (LinearLayout) convertView.findViewById(R.id.item_image_layout);
            holder.item_image_0 = (ImageView) convertView.findViewById(R.id.item_image_0);
            holder.item_image_1 = (ImageView) convertView.findViewById(R.id.item_image_1);
            holder.item_image_2 = (ImageView) convertView.findViewById(R.id.item_image_2);
            convertView.setTag(holder);
		} else {
			holder = (Containner) convertView.getTag();
		}
		
		final PostModel post=list.get(position);
		holder.item_title.setText(post.getTitle());
		holder.publish_time.setText(post.getTime());
		holder.item_source.setText(post.getView_count());
		holder.comment_count.setText(post.getComment_count());
		// containner.ivRight
		String imgUrl = post.getImageurl();
		
		List<String> picList = new ArrayList<String>();

        String[] picArray = imgUrl.split(",");

        for (int i = 0; i < picArray.length; i++) {
            String pic = picArray[i];
            picList.add(pic);
        }

        if (picList != null && picList.size() != 0) {
            if (picList.size() == 1) {
                holder.item_image_layout.setVisibility(View.GONE);
                holder.right_image.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(picList.get(0),
                        holder.right_image, displayImageOptions);
            } else if (picList.size() == 2) {
                holder.right_image.setVisibility(View.GONE);
                holder.item_image_layout.setVisibility(View.VISIBLE);
                holder.item_image_2.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(picList.get(0),
                        holder.item_image_0, displayImageOptions);
                ImageLoader.getInstance().displayImage(picList.get(1),
                        holder.item_image_1, displayImageOptions);
            } else if (picList.size() == 3) {
                holder.right_image.setVisibility(View.GONE);
                holder.item_image_layout.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(picList.get(0),
                        holder.item_image_0, displayImageOptions);
                ImageLoader.getInstance().displayImage(picList.get(1),
                        holder.item_image_1, displayImageOptions);
                ImageLoader.getInstance().displayImage(picList.get(2),
                        holder.item_image_2, displayImageOptions);
            }
        } else {
            holder.right_image.setVisibility(View.GONE);
            holder.item_image_layout.setVisibility(View.GONE);
        }
        
        
        if(TextUtils.isEmpty(imgUrl)){
            holder.right_image.setVisibility(View.GONE);
            holder.item_image_layout.setVisibility(View.GONE);
        }

		return convertView;
	}

	public class Containner {
		 RelativeLayout item_layout;
	        //title
	        TextView item_title;
	        //来源
	        TextView item_source;
	        //评论数量
	        TextView comment_count;
	        //发布时间
	        TextView publish_time;
	        //右边图片
	        ImageView right_image;
	        //3张图片布局
	        LinearLayout item_image_layout; //3张图片时候的布局
	        ImageView item_image_0;
	        ImageView item_image_1;
	        ImageView item_image_2;
	        //大图的图片的话布局
	        //pop按钮
	        ImageView popicon;
	}
}
