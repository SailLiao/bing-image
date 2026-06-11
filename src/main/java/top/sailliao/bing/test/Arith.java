package top.sailliao.bing.test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;


public class Arith {

	private static final int DEF_DIV_SCALE = 10; // 这个类不能实例化

	/**
	 * 提供精确的加法运算。
	 *
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 多个数提供精确加法运算
	 *
	 * @param values 多个相加的数
	 * @return
	 */
	public static double adds(double... values){
		BigDecimal result = BigDecimal.ZERO;
		if(values != null && values.length > 0){
			for (double value : values) {
				BigDecimal bValue = BigDecimal.valueOf(value);
				result = result.add(bValue);
			}
		}
		return result.doubleValue();
	}

	/**
	 * 多个数提供精确加法运算
	 *
	 * @param values 多个相加的数
	 * @return
	 */
	public static BigDecimal adds(BigDecimal... values){
		BigDecimal result = BigDecimal.ZERO;
		if(values != null && values.length > 0){
			for (BigDecimal value : values) {
				result = result.add(value);
			}
		}
		return result;
	}

	/**
	 * 提供精确的减法运算。
	 *
	 * @param v1
	 *            被减数
	 * @param v2
	 *            减数
	 * @return 两个参数的差
	 */
	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确的减法运算。
	 *
	 * @param v1 被减数
	 * @param v2 减数
	 * @return 两个参数的差(如小于0则返回0)
	 */
	public static double subForLeZero(double v1, double v2) {
		double dVal = sub(v1, v2);
		return dVal < 0 ? 0 : dVal;
	}

	/**
	 * 多个数提供精确加法运算
	 * @param v1 被减数
	 * @param values 多个减数
	 * @return
	 */
	public static double subs(double v1, double... values){
		BigDecimal result = BigDecimal.valueOf(v1);
		if(values != null && values.length > 0){
			for (double value : values) {
				BigDecimal bValue = BigDecimal.valueOf(value);
				result = result.subtract(bValue);
			}
		}
		return result.doubleValue();
	}

	/**
	 * 提供精确的乘法运算。
	 *
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * @return 两个参数的积
	 */
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算。
	 *
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * @return 两个参数的积
	 */
	public static double mulScale(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 提供精确的乘法运算
	 * @param v1 被乘数
	 * @param v2 乘数
	 * @param scale 精度
	 * @param roundingMode 舍入模式
	 */
	public static double mul(double v1, double v2, int scale, int roundingMode) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).setScale(scale, roundingMode).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 *
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, DEF_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 *
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。(向下取整)
	 *
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double divDown(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_DOWN).doubleValue();
	}


	/**
	 * 提供精确的小数位四舍五入处理。
	 *
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 随机红包，随机范围 0.01-剩余红包平均金额*2
	 * @param remainNum剩余红包个数
	 * @param remainMoney剩余红包金额
	 * @return
	 */
	public static double getRandomMoney(int remainNum,double remainMoney) {
		// remainSize 剩余的红包数量
	    // remainMoney 剩余的钱
	    if (remainNum == 1) {
	        return (double) Math.round(remainMoney * 100) / 100;
	    }
	    Random r     = new Random();
	    double min   = 0.01; //最小金额
	    double max   = remainMoney / remainNum * 2;
	    double money = r.nextDouble() * max;
	    money = money <= min ? 0.01: money;
	    money = Math.floor(money * 100) / 100;
	    return money;
	}

	/**
	 * 抽奖， 最小金额 0.1
	 * @param remainNum
	 * @param remainMoney
	 * @return
	 */
	public static double getRandomMoneyWith1Decimal(int remainNum,double remainMoney) {
		// remainSize 剩余的红包数量
		// remainMoney 剩余的钱
		if (remainNum == 1) {
			return (double) Math.round(remainMoney * 10) / 10;
		}
		Random r     = new Random();
		double min   = 0.1; //最小金额
		double max   = remainMoney / remainNum * 2;
		double money = r.nextDouble() * max;
		money = money <= min ? 0.1: money;
		money = Math.floor(money * 10) / 10;
		return money;
	}

	/**
	 * (更精确的)检查浮点值是否小于等于0
	 * <p>
	 * 备注:
	 * 1.传入为空值,则以0处理.
	 *
	 * @param doubleValue
	 * @return true-小于等于0 false-大于0
	 */
	public static boolean checkValueIsLEZero(Double doubleValue) {
		if (doubleValue == null) {
			return true;
		}
		return Math.abs(doubleValue) < 0.0000001;
	}

	/**
	 * (更精确的)检查浮点值是否大于0
	 * <p>
	 * 备注:
	 * 1.传入为空值,则以0处理.
	 *
	 * @param doubleValue
	 * @return
	 */
	public static boolean checkValueIsGtZero(Double doubleValue) {
		return !checkValueIsLEZero(doubleValue);
	}

	/**
	 * 个人所得税扣税公式
	 * @param totalTax 本月总个税
	 * @param totalPreTax 本月总税前收入
	 * @return
	 */
	public static double revenue(Double totalTax, Double totalPreTax) {
		double b = 0d;
		if (800 < totalPreTax && totalPreTax <= 4000) {
			b = sub(mul(sub(totalPreTax, 800), 0.2), totalTax);
		}
		if (totalPreTax > 4000) {
			Double c1 = mul(totalPreTax, 0.8);
			if (c1 < 20000) {
				b = sub(mul(c1, 0.2), totalTax);
			}
			if (c1 > 20000 && c1 <= 50000) {
				b = sub(sub(mul(c1, 0.3), totalTax), 2000);
			}
			if (c1 > 50000) {
				b = sub(sub(mul(c1, 0.4), totalTax), 7000);
			}
		}
		if (totalPreTax <= 800) {
			b = 0.0;
		}
		return b;
	}

	/**
	 *
	 * @param preNum 本期
	 * @param sufNum 同期
	 * @return
	 */
	public static double percentBigDecimal(BigDecimal preNum, BigDecimal sufNum){
		double result = countDecimal(preNum,sufNum);
		return result;
	}

	public static double countDecimal(BigDecimal preNum, BigDecimal sufNum){
		boolean preBoolean = verifyNum(preNum);
		boolean sufBoolean = verifyNum(sufNum);
		//同时为true计算
		if(preBoolean && sufBoolean){
			boolean b = verifyEqual(preNum, sufNum);
			if (b == false){
				return realCountDecimal(preNum,sufNum);
			}
			if (b){
				return 0;
			}
		}
		if(preBoolean == false && sufBoolean ==false){
			return 0;
		}
		if(sufBoolean ==false){
			return 100;
		}
		return  0;
	}

	//验证数字是否为零和null
	public static boolean verifyNum(BigDecimal num){
		if(null !=num && num.compareTo(BigDecimal.ZERO)!=0 ){
			return true;
		}
		return false;
	}

	//验证两个数字是否相等
	public static boolean verifyEqual(BigDecimal preNum, BigDecimal sufNum){
		int n = preNum.compareTo(sufNum);
		//比较 -1 小于   0 等于    1 大于
		if(n==0){
			return true;
		}
		return false;
	}
	//真正计算
	public static double realCountDecimal(BigDecimal preNum, BigDecimal sufNum){
		//(前面的数字-后面的数字)/后面的数字*100
		BigDecimal bigDecimal = (preNum.subtract(sufNum)).divide(sufNum, 2, BigDecimal.ROUND_UP).multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_UP);
		if (bigDecimal.compareTo(BigDecimal.ZERO) !=0){
			return  bigDecimal.doubleValue();
		}
		return 0;
	}
}
