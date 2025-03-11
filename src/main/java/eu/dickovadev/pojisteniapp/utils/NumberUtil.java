package eu.dickovadev.pojisteniapp.utils;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Component
public class NumberUtil {

    public static String formatNumberDays(double num) {
        DecimalFormat df = new DecimalFormat("#,##0.0", getCzechFormatSymbols());
        String formattedNum = df.format(num);

        String suffix = (num == 1) ? " den" : " dní";

        return formattedNum + suffix;
    }

    public static String formatCurrency(double num) {
        DecimalFormat df = new DecimalFormat("#,##0", getCzechFormatSymbols());
        return df.format(num) + " Kč";
    }

    private static DecimalFormatSymbols getCzechFormatSymbols() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');
        return symbols;
    }
}

