package net.bigtangle.wallet.core.utils;

import android.content.Context;
import android.os.Build;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



/**
 * Format Util.
 */
public class FormatUtil {



    /**
     * Erster Großbuchstabe.
     * 
     * @param name
     *            Wert für name
     * @return Ergebnis als string
     */
    public static String firstCapital(final String name) {
        if (name == null) {
            return "";
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

    }
    public static Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }
    public static DecimalFormat getDecimalFormat(Locale locale) {
        // DecimalFormatSymbols dfs = new DecimalFormatSymbols( locale);
        // dfs.setDecimalSeparator(',');
        return new DecimalFormat("###,###,###,###,###,###,###,###,##0.####################################",
                DecimalFormatSymbols.getInstance(locale));
        // new DecimalFormat("0.##", dfs);
    }

    public static String toXMLData(Object d) {
        if (d == null)
            return "";
        if (d instanceof String) {
            return "<![CDATA[" + d + "]]>";
        }
        if (d instanceof Date) {
            return (new SimpleDateFormat("yyyy-MM-dd")).format(d);
        }
        return d.toString();
    }
}
