package top.sailliao.bing.test.wm;

import java.util.Calendar;

public class DateUti {

    public static String monthNowStr(int second) {
        Calendar c = Calendar.getInstance();
        if (second != 0) {
            c.add(Calendar.MONTH, second);
        }
        return c.get(Calendar.YEAR) + "_" + (c.get(Calendar.MONTH) + 1);
    }

    public static void main(String[] args) {
        System.out.println(monthNowStr(-1));
    }
}
