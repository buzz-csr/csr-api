package com.naturalmotion.csr_api.service.car.comparator;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CarComparator implements Comparator<Integer> {

    private Pattern pattern = Pattern.compile("^(.*?)_");

    private JsonArray caow;

    public CarComparator(JsonArray caow) {
        this.caow = caow;
    }

    @Override
    public int compare(Integer o1, Integer o2) {

        int result = 0;
        if (o1 == -1) {
            result = 1;
        }
        if (o2 == -1) {
            result = -1;
        }
        if (o1 != -1 && o2 != -1) {
            JsonObject carLeft = caow.getJsonObject(o1);
            JsonObject carRight = caow.getJsonObject(o2);

            String brandLeft = getBrand(carLeft);
            String brandRight = getBrand(carRight);

            int dynoLeft = carLeft.getInt("tthm");
            int dynoRight = carRight.getInt("tthm");

            String tierLeft = carLeft.getString("ctie");
            String tierRight = carRight.getString("ctie");

            int legendLevelLeft = carLeft.getInt("cmlv");
            int legendLevelRight = carRight.getInt("cmlv");


            if (legendLevelLeft == legendLevelRight) {
                if (brandLeft.equals(brandRight)) {
                    if (tierLeft.equals(tierRight)) {
                        result = Integer.compare(dynoLeft, dynoRight);
                    } else {
                        result = tierRight.compareTo(tierLeft);
                    }
                } else {
                    result = brandLeft.toLowerCase().compareTo(brandRight.toLowerCase());
                }
            } else {
                result = Integer.compare(legendLevelRight, legendLevelLeft);
            }
        }
        return result;
    }

    private String getBrand(JsonObject o1) {
        String brandLeft = null;
        Matcher matcher = pattern.matcher(o1.getString("crdb"));
        if (matcher.find()) {
            brandLeft = matcher.group(1);
        }
        return brandLeft;
    }
}
