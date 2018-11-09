package com.iwill.deploy.common.utils;

import com.iwill.deploy.common.utils.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 逻辑表达式语法解析
 *
 * @author shumingl
 */
public class LogicalExprParser {

    private static final Logger logger = LoggerFactory.getLogger(LogicalExprParser.class);

    /**
     * 表达式游标
     */
    private int index = 0; // 游标
    /**
     * 表达式
     */
    private String expr;
    /**
     * 后缀表达式的结构信息
     */
    private List<String> suffix;
    /**
     * 作业状态
     */
    private Map<String, Boolean> status;
    /**
     * 操作符优先级列表
     */
    private Map<String, Integer> priority;
    /**
     * 符号表
     */
    private String symbols = "&&;||;(;)";

    /**
     * 构造函数，初始化符号表和优先级
     */
    private void InitPriority() {

        priority = new HashMap<>();

        priority.put("O(", 7);// 栈外：'('
        priority.put("I(", 0);// 栈内：'('

        priority.put("O||", 1);// 栈外：或
        priority.put("I||", 2);// 栈内：或

        priority.put("O&&", 3);// 栈外：与
        priority.put("I&&", 4);// 栈内：与

    }

    /**
     * 构造函数，同时初始化运算符优先级
     */
    public LogicalExprParser() {
        InitPriority();
    }

    /**
     * 构造函数，同时初始化运算符优先级
     */
    public LogicalExprParser(String expr) {
        InitPriority();
        this.expr = expr;
        this.suffix = infix2suffix();
    }


    /**
     * @param expr 表达式。<br/>如：job1 && job2 || job3 && !job4
     * @return
     */
    public LogicalExprParser withExpr(String expr) {
        this.expr = expr;
        this.suffix = infix2suffix();
        return this;
    }

    /**
     * 将中缀表达式转换成后缀表达式
     *
     * @return 后缀表达式
     */
    private List<String> infix2suffix() {
        // 操作符栈
        Stack<String> operators = new Stack<>();
        // 输出结果
        List<String> suffix = new ArrayList<>();
        // 构建后缀表达式
        String token = next();
        while (token != null) {
            if (!symbols.contains(token)) {//读取变量
                suffix.add(token);//将变量加入表达式
            } else if (symbols.contains(token)) {//操作符
                if (operators.isEmpty()) {//栈空则直接压栈
                    operators.push(token);
                } else {
                    Integer cur = priority.get("O" + token);//当前字符的优先级
                    Integer top = priority.get("I" + operators.peek());//栈顶字符的优先级
                    if (token.charAt(0) != ')' && cur > top) operators.push(token);
                    else if (token.charAt(0) == ')') {
                        do {
                            String tmp = operators.pop();
                            if (!"(".equals(tmp)) suffix.add(tmp);
                            else break;
                            if (operators.empty()) throw new RuntimeException("非法表达式");
                        } while (true);
                    } else {
                        do {
                            suffix.add(operators.pop());
                            if (operators.isEmpty()) break;
                        } while (cur < priority.get("I" + operators.peek()));
                        operators.push(token);
                    }
                }
            } else
                throw new RuntimeException("非法表达式");
            token = next();
        }

        // 如果栈不为空，把剩余的运算符依次弹出，送至输出序列。
        while (operators.size() != 0) {
            suffix.add(operators.pop());
        }
        return suffix;
    }

    /**
     * 通过后缀表达式求出算术结果
     *
     * @return
     */
    public Boolean exec(Map<String, Boolean> status) {

        if (suffix == null) return null;
        this.status = status;

        Stack<Object> stack = new Stack<>();// 定义运行时栈

        // 解析并计算后缀表达式
        for (String str : suffix) {
            if (str == null || "".equals(str)) continue;
            str = str.trim();
            // 如果是变量，则进栈
            if (!symbols.contains(str)) {
                stack.push(str);
            } else {
                // 如果是运算符，弹出运算数，计算结果。
                Object y = stack.pop();
                Object x = stack.pop();
                Boolean result = calc(x, y, str);
                stack.push(result); // 将运算结果重新压入栈。
            }
        }
        // 弹出栈顶元素就是运算最终结果。
        Object top = stack.pop();
        if (top instanceof String)
            return getJobRet((String) top);
        else
            return (Boolean) top;
    }

    /**
     * 逻辑运算
     *
     * @param l      第一操作数
     * @param r      第二操作数
     * @param symbol 运算符
     * @return
     */
    private Boolean calc(Object l, Object r, String symbol) {
        if (l == null || r == null) return false;

        Boolean left = l instanceof String ? getJobRet((String) l) : (boolean) l;
        Boolean right = r instanceof String ? getJobRet((String) r) : (boolean) r;
        Boolean result = false;

        if (symbol.equals("&&"))
            result = left && right;
        else if (symbol.equals("||"))
            result = left || right;

        //logger.debug(String.format("expr : %s(%s) %s %s(%s) = %s", l, left, symbol, r, right, result));
        return result;
    }

    /**
     * 获取作业状态
     *
     * @param jobId 作业ID
     * @return
     */
    private boolean getJobRet(String jobId) {
        if (StringUtil.isNOE(jobId)) return false;

        Boolean result;
        if (jobId.startsWith("!")) jobId = jobId.substring(1);
        result = status.get(jobId);
        if (result == null) return false;
        if (jobId.startsWith("!")) result = !result;
        return result;
    }

    /**
     * 获取下一个符号
     *
     * @return
     */
    private String next() {

        if (index >= expr.length()) return null;//表达式结尾

        char c = expr.charAt(index);

        while (c == ' ' || c == '\t') { //跳过空白字符
            index++;
            if (index >= expr.length()) {//表达式结尾
                return null;
            } else {
                c = expr.charAt(index);
            }
        }

        // 读取下一个符号
        switch (c) {
            case '&':
                if (expr.charAt(index + 1) == '&') {
                    index += 2;
                    return "&&";
                } else {
                    throw new RuntimeException("error: &. expect: &&");
                }
            case '|':
                if (expr.charAt(index + 1) == '|') {
                    index += 2;
                    return "||";
                } else {
                    throw new RuntimeException("error: |. expect: ||");
                }
            case '(':
            case ')':
                index++;
                return "" + c;
            default: // 普通符号
                StringBuilder temp = new StringBuilder();
                String chars = "()&|";
                while (!chars.contains(c + "")) {
                    temp.append(c);
                    index++;
                    if (index >= expr.length()) {//表达式结尾
                        return temp.toString();
                    } else {
                        c = expr.charAt(index);
                    }
                }
                return temp.toString();
        }
    }

    public String getExpr() {
        return expr;
    }

}
