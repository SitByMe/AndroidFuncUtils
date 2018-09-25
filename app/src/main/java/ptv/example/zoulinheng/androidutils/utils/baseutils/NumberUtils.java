package ptv.example.zoulinheng.androidutils.utils.baseutils;

import java.text.DecimalFormat;

/**
 * Created by lhZou on 2017/9/1.
 * desc: 数字工具类
 */
public class NumberUtils {
    public enum SymbolSite {
        LEFT, RIGHT
    }

    /**
     * 将数转换成钱
     *
     * @param money money(double)
     * @return
     */
    public static String toMoney(double money) {
        return toMoney(money, true);
    }

    /**
     * 将数转换成钱
     *
     * @param money money(int)
     * @return
     */
    public static String toMoney(int money) {
        return toMoney(money, true);
    }

    /**
     * 将数转换成钱
     *
     * @param money      money(double)
     * @param withSymbol 是否带有人民币符号（¥）
     * @return
     */
    public static String toMoney(double money, boolean withSymbol) {
        return toMoney(money, withSymbol, SymbolSite.LEFT);
    }

    /**
     * 将数转换成钱
     *
     * @param money      money(int)
     * @param withSymbol 是否带有人民币符号（¥）
     * @return
     */
    public static String toMoney(int money, boolean withSymbol) {
        return toMoney(money, withSymbol, SymbolSite.LEFT);
    }

    /**
     * 将数转换成钱
     *
     * @param monty      money(double)
     * @param withSymbol 是否带有人民币符号
     * @param symbolSite LEFT：左边带“¥”   RIGHT：右边带“元”
     * @return
     */
    public static String toMoney(double monty, boolean withSymbol, SymbolSite symbolSite) {
        switch (symbolSite) {
            case LEFT:
                return withSymbol ? "¥" + retain2Size(monty) : retain2Size(monty);
            case RIGHT:
                return withSymbol ? retain2Size(monty) + "元" : retain2Size(monty);
        }
        return retain2Size(monty);
    }

    /**
     * 将数转换成钱
     *
     * @param money      money(int)
     * @param withSymbol 是否带有人民币符号
     * @param symbolSite LEFT：左边带“¥”   RIGHT：右边带“元”
     * @return
     */
    public static String toMoney(int money, boolean withSymbol, SymbolSite symbolSite) {
        switch (symbolSite) {
            case LEFT:
                return withSymbol ? "¥" + money : String.valueOf(money);
            case RIGHT:
                return withSymbol ? money + "元" : String.valueOf(money);
        }
        return toMoney(money, false);
    }

    private static String retain2Size(double money) {
        DecimalFormat df = new DecimalFormat("######0.00");
        return df.format(money);
    }

    public static String retainSize(double number, int size) {
        StringBuilder pattern = new StringBuilder("######0");
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                pattern.append(".");
            }
            pattern.append("0");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(number);
    }
}
