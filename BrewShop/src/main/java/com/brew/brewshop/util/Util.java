package com.brew.brewshop.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.brew.brewshop.storage.recipes.Weight;

import java.math.BigDecimal;
import java.util.List;

public class Util {
    private static final String TAG = Util.class.getName();

    private static final double MIN_GALLONS = 1.0;

    private static int[] SRM_COLORS = new int[] {
        0xF3F993,
        0xF5F75C,
        0xF6F513,
        0xEAE615,
        0xE0D01B,
        0xD5BC26,
        0xCDAA37,
        0xC1963C,
        0xBE8C3A,
        0xBE823A,
        0xC17A37,
        0xBF7138,
        0xBC6733,
        0xB26033,
        0xA85839,
        0x985336,
        0x8D4C32,
        0x7C452D,
        0x6B3A1E,
        0x5D341A,
        0x4E2A0C,
        0x4A2727,
        0x361F1B,
        0x261716,
        0x231716,
        0x19100F,
        0x16100F,
        0x120D0C,
        0x100B0A,
        0x050B0A
    };

    public static double calculateHopUtilization(double minutes, double gravity) {
        double bigness = 1.65 * Math.pow(0.000125, (gravity - 1));
        double timeFactor = (1 - Math.exp(-0.04 * minutes)) / 4.15;
        return bigness * timeFactor;
    }

    public static double getTinsethIbu(double minutes, double oz, double percentAlpha, double gallons, double gravity) {
        if (gallons < MIN_GALLONS) {
            gallons = MIN_GALLONS;
        }
        double util = calculateHopUtilization(minutes, gravity);
        return (util * percentAlpha/100 * oz * 7490) / gallons;
    }

    public static int getColor(double srm) {
        return getColorNoAlpha(srm) + 0xFF000000;
    }

    private static int getColorNoAlpha(double srm) {
        int rounded = (int) Math.round(srm);
        if (rounded < 1) {
            return SRM_COLORS[0];
        } else if (rounded > SRM_COLORS.length) {
            return SRM_COLORS[SRM_COLORS.length - 1];
        } else {
            return SRM_COLORS[rounded - 1];
        }
    }

    public static int toInt(CharSequence value) {
        if (value.length() == 0) {
            return 0;
        }
        return Integer.parseInt(value.toString());
    }

    public static double toDouble(CharSequence value) {
        double converted = 0;
        try {
            converted = Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            Log.d(TAG, "Error converting to double: " + value);
        }
        return converted;
    }

    public static String fromDouble(double d, int precision) {
        return fromDouble(d, precision, true);
    }

    public static String fromDouble(double d, int precision, boolean stripZeros) {
        BigDecimal bd = new BigDecimal(d).setScale(precision, BigDecimal.ROUND_HALF_EVEN);
        String string;
        if (stripZeros) {
            if (d < .001) { //Fix a bug in Java 7
                string = "0";
            } else {
                string = bd.stripTrailingZeros().toPlainString();
            }
        } else {
            string = bd.toPlainString();
        }
        return string;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static String separateSentences(String paragraph) {
        return paragraph.replace(". ", ".\n\n");
    }

    public static boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static String formatImperialWeight(Weight weight, int significance) {
        StringBuilder builder = new StringBuilder();
        int pounds = weight.getPoundsPortion();
        if (pounds > 0) {
            builder.append(String.format("%d lb.", pounds));
        }
        double ounces = weight.getOuncesPortion();
        double min = Math.pow(10 ,-significance) * .5;
        if (pounds == 0 || ounces > min) {
            if (pounds > 0) {
                builder.append(" ");
            }
            Util.fromDouble(ounces, 1, true);
            builder.append(Util.fromDouble(ounces, significance, true) + " oz.");
        }
        return builder.toString();
    }

    public static String formatMetricWeight(Weight weight, int significance) {
        StringBuilder builder = new StringBuilder();
        double grams = weight.getGrams();
        if (grams < 1000) {
            builder.append(Util.fromDouble(grams, 1)).append(" g");
        } else {
            builder.append(Util.fromDouble(weight.getKilograms(), significance)).append(" kg");
        }
        return builder.toString();
    }
}
