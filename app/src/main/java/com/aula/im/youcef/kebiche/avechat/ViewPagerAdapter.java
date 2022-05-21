package com.aula.im.youcef.kebiche.avechat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;


public class ViewPagerAdapter extends PagerAdapter {

    Context context;


    int animations [] = {
            R.raw.createchat,
            R.raw.joinchat,
            R.raw.enjoychat
    };

    int des [] = {
      R.string.create_chat_ob ,
      R.string.join_chat_ob ,
      R.string.enjoy_chat_ob
    };


    public ViewPagerAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view  = layoutInflater.inflate(R.layout.slider , container , false);
        LottieAnimationView lottieSlide = (LottieAnimationView) view.findViewById(R.id.lottie_splash);
        TextView slideDesc = (TextView) view.findViewById(R.id.desText);

        lottieSlide.setAnimation(animations[position]);
        slideDesc.setText(des[position]);



        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }


}
