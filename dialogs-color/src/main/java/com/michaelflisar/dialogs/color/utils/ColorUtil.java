package com.michaelflisar.dialogs.color.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.core.content.ContextCompat;
import android.view.View;

import com.michaelflisar.dialogs.color.R;

public class ColorUtil {
    public static final float[] COLOR_FILTER_NEGATIVE = {
            -1.0f, 0, 0, 0, 255, // red
            0, -1.0f, 0, 0, 255, // green
            0, 0, -1.0f, 0, 255, // blue
            0, 0, 0, 1.0f, 0  // alpha
    };

    public static final double DARKNESS_FACTOR_BORDER = 0.2f;

    public static GroupedColor COLORS_BW = new GroupedColor(null, R.string.black_white,
            R.color.md_black_1000,
            R.color.md_white_1000
    );

    public static GroupedColor COLORS_AMBER = new GroupedColor(5, R.string.amber,
            R.color.md_amber_50,
            R.color.md_amber_100,
            R.color.md_amber_200,
            R.color.md_amber_300,
            R.color.md_amber_400,
            R.color.md_amber_500,
            R.color.md_amber_600,
            R.color.md_amber_700,
            R.color.md_amber_800,
            R.color.md_amber_900,
            R.color.md_amber_A100,
            R.color.md_amber_A200,
            R.color.md_amber_A400,
            R.color.md_amber_A700
    );

    public static GroupedColor COLORS_BLUE = new GroupedColor(5, R.string.blue,
            R.color.md_blue_50,
            R.color.md_blue_100,
            R.color.md_blue_200,
            R.color.md_blue_300,
            R.color.md_blue_400,
            R.color.md_blue_500,
            R.color.md_blue_600,
            R.color.md_blue_700,
            R.color.md_blue_800,
            R.color.md_blue_900,
            R.color.md_blue_A100,
            R.color.md_blue_A200,
            R.color.md_blue_A400,
            R.color.md_blue_A700
    );

    public static GroupedColor COLORS_BLUE_GREY = new GroupedColor(5, R.string.blue_grey,
            R.color.md_blue_grey_50,
            R.color.md_blue_grey_100,
            R.color.md_blue_grey_200,
            R.color.md_blue_grey_300,
            R.color.md_blue_grey_400,
            R.color.md_blue_grey_500,
            R.color.md_blue_grey_600,
            R.color.md_blue_grey_700,
            R.color.md_blue_grey_800,
            R.color.md_blue_grey_900
    );

    public static GroupedColor COLORS_BROWN = new GroupedColor(5, R.string.brown,
            R.color.md_brown_50,
            R.color.md_brown_100,
            R.color.md_brown_200,
            R.color.md_brown_300,
            R.color.md_brown_400,
            R.color.md_brown_500,
            R.color.md_brown_600,
            R.color.md_brown_700,
            R.color.md_brown_800,
            R.color.md_brown_900
    );

    public static GroupedColor COLORS_CYAN = new GroupedColor(5, R.string.cyan,
            R.color.md_cyan_50,
            R.color.md_cyan_100,
            R.color.md_cyan_200,
            R.color.md_cyan_300,
            R.color.md_cyan_400,
            R.color.md_cyan_500,
            R.color.md_cyan_600,
            R.color.md_cyan_700,
            R.color.md_cyan_800,
            R.color.md_cyan_900,
            R.color.md_cyan_A100,
            R.color.md_cyan_A200,
            R.color.md_cyan_A400,
            R.color.md_cyan_A700
    );

    public static GroupedColor COLORS_DEEP_ORANGE = new GroupedColor(5, R.string.deep_orange,
            R.color.md_deep_orange_50,
            R.color.md_deep_orange_100,
            R.color.md_deep_orange_200,
            R.color.md_deep_orange_300,
            R.color.md_deep_orange_400,
            R.color.md_deep_orange_500,
            R.color.md_deep_orange_600,
            R.color.md_deep_orange_700,
            R.color.md_deep_orange_800,
            R.color.md_deep_orange_900,
            R.color.md_deep_orange_A100,
            R.color.md_deep_orange_A200,
            R.color.md_deep_orange_A400,
            R.color.md_deep_orange_A700
    );

    public static GroupedColor COLORS_DEEP_PURPLE = new GroupedColor(5, R.string.deep_purple,
            R.color.md_deep_purple_50,
            R.color.md_deep_purple_100,
            R.color.md_deep_purple_200,
            R.color.md_deep_purple_300,
            R.color.md_deep_purple_400,
            R.color.md_deep_purple_500,
            R.color.md_deep_purple_600,
            R.color.md_deep_purple_700,
            R.color.md_deep_purple_800,
            R.color.md_deep_purple_900,
            R.color.md_deep_purple_A100,
            R.color.md_deep_purple_A200,
            R.color.md_deep_purple_A400,
            R.color.md_deep_purple_A700
    );

    public static GroupedColor COLORS_GREEN = new GroupedColor(5, R.string.green,
            R.color.md_green_50,
            R.color.md_green_100,
            R.color.md_green_200,
            R.color.md_green_300,
            R.color.md_green_400,
            R.color.md_green_500,
            R.color.md_green_600,
            R.color.md_green_700,
            R.color.md_green_800,
            R.color.md_green_900,
            R.color.md_green_A100,
            R.color.md_green_A200,
            R.color.md_green_A400,
            R.color.md_green_A700
    );

    public static GroupedColor COLORS_GREY = new GroupedColor(5, R.string.grey,
            R.color.md_grey_50,
            R.color.md_grey_100,
            R.color.md_grey_200,
            R.color.md_grey_300,
            R.color.md_grey_400,
            R.color.md_grey_500,
            R.color.md_grey_600,
            R.color.md_grey_700,
            R.color.md_grey_800,
            R.color.md_grey_900
    );

    public static GroupedColor COLORS_INDIGO = new GroupedColor(5, R.string.indigo,
            R.color.md_indigo_50,
            R.color.md_indigo_100,
            R.color.md_indigo_200,
            R.color.md_indigo_300,
            R.color.md_indigo_400,
            R.color.md_indigo_500,
            R.color.md_indigo_600,
            R.color.md_indigo_700,
            R.color.md_indigo_800,
            R.color.md_indigo_900,
            R.color.md_indigo_A100,
            R.color.md_indigo_A200,
            R.color.md_indigo_A400,
            R.color.md_indigo_A700
    );

    public static GroupedColor COLORS_LIGHT_BLUE = new GroupedColor(5, R.string.light_blue,
            R.color.md_light_blue_50,
            R.color.md_light_blue_100,
            R.color.md_light_blue_200,
            R.color.md_light_blue_300,
            R.color.md_light_blue_400,
            R.color.md_light_blue_500,
            R.color.md_light_blue_600,
            R.color.md_light_blue_700,
            R.color.md_light_blue_800,
            R.color.md_light_blue_900,
            R.color.md_light_blue_A100,
            R.color.md_light_blue_A200,
            R.color.md_light_blue_A400,
            R.color.md_light_blue_A700
    );

    public static GroupedColor COLORS_LIGHT_GREEN = new GroupedColor(5, R.string.light_green,
            R.color.md_light_green_50,
            R.color.md_light_green_100,
            R.color.md_light_green_200,
            R.color.md_light_green_300,
            R.color.md_light_green_400,
            R.color.md_light_green_500,
            R.color.md_light_green_600,
            R.color.md_light_green_700,
            R.color.md_light_green_800,
            R.color.md_light_green_900,
            R.color.md_light_green_A100,
            R.color.md_light_green_A200,
            R.color.md_light_green_A400,
            R.color.md_light_green_A700
    );

    public static GroupedColor COLORS_LIME = new GroupedColor(5, R.string.lime,
            R.color.md_lime_50,
            R.color.md_lime_100,
            R.color.md_lime_200,
            R.color.md_lime_300,
            R.color.md_lime_400,
            R.color.md_lime_500,
            R.color.md_lime_600,
            R.color.md_lime_700,
            R.color.md_lime_800,
            R.color.md_lime_900,
            R.color.md_lime_A100,
            R.color.md_lime_A200,
            R.color.md_lime_A400,
            R.color.md_lime_A700
    );

    public static GroupedColor COLORS_ORANGE = new GroupedColor(5, R.string.orange,
            R.color.md_orange_50,
            R.color.md_orange_100,
            R.color.md_orange_200,
            R.color.md_orange_300,
            R.color.md_orange_400,
            R.color.md_orange_500,
            R.color.md_orange_600,
            R.color.md_orange_700,
            R.color.md_orange_800,
            R.color.md_orange_900,
            R.color.md_orange_A100,
            R.color.md_orange_A200,
            R.color.md_orange_A400,
            R.color.md_orange_A700
    );

    public static GroupedColor COLORS_PINK = new GroupedColor(5, R.string.pink,
            R.color.md_pink_50,
            R.color.md_pink_100,
            R.color.md_pink_200,
            R.color.md_pink_300,
            R.color.md_pink_400,
            R.color.md_pink_500,
            R.color.md_pink_600,
            R.color.md_pink_700,
            R.color.md_pink_800,
            R.color.md_pink_900,
            R.color.md_pink_A100,
            R.color.md_pink_A200,
            R.color.md_pink_A400,
            R.color.md_pink_A700
    );

    public static GroupedColor COLORS_PURPLE = new GroupedColor(5, R.string.purple,
            R.color.md_purple_50,
            R.color.md_purple_100,
            R.color.md_purple_200,
            R.color.md_purple_300,
            R.color.md_purple_400,
            R.color.md_purple_500,
            R.color.md_purple_600,
            R.color.md_purple_700,
            R.color.md_purple_800,
            R.color.md_purple_900,
            R.color.md_purple_A100,
            R.color.md_purple_A200,
            R.color.md_purple_A400,
            R.color.md_purple_A700
    );

    public static GroupedColor COLORS_RED = new GroupedColor(5, R.string.red,
            R.color.md_red_50,
            R.color.md_red_100,
            R.color.md_red_200,
            R.color.md_red_300,
            R.color.md_red_400,
            R.color.md_red_500,
            R.color.md_red_600,
            R.color.md_red_700,
            R.color.md_red_800,
            R.color.md_red_900,
            R.color.md_red_A100,
            R.color.md_red_A200,
            R.color.md_red_A400,
            R.color.md_red_A700
    );

    public static GroupedColor COLORS_TEAL = new GroupedColor(5, R.string.teal,
            R.color.md_teal_50,
            R.color.md_teal_100,
            R.color.md_teal_200,
            R.color.md_teal_300,
            R.color.md_teal_400,
            R.color.md_teal_500,
            R.color.md_teal_600,
            R.color.md_teal_700,
            R.color.md_teal_800,
            R.color.md_teal_900,
            R.color.md_teal_A100,
            R.color.md_teal_A200,
            R.color.md_teal_A400,
            R.color.md_teal_A700
    );

    public static GroupedColor COLORS_YELLOW = new GroupedColor(5, R.string.yellow,
            R.color.md_yellow_50,
            R.color.md_yellow_100,
            R.color.md_yellow_200,
            R.color.md_yellow_300,
            R.color.md_yellow_400,
            R.color.md_yellow_500,
            R.color.md_yellow_600,
            R.color.md_yellow_700,
            R.color.md_yellow_800,
            R.color.md_yellow_900,
            R.color.md_yellow_A100,
            R.color.md_yellow_A200,
            R.color.md_yellow_A400,
            R.color.md_yellow_A700);


    public static final GroupedColor[] COLORS = new GroupedColor[]
            {
                    COLORS_RED,
                    COLORS_PINK,
                    COLORS_PURPLE,
                    COLORS_DEEP_PURPLE,
                    COLORS_INDIGO,
                    COLORS_BLUE,
                    COLORS_LIGHT_BLUE,
                    COLORS_CYAN,
                    COLORS_TEAL,
                    COLORS_GREEN,
                    COLORS_LIGHT_GREEN,
                    COLORS_LIME,
                    COLORS_YELLOW,
                    COLORS_AMBER,
                    COLORS_ORANGE,
                    COLORS_DEEP_ORANGE,
                    COLORS_BROWN,
                    COLORS_GREY,
                    COLORS_BLUE_GREY,
                    COLORS_BW
            };

    public static String getNameFromMaterialColor(Context context, int color) {
        String name = context.getResources().getResourceEntryName(color);
        int firstUnderlineIndex = name.indexOf("_");
        int lastUnderlineIndex = name.lastIndexOf("_");

        String subName = name.substring(lastUnderlineIndex + 1);
        String group = name.substring(firstUnderlineIndex + 1, lastUnderlineIndex).replace("_", " ");

        return subName;
    }

    public static int getNearestColorGroup(Context c, int color) {
        int index = 0;
        Double minDiff = null;

        for (int i = 0; i < COLORS.length; i++) {
            // s/w => nur auswählen, falls Farben gleich sind
            if (COLORS[i].getColors().length == 2) {
                if (color == COLORS[i].getColor(c, 0) || color == COLORS[i].getColor(c, 1)) {
                    return i;
                }
            }
            // sonst haben die Listen 10 oder 14 Werte, wir vergleichen mit der 500er Farbe wleche immer auf Index 6 ist
            else {
                double diff = calcColorDifference(color, COLORS[i].getColor(c, 6));
                if (minDiff == null || minDiff > diff) {
                    minDiff = diff;
                    index = i;
                }
            }
        }

        return index;
    }

    public static int getBestTextColor(int background) {
        if (getDarknessFactor(background) > 0.2f) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    public static boolean isColorDark(int color) {
        double darkness = getDarknessFactor(color);
        if (darkness < 0.5) {
            return false;
        } else {
            return true;
        }
    }

    public static double getDarknessFactor(int color) {
        return 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
    }

    public static String getColorAsRGB(int color) {
        return String.format("#%06X", 0xFFFFFF & color);
    }

    public static String getColorAsARGB(int color) {
        return String.format("#%08X", 0xFFFFFFFF & color);
    }

    public static double calcColorDifference(int c1, int c2) {
        int r1 = Color.red(c1);
        int g1 = Color.green(c1);
        int b1 = Color.blue(c1);

        int r2 = Color.red(c2);
        int g2 = Color.green(c2);
        int b2 = Color.blue(c2);

        int diffRed = Math.abs(r1 - r2);
        int diffGreen = Math.abs(g1 - g2);
        int diffBlue = Math.abs(b1 - b2);

        double pctDiffRed = (double) diffRed / 255f;
        double pctDiffGreen = (double) diffGreen / 255f;
        double pctDiffBlue = (double) diffBlue / 255f;

        return (pctDiffRed + pctDiffGreen + pctDiffBlue) / 3f;
    }

    public static class GroupedColor {
        private Integer mMainColorIndex;
        private int mResTitle;
        private int[] mColors;

        public GroupedColor(Integer mainColorIndex, int resTitle, int... colors) {
            mMainColorIndex = mainColorIndex;
            mResTitle = resTitle;
            mColors = colors;
        }

        public Integer getMainColorRes() {
            if (mMainColorIndex != null) {
                return mColors[mMainColorIndex];
            }
            return null;
        }

        public Integer getMainColor(Context context) {
            return ContextCompat.getColor(context, getMainColorRes());
        }

        public int[] getColors() {
            return mColors;
        }

        public int getColor(Context context, int index) {
            return ContextCompat.getColor(context, mColors[index]);
        }

        public int getColorRes(int index) {
            return mColors[index];
        }

        public String getHeaderDescription(Context context) {
            return context.getString(mResTitle).toUpperCase();
        }

        public String getColorDescription(Context context, int index) {
            return ColorUtil.getNameFromMaterialColor(context, mColors[index]);
        }
    }

    public static void setCircleBackground(View view, boolean withBorder, boolean darkTheme, int color) {
        GradientDrawable drawable = null;
        if (withBorder) {
            drawable = (GradientDrawable) view.getContext().getResources().getDrawable(darkTheme ? R.drawable.circle_with_border_dark : R.drawable.circle_with_border_light);
        } else {
            drawable = (GradientDrawable) view.getContext().getResources().getDrawable(R.drawable.circle);
        }
        drawable.setColor(color);
//        drawable.setColorFilter(primColor, PorterDuff.Mode.SRC_ATOP);

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        view.setBackgroundDrawable(drawable);
//        else
//            view.setBackground(drawable);
    }
}
