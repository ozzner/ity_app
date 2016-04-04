package com.italkyou.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by RenzoD on 02/07/2015.
 */
public class StringUtil {



    public static final String ITY_FORMAT = "##,##0.00";

    /**
     * @param input Cadena a formatear
     */
    public static String format(Double input){
        DecimalFormat myFormatter;
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();

        if (AppUtil.obtenerIdiomaLocal().equals(Const.IDIOMA_ES)){
            dfs.setDecimalSeparator(Const.CHAR_COMMA);
            dfs.setPerMill(Const.CHAR_DOT);
            myFormatter = new DecimalFormat(ITY_FORMAT);
        }
        else {
            dfs.setDecimalSeparator(Const.CHAR_DOT);
            dfs.setPerMill(Const.CHAR_COMMA);
            myFormatter = new DecimalFormat(ITY_FORMAT);
        }

        myFormatter.setDecimalFormatSymbols(dfs);
        String output = myFormatter.format(input);

        return output;
    }
}
