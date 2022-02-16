package com.example.orderfood.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.orderfood.Model.ItemSlide;
import com.example.orderfood.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SlideAdapter extends SliderViewAdapter<SlideAdapter.SliderViewHolder>{
    private Context mContext;
    private List<ItemSlide> listSlider;

    public SlideAdapter(Context mContext, List<ItemSlide> listSlider) {
        this.mContext = mContext;
        this.listSlider = listSlider;
    }

    @Override
    public SliderViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slide_image,parent,false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SliderViewHolder viewHolder, int position) {

            ItemSlide item = listSlider.get(position);
            if (item == null)
            {
                return;
            }
            else
            {
                viewHolder.img.setImageResource(item.getResourceID());
            }
    }

    @Override
    public int getCount() {
        return listSlider.size();
    }

    public class SliderViewHolder extends SliderViewAdapter.ViewHolder {
        ImageView img;
        public SliderViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.item_image_slider);
        }
    }

}