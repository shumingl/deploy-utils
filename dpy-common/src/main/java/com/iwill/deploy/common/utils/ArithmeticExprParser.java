package com.iwill.deploy.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

/**
 * 算数表达式语法解析
 * 
 * @author liushuming
 * 
 */
public class ArithmeticExprParser {
	
	/**
	 * 表达式集合
	 */
	private Map<String, String> expSet;
	/**
	 * 缓存后缀表达式的结构信息
	 */
	private Map<String, List<String>> suffixSet;
	/**
	 * 操作符优先级列表
	 */
	private Map<String, Integer> priority;
	/**
	 * 符号表
	 */
	private String symbols = "+-*/^([{}])";

	/**
	 * 构造函数，初始化符号表和优先级
	 */
	private void InitPriority(){
		priority = new HashMap<String, Integer>();
		// 栈内：'(', '[', '{'
		priority.put("I(", 0);
		priority.put("I[", 0);
		priority.put("I{", 0);
		// 栈外：'+', '-'
		priority.put("O+", 1);
		priority.put("O-", 1);
		// 栈内：'+', '-' 加减法的结合顺序是从左至右，故栈内优先级高。
		priority.put("I+", 2);
		priority.put("I-", 2);
		// 栈外：'*', '/'
		priority.put("O*", 3);
		priority.put("O/", 3);
		// 栈内：'*', '/' 乘除法的结合顺序是从左至右，故栈内优先级高。
		priority.put("I*", 4);
		priority.put("I/", 4);
		// 栈内：'^'
		priority.put("I^", 5);
		// 栈外：'^' 幂运算符的结合顺序是从右至左，故栈外优先级高。
		priority.put("O^", 6);
		// 栈外：'{'
		priority.put("O{", 7);
		// 栈外：'['
		priority.put("O[", 8);
		// 栈外：'('
		priority.put("O(", 9);

	}
	
	/**
	 * 解析表达式集合中的表达式，转化成后缀表达式缓存在bean中
	 */
	private void InitSuffixSet() {
		if (expSet == null) return;
		
		for (String key : expSet.keySet()) {
			suffixSet.put(key, infixToSuffix(expSet.get(key).trim()));
		}
	}
	
	/**
	 * 构造函数，同时初始化运算符优先级
	 */
	public ArithmeticExprParser() {
		suffixSet = new HashMap<String, List<String>>();
		InitPriority();
	}
	
	/**
	 * 初始化对象数据
	 * @param expSet
	 * @return
	 */
	public ArithmeticExprParser parse(Map<String, String> expSet){
		this.expSet = expSet;
		InitSuffixSet();
		return this;
	}

	/**
	 * 将中缀表达式转换成后缀表达式
	 * @param infix
	 * @return
	 */
	public List<String> infixToSuffix(String infix) {
		// 操作符栈
		Stack<String> operators = new Stack<String>();
		// 输出结果
		List<String> suffix = new ArrayList<String>();

		int length = infix.length();
		int index = 0;
		
		// 构建后缀表达式
		while (index < length) {

			char ch = infix.charAt(index);
			
			// 忽略空格
			if (ch == ' '){
				index++;
				continue;
			}
			
			String sch = "" + ch;//待处理的字符

			if (symbols.indexOf(sch) == -1) {//读取变量
				StringBuilder sb = new StringBuilder();
				while(symbols.indexOf(sch) == -1){
					sb.append(sch);
					index++;
					if (index >= length) break;
					sch = "" + infix.charAt(index);
				}
				index--;
				suffix.add(sb.toString());//将变量加入表达式

			} else if (symbols.indexOf(sch) > -1) {//操作符
				if (operators.isEmpty()) {
					operators.push(sch);

				} else {
					
					Integer cur = priority.get("O" + ch);//当前字符的优先级
					Integer top = priority.get("I" + operators.peek());//栈顶字符的优先级
					if (ch != ')' && cur > top) {
						operators.push(sch);

					} else if (ch == ')') {
						do {
							String tmp = operators.pop();
							if (!"(".equals(tmp))
								suffix.add(tmp);
							else
								break;

							if (operators.empty())
								throw new RuntimeException("非法表达式。");

						} while (true);

					} else {
						do {
							String tmp = operators.pop();
							suffix.add(tmp);
							if (operators.isEmpty())
								break;
						} while (cur < priority.get("I" + operators.peek()));
						operators.push(sch);
					}
				}
			} else {
				throw new RuntimeException("非法表达式。");
			}
			index++;
		}

		// 如果栈不为空，把剩余的运算符依次弹出，送至输出序列。
		while (operators.size() != 0) {
			suffix.add(operators.pop());
		}
		return suffix;
	}

	/**
	 * 通过后缀表达式求出算术结果
	 * @param suffixKey 后缀表达式
	 * @return BigDecimal
	 */
	public BigDecimal get(String suffixKey) {

		List<String> suffix = suffixSet.get(suffixKey);// 获取对应的后缀表达式

		if (suffix == null) return null;

		// 定义运行时栈
		Stack<BigDecimal> stack = new Stack<BigDecimal>();

		// 解析并计算后缀表达式
		for (String str : suffix) {

			if (str == null || "".equals(str))
				continue;
			str = str.trim();
			
			// 如果是变量，则进栈
			if (!symbols.contains(str)) {
				
				BigDecimal temp;
				temp = isNumeric(str);// 获取后缀表达式的最后一个字符：常量是数字，返回具体值；表达式是运算符，返回空
				
				if (temp == null) { // 说明这个变量是通过其他字段计算过来的，是表达式
					stack.push(get(str));
				} else {
					stack.push(temp);
				}

			} else {
				// 如果是运算符，弹出运算数，计算结果。
				BigDecimal y = stack.pop();
				BigDecimal x = stack.pop();
				stack.push(caculate(x, y, str)); // 将运算结果重新压入栈。
			}
		}
		return stack.pop(); // 弹出栈顶元素就是运算最终结果。

	}

	/**
	 * BigDecimal 的加减乘除和幂运算
	 * @param x 第一操作数
	 * @param y 第二操作数
	 * @param symbol 运算符
	 * @return
	 */
	private BigDecimal caculate(BigDecimal x, BigDecimal y, String symbol) {
		if (symbol.equals("+"))
			return x.add(y);
		if (symbol.equals("-"))
			return x.subtract(y);
		if (symbol.equals("*"))
			return x.multiply(y);
		if (symbol.equals("/"))
			return x.divide(y, 16, BigDecimal.ROUND_HALF_UP);
		if (symbol.equals("^"))
			return x.pow(y.intValue());
		return BigDecimal.ZERO;
	}
	
	/**
	 * 读取所有的公式配置
	 * @return
	 */
	public Map<String, String> getExpSet() {
		return expSet;
	}
	
	/**
	 * 判断字符串是否为数字
	 * @param string
	 * @return
	 */
	public BigDecimal isNumeric(String string) {
		try {
			return new BigDecimal(string);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 转换成26进制的数字String，使用字母表示
	 * @param integer
	 * @return
	 */
	public static String Integer26String(Integer integer) {
		
		int high = integer - 1;
		if (high <= 0) return "A";
		
		StringBuilder result = new StringBuilder();
		do {
			result.insert(0, (char) (65 + high % 26));
			high = ((Double) Math.floor(high / 26)).intValue() - 1;
		} while (high % 26 >= 0);
		return result.toString();
	}
	
	/**
	 * 测试方法
	 * @param args
	 */
	public static void main(String args[]) throws Exception{

		// 初始化表达式集合
		Map<String, String> exp = new HashMap<String, String>();
		
		// 从控制台读取数据
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		String notice = ""
				+ "===========================================\n"
				+ "格式要求：\n"
				+ "1、第一行包含行数和列数，列数在前，行数在后，使用空格分开。\n"
				+ "2、其余行为阵列的值，不同的值中间使用空格隔开。\n"
				+ "3、公式以等号（=）开始，不能含有空格，不区分大小写，公式之间请勿包含循环引用。\n"
				+ "4、输入两次回车可直接查看计算结果。\n"
				+ "5、输入exit直接退出程序。\n"
				+ "===========================================\n"
				+ "参考数据样式：\n"
				+ "4 3\n"
				+ "10 34 37 =A1+B2+C1\n"
				+ "40 17 34 =A2+B1+C2\n"
				+ "=A1+A2 =B1+B2 =C1+C2 =D1+D2\n"
				+ "===========================================\n\n"
				+ "输入单元格方阵:";
		
		int row = 0; // 行数
		int col = 0; // 列数
		
		// 程序开始
		System.out.println(notice); // 提示信息
		
		String line; // 第一行本来是行数和列数，直接忽略掉不使用，程序中自会处理行列
		
		while (true) {

			line = reader.readLine(); // 从控制台读取一行数据
			
			if (line == null || "".equals(line.trim()) || line.trim().startsWith("exit")) break; // 检测到空行或exit后，退出循环体
			
			line = line.trim().replace("  ", " "); // 将该行数据去掉首尾和中间的多余空格
			String cells[] = line.split(" "); // 根据空格拆分成数组，作为表格中的某一行
			if (cells.length > col) col = cells.length; // 计算最大列数

			row++;
			int index = 1;
			for (String value : cells) { // 遍历每个数值然后保存到表达式中
				if (value != null && !"".equals(value.trim())) { // 忽略掉空白值
					String address = Integer26String(index) + row;
					exp.put(address, value.startsWith("=") ? value.substring(1).toUpperCase() : value); // 添加到表达式集合中
					index++;
				}
			}
		}

		System.out.println("输出计算结果：");
		// 初始化解析器
		ArithmeticExprParser parser = new ArithmeticExprParser();
		parser.parse(exp);
		
		for (int r = 1; r <= row; r++) { // 遍历行列生成单元格地址，然后根据单元格地址计算输出结果
			for (int c = 1; c <= col; c++) {
				String address = Integer26String(c) + r; // 计算地址
				BigDecimal result = parser.get(address);
				System.out.print((result == null ? "" : (result.compareTo(new BigDecimal(result.intValue())) == 0 ? result : result.setScale(4, BigDecimal.ROUND_HALF_UP))) + " "); // 输出单元格计算结果
			}
			System.out.println();
		}
		System.out.println("===========================================");
		reader.close();
	}
}
