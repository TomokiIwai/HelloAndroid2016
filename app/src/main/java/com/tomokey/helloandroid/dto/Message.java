package com.tomokey.helloandroid.dto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * メッセージデータクラス
 */
@Getter
@Setter
@AllArgsConstructor
public class Message {
    public Integer id;
    public String name;
    public String message;
    public String image;

    /**
     * イメージデータをBitmapとして取得します。
     *
     * @return {@link Bitmap}
     */
    public Bitmap getImageAsBitmap() {
        if (TextUtils.isEmpty(image)) {
            return null;
        }

        try {
            byte[] data = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception ignore) {
            return null;
        }
    }
}
