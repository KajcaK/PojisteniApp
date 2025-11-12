package eu.dickovadev.pojisteniapp.utils;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Component
public class NumberUtil {

    public String formatNumberDays(Double num) {
        DecimalFormat df = new DecimalFormat("#,##0.0", getCzechFormatSymbols());
        String formattedNum = df.format(num);

        String suffix;
        if (num == 1) {
            suffix = " den";
        } else if (num >= 2 && num <= 4) {
            suffix = " dny";
        } else {
            suffix = " dní";
        }

        return formattedNum + suffix;
    }

    public String formatCurrency(Double num) {
        DecimalFormat df = new DecimalFormat("#,##0", getCzechFormatSymbols());
        return df.format(num) + " Kč";
    }

    private DecimalFormatSymbols getCzechFormatSymbols() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("cs-CZ"));
        symbols.setGroupingSeparator(' ');
        return symbols;
    }
}

