package ua.com.icabbyclient.icabbyclient.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class DecimalUtils {

    private static final int BIG_DECIMAL_SCALE = 4;

    public static BigDecimal createBigDecimal(String number) {
        return new BigDecimal(number, MathContext.DECIMAL128).setScale(BIG_DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal createBigDecimal(double number) {
        return new BigDecimal(number, MathContext.DECIMAL128).setScale(BIG_DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal createBigDecimal(long number) {
        return new BigDecimal(number, MathContext.DECIMAL128).setScale(BIG_DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal createBigDecimalFromCents(long number) {
        return createBigDecimal(number).movePointLeft(2);
    }

    public static BigDecimal createBigDecimalFromCents(String number) {
        return createBigDecimal(number).movePointLeft(2);
    }

    public static int toCents(BigDecimal usd) {
        if (usd != null && usd.compareTo(BigDecimal.ZERO) > 0) {
            return usd.movePointRight(2).intValue();
        }
        return 0;
    }

    public static BigDecimal toCentsBigDecimals(BigDecimal usd) {
        if (usd != null && usd.compareTo(BigDecimal.ZERO) > 0) {
            return usd.movePointRight(2);
        }
        return null;
    }

    public static BigDecimal toDolars(BigDecimal usd) {
        if (usd != null && usd.compareTo(BigDecimal.ZERO) > 0) {
            String total = String.valueOf(usd);
            return new BigDecimal(total).movePointLeft(2);
        }
        return new BigDecimal(0.00);
    }

    /**
     * Returns rounded integer part of big decimal
     *
     * @param value - big decimal to round
     */
    public static BigDecimal roundToInt(BigDecimal value) {
        return value.setScale(0, RoundingMode.HALF_UP);
    }

    public static BigDecimal roundToIntDown(BigDecimal value) {
        return value.setScale(0, RoundingMode.DOWN);
    }

    public static BigDecimal roundToIntUp(BigDecimal value) {
        return value.setScale(0, RoundingMode.UP);
    }

    public static BigDecimal round(BigDecimal value, int places) {
        return value.setScale(places, RoundingMode.HALF_UP);
    }

    public static String getString(BigDecimal value) {
        return getString(value, false);
    }

    public static String getString(BigDecimal value, boolean showZeroValues) {
        if (showZeroValues || value.compareTo(BigDecimal.ZERO) > 0) {
            return DecimalUtils.round(value, 2).toString();
        }
        return "";
    }

    public static boolean moreThanZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    public static int getDollarsFromBigDecimal(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) > 0) {
            String str = DecimalUtils.round(value, 2).toString();
            int dot = str.indexOf('.');
            return Integer.valueOf(str.substring(0, dot)).intValue();
        }
        return 0;
    }

    public static int getCentsFromBigDecimal(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) > 0) {
            String str = DecimalUtils.round(value, 2).toString();
            int dot = str.indexOf('.');
            return Integer.valueOf(str.substring(dot + 1, str.length())).intValue();
        }
        return 0;
    }

    public static String getStringFromBigDecimal0(BigDecimal value) {
        if (value != null && value.compareTo(BigDecimal.ZERO) > 0) {
            return DecimalUtils.round(value, 2).toString();
        }
        return "0.00";
    }

}
