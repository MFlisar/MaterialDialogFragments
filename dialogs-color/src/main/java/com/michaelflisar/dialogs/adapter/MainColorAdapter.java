package com.michaelflisar.dialogs.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.michaelflisar.dialogs.color.R;
import com.michaelflisar.dialogs.utils.ColorUtil;

public class MainColorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private ColorUtil.GroupedColor[] mItems;
    private IMainColorClickedListener mListener;
    private int mSelected = -1;
    private boolean mDarkTheme;

    public MainColorAdapter(boolean darkTheme, ColorUtil.GroupedColor[] items, int selected, IMainColorClickedListener listener)
    {
        mDarkTheme = darkTheme;
        mItems = items;
        mListener = listener;
        mSelected = selected;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ColorViewHolder(inflater.inflate(R.layout.row_main_color, parent, false));
    }

    public void setSelected(int index)
    {
        int oldSelected = mSelected;
        mSelected = index;
        notifyItemChanged(oldSelected);
        notifyItemChanged(index);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final ColorViewHolder vh = (ColorViewHolder) holder;

        vh.vSelectedBackground.setVisibility(position == mSelected ? View.VISIBLE : View.INVISIBLE);

        if (vh.getOldPosition() != position)
        {
            final ColorUtil.GroupedColor groupColor = mItems[position];
            int color = 0;
            if (groupColor.getMainColorRes() != null)
                color = groupColor.getMainColor(holder.itemView.getContext());
            else
                color = Color.TRANSPARENT;
            String title = groupColor.getHeaderDescription(vh.itemView.getContext());
            title = "";

            drawBackground(vh.vSelectedBackground, mDarkTheme, Color.TRANSPARENT, true);
            vh.tvColor.setText(title);
            if (color == Color.TRANSPARENT)
            {
                Drawable d = VectorDrawableCompat.create(holder.itemView.getContext().getResources(), R.drawable.vector_bw, holder.itemView.getContext().getTheme());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    vh.tvColor.setBackground(d);
                else
                    vh.tvColor.setBackgroundDrawable(d);
            }
//                vh.tvColor.setBackgroundResource(R.drawable.vector_bw);
            else
                drawBackground(vh.tvColor, mDarkTheme, color, false);
            if (ColorUtil.getDarknessFactor(color) >= ColorUtil.DARKNESS_FACTOR_BORDER)
                vh.tvColor.setTextColor(Color.WHITE);
            else
                vh.tvColor.setTextColor(Color.BLACK);

            vh.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mListener != null)
                        mListener.onColorClicked(MainColorAdapter.this, vh, groupColor, vh.getAdapterPosition());
                }
            });
        }
    }

    private void drawBackground(View view, boolean darkTheme, int color, boolean withBorder)
    {
        ColorUtil.setCircleBackground(view, withBorder, darkTheme, color);
    }

    @Override
    public int getItemCount() {
        return mItems.length;
    }

    public static class ColorViewHolder extends RecyclerView.ViewHolder
    {
        View vSelectedBackground;
        TextView tvColor;

        public ColorViewHolder(View itemView)
        {
            super(itemView);
            vSelectedBackground = itemView.findViewById(R.id.vSelectedBackground);
            tvColor = (TextView) itemView.findViewById(R.id.tvColor);
        }
    }

    public interface IMainColorClickedListener
    {
        void onColorClicked(MainColorAdapter adapter, ColorViewHolder view, ColorUtil.GroupedColor color, int pos);
    }
}
