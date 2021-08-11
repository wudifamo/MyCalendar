package com.k.calendar;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

public class SpanUtil {

    public static void protocol(CheckBox checkBox, String title, String protocol) {
        SpannableString ss = new SpannableString(title + "《" + protocol + "》");

        ss.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //点击的响应事件

            }
        }, title.length(), title.length() + protocol.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        checkBox.setText(ss);
        checkBox.setMovementMethod(LinkMovementMethod.getInstance());

    }

    public static SpannableString spanSize(int size, String content, String sizeText) {
        SpannableString ss = new SpannableString(content + sizeText);
        ss.setSpan(new AbsoluteSizeSpan(size, true), content.length(), content.length() + sizeText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static SpannableStringBuilder sizeAndColorSpan(int textSize, int color, String container, String... children) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(container);
        for (String child : children) {
            int start = container.indexOf(child);
            int end = start + child.length();
            spannableStringBuilder.setSpan(new AbsoluteSizeSpan(textSize, true), start, end, 33);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(color), start, end, 33);
        }
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder colorSpan(int color, String container, String... children) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(container);
        for (String child : children) {
            int start = container.indexOf(child);
            int end = start + child.length();
            spannableStringBuilder.setSpan(new ForegroundColorSpan(color), start, end, 33);
        }
        return spannableStringBuilder;
    }

    public static void moneyUnitSpan(Context context, String money, EditText editText) {
        editText.setText(colorSpan(ContextCompat.getColor(context, R.color.colorPrimary), money + "元", money));
    }

    public static SpannableString wanSpan(String text) {
        return spanSize(13, text + " ", "万");
    }
}
