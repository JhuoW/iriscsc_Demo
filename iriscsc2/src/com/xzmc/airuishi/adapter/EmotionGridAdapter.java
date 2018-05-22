package com.xzmc.airuishi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xzmc.airuishi.R;
import com.xzmc.airuishi.utils.EmotionUtils;
import com.xzmc.airuishi.view.ViewHolder;

/**
 * 表情适配�?
 */
public class EmotionGridAdapter extends ChatBaseListAdapter<String> {

  public EmotionGridAdapter(Context ctx) {
    super(ctx);
  }

  @Override
  public View getView(int position, View conView, ViewGroup parent) {
    if (conView == null) {
      LayoutInflater inflater = LayoutInflater.from(ctx);
      conView = inflater.inflate(R.layout.chat_emotion_item, null);
    }
    ImageView emotionImageView = ViewHolder.findViewById(conView, R.id.emotionImageView);
    String emotion = (String) getItem(position);
    emotion = emotion.substring(1);
    Bitmap bitmap= EmotionUtils.getDrawableByName(ctx,emotion);
    emotionImageView.setImageBitmap(bitmap);
    return conView;
  }
}
