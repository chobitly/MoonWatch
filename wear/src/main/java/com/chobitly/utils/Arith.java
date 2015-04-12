package com.chobitly.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <pre>
 *   由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精   
 *   确的浮点数运算，包括加减乘除和四舍五入。
 * </pre>
 * 
 * @see http://704378737-qq-com.iteye.com/blog/1070562
 */
public class Arith {
	// 默认除法和取整等运算的精度
	public static final int DEF_SCALE = 2;// 金额计算，2位精度即可

	// 这个类不能实例化
	private Arith() {
	}

	/**
	 * 提供精确的比较运算。
	 * 
	 * @param v1
	 * @param v2
	 * 
	 * @return 两个参数是否相等
	 */
	public static boolean equals(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.equals(b2);
	}

	/**
	 * 提供精确的比较运算。
	 * 
	 * @param v1
	 * @param v2
	 * @param scale
	 *            指定精度
	 * 
	 * @return 两个参数是否在指定精度下可认为相等
	 */
	public static boolean equals(double v1, double v2, int scale) {
		BigDecimal b1 = new BigDecimal(Double.toString(round(v1, scale)));
		BigDecimal b2 = new BigDecimal(Double.toString(round(v2, scale)));
		return b1.equals(b2);
	}

	/**
	 * 提供精确的比较运算。
	 * 
	 * @param v1
	 * @param v2
	 * 
	 * @return 两个参数的比较结果
	 */
	public static int compareTo(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.compareTo(b2);
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * 
	 * @return 两个参数的和
	 */
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * 
	 * @return 两个参数的和
	 */
	public static double add(double... values) {
		if (values.length < 2) {
			throw new IllegalArgumentException(
					"The add operation need at least two argments");
		}
		BigDecimal returnValue = new BigDecimal(Double.toString(values[0]));
		for (int i = 1; i < values.length; ++i) {
			returnValue = returnValue.add(new BigDecimal(Double
					.toString(values[i])));
		}
		return returnValue.doubleValue();
	}

	/**
	 * 提供精确的减法运算。
	 * 
	 * @param v1
	 *            被减数
	 * 
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
	 * 提供精确的乘法运算。
	 * 
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * 
	 * @return 两个参数的积
	 */
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到小数点以后10位，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, DEF_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
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
		return div(v1, v2, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，默认精确到小数点后两位，由roundingMode指定取整模式。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param roundingMode
	 *            取整模式，参见{@link java.math.RoundingMode}
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, RoundingMode roundingMode) {
		return div(v1, v2, DEF_SCALE, roundingMode);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，由roundingMode指定取整模式。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @param roundingMode
	 *            取整模式，参见{@link java.math.RoundingMode}
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale,
			RoundingMode roundingMode) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, roundingMode.ordinal()).doubleValue();
	}

	/**
	 * <pre>
	 * 
	 * 提供精确的小数位四舍五入处理，默认保留小数点后两位。       
	 *  
	 * @param v 需要四舍五入的数字       
	 * @return 四舍五入后的结果
	 * </pre>
	 */
	public static double round(double v) {
		return round(v, DEF_SCALE);
	}

	/**
	 * <pre>
	 * 
	 * 提供精确的小数位四舍五入处理。       
	 *  
	 * @param v 需要四舍五入的数字       
	 * @param scale 小数点后保留几位       
	 * @return 四舍五入后的结果
	 * </pre>
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
	 * <pre>
	 * 
	 * 提供精确的小数位向上取整处理，默认保留小数点后两位。       
	 *  
	 * @param v 需要向上取整的数字       
	 * @return 向上取整后的结果
	 * </pre>
	 */
	public static double ceil(double v) {
		return ceil(v, DEF_SCALE);
	}

	/**
	 * <pre>
	 * 
	 * 提供精确的小数位向上取整处理。       
	 *  
	 * @param v 需要向上取整的数字       
	 * @param scale 小数点后保留几位       
	 * @return 向上取整后的结果
	 * </pre>
	 */
	public static double ceil(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_CEILING).doubleValue();
	}

	/**
	 * <pre>
	 * 
	 * 提供精确的小数位向下取整处理，默认保留小数点后两位。       
	 *  
	 * @param v 需要向下取整的数字       
	 * @return 向下取整后的结果
	 * </pre>
	 */
	public static double floor(double v) {
		return floor(v, DEF_SCALE);
	}

	/**
	 * <pre>
	 * 
	 * 提供精确的小数位向下取整处理。       
	 *  
	 * @param v 需要向下取整的数字       
	 * @param scale 小数点后保留几位       
	 * @return 向下取整后的结果
	 * </pre>
	 */
	public static double floor(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_FLOOR).doubleValue();
	}

	/**
	 * <pre>
	 * 
	 * 提供精确的小数位向绝对值大的方向取整的处理，默认保留小数点后两位。       
	 *  
	 * @param v 需要向绝对值大的方向取整的数字       
	 * @return 向绝对值大的方向取整后的结果
	 * </pre>
	 */
	public static double roundUp(double v) {
		return roundUp(v, DEF_SCALE);
	}

	/**
	 * <pre>
	 * 
	 * 提供精确的小数位向绝对值大的方向取整的处理。       
	 *  
	 * @param v 需要向绝对值大的方向取整的数字       
	 * @param scale 小数点后保留几位       
	 * @return 向绝对值大的方向取整后的结果
	 * </pre>
	 */
	public static double roundUp(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_UP).doubleValue();
	}

	/**
	 * <pre>
	 * 
	 * 提供精确的小数位向0的方向取整处理，默认保留小数点后两位。       
	 *  
	 * @param v 需要向0的方向取整的数字       
	 * @return 向0的方向取整后的结果
	 * </pre>
	 */
	public static double roundDown(double v) {
		return roundDown(v, DEF_SCALE);
	}

	/**
	 * <pre>
	 * 
	 * 提供精确的小数位向0的方向取整处理。       
	 *  
	 * @param v 需要向0的方向取整的数字       
	 * @param scale 小数点后保留几位       
	 * @return 向0的方向取整后的结果
	 * </pre>
	 */
	public static double roundDown(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_DOWN).doubleValue();
	}
}