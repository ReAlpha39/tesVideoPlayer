package id.nyaa.tesvideoplayerii.ui.title;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import id.nyaa.tesvideoplayerii.R;

public class TitleBuilder {

    private TextView left_textview;
    private ImageView left_imageview;
    private TextView middle_textview;
    private Activity mActivity;

    /**
     * The first method of initialization
     * The initialization method of directly referencing the file in the layout
     * @param context
     */
    public TitleBuilder(Activity context) {
        mActivity = context;
        left_textview = context.findViewById(R.id.title_left_textview);
        left_imageview = context.findViewById(R.id.title_left_imageview);
        middle_textview = context.findViewById(R.id.title_middle_textview);
    }

    /**
     * Set the text of the middle text
     *
     * @param text
     * @return
     */
    public TitleBuilder setMiddleTitleText(String text) {
        middle_textview.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        middle_textview.setText(text);
        return this;
    }


    /**
     * Set resources for the picture on the left
     * @param useDftLeftImage Whether to use the default image resource
     * @param resId
     * @return
     */
    public TitleBuilder setLeftImageRes(boolean useDftLeftImage, int resId) {
        if(useDftLeftImage){
            left_imageview.setVisibility(View.VISIBLE);
            return this;
        }
        left_imageview.setVisibility(resId > 0 ? View.VISIBLE : View.GONE);
        left_imageview.setImageResource(resId);
        return this;
    }

    /**
     * Set the click event of the text or picture on the left
     * @param useDftListener Whether to use the default listening event
     * @param listener
     * @return
     */
    public TitleBuilder setLeftTextOrImageListener(boolean useDftListener, View.OnClickListener listener) {
        if(useDftListener){
            left_imageview.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mActivity.finish();
                }
            });
            return this;
        }
        if (left_imageview.getVisibility() == View.VISIBLE) {
            left_imageview.setOnClickListener(listener);
        } else if (left_textview.getVisibility() == View.VISIBLE) {
            left_textview.setOnClickListener(listener);
        }
        return this;
    }
}
