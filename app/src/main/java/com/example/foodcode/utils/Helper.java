package com.example.foodcode.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class Helper {

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = json.get(key);
            map.put(key, value);
        }

        return map;
    }

    public static String formatMoney(Double value, Boolean withThousand) {

        if (withThousand) {
            NumberFormat format = NumberFormat.getNumberInstance(Locale.CHINA);
            format.setMaximumFractionDigits(2);
            format.setMinimumFractionDigits(2);
            return format.format(value);
        } else {
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(value);
        }
    }
}
