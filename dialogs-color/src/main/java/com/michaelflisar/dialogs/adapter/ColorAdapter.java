package com.michaelflisar.dialogs.adapter;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.michaelflisar.dialogs.color.R;
import com.michaelflisar.dialogs.utils.ColorUtil;

public class ColorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private ColorUtil.GroupedColor mGroupedColor;
    private IColorClickedListener mListener;

    public ColorAdapter(ColorUtil.GroupedColor groupedColor, IColorClickedListener listener)
    {
        mListener = listener;
        mGroupedColor = groupedColor;
    }

    public void setGroupColor(ColorUtil.GroupedColor groupedColor)
    {
        mGroupedColor = groupedColor;
        notifyDataSetChanged();
//        notifyItemRangeChanged(0, groupedColor.getColors().length);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ColorViewHolder(inflater.inflate(R.layout.row_color, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position)
    {
        final ColorViewHolder vh = (ColorViewHolder) holder;

        final int color = mGroupedColor.getColor(holder.itemView.getContext(), position);

        String colorValue = ColorUtil.getColorAsRGB(color);
        String colorNumber = mGroupedColor.getColorDescription(holder.itemView.getContext(), position);

        vh.tvColorNumber.setText(colorNumber);
        vh.tvColorValue.setText(colorValue);
        vh.itemView.setBackgroundColor(color);
        if (ColorUtil.getDarknessFactor(color) >= ColorUtil.DARKNESS_FACTOR_BORDER)
        {
            vh.tvColorNumber.setTextColor(Color.WHITE);
            vh.tvColorValue.setTextColor(Color.WHITE);
        }
        else
        {
            vh.tvColorNumber.setTextColor(Color.BLACK);
            vh.tvColorValue.setTextColor(Color.BLACK);
        }

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onColorClicked(ColorAdapter.this, vh, color, vh.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mGroupedColor == null ? 0 : mGroupedColor.getColors().length;
    }

    public static class ColorViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvColorNumber;
        TextView tvColorValue;

        public ColorViewHolder(View itemView)
        {
            super(itemView);
            tvColorNumber = (TextView)itemView.findViewById(R.id.tvColorNumber);
            tvColorValue = (TextView)itemView.findViewById(R.id.tvColorValue);
        }
    }

    public interface IColorClickedListener
    {
        void onColorClicked(ColorAdapter adapter, ColorViewHolder view, Integer color, int pos);
    }
}
