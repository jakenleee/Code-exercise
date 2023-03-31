package com.example.collection.calc;

import lombok.Data;
import org.assertj.core.util.Maps;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @ClassName: CalculatorUnit
 * @Description:
 * @author: lizj
 * @date: 2023/3/30 17:28
 */

public class CalculatorUnit {
    private static final String MATH_ADD = "+";
    private static final String MATH_SUBTRACT = "-";
    private static final String MATH_MULTIPLY = "*";
    private static final String MATH_DIVIDE = "/";

    // 计算过程记录
    private List<CalcNode> lastNumList = new ArrayList<>();
    private int lastNode = -1;

    // 记录回滚前滚过程
    private Map<Integer,Integer> operateMap = Maps.newHashMap(0,0);


    public static void main(String[] args) {
        CalculatorUnit calculatorUnit = new CalculatorUnit();
        BigDecimal bigDecimalOne = calculatorUnit.calculate(BigDecimal.valueOf(3L), "*", BigDecimal.valueOf(4L));
        System.out.println("First计算结果"+bigDecimalOne);
        BigDecimal bigDecimalTwo = calculatorUnit.calculate(bigDecimalOne, "+", BigDecimal.valueOf(4L));
        System.out.println("Second计算结果"+bigDecimalTwo);
        BigDecimal bigDecimalThree = calculatorUnit.calculate(bigDecimalTwo, "/", BigDecimal.valueOf(2L));
        System.out.println("Third计算结果"+bigDecimalThree);
        CalcNode NodeOne = calculatorUnit.undo();
        System.out.println("First undo:"+NodeOne.getResultNum());
        CalcNode NodeTwo = calculatorUnit.undo();
        System.out.println("Second undo:"+NodeTwo.getResultNum());
        CalcNode NodeThree = calculatorUnit.undo();
        System.out.println("Third undo:"+NodeThree.getResultNum());
        CalcNode redo = calculatorUnit.redo();
        System.out.println("First redo:"+redo.getResultNum());
        CalcNode redo1 = calculatorUnit.redo();
        System.out.println("Second redo:"+redo1.getResultNum());
        CalcNode redo2 = calculatorUnit.redo();
        System.out.println("Second redo:"+redo2.getResultNum());
    }

    private CalcNode redo(){
        if (CollectionUtils.isEmpty(lastNumList)) {
            System.out.println("未进行过计算,redo无效");
            throw new UnsupportedOperationException("非法操作");
        }
        if (lastNumList.size()-1 == lastNode) {
            System.out.println("无法再进行redo");
            throw new UnsupportedOperationException("无法再进行redo");
        }
        lastNode++;
        return lastNumList.get(lastNode);
    }


    private CalcNode undo(){
        if (CollectionUtils.isEmpty(lastNumList)) {
            System.out.println("未进行过计算,undo无效");
            throw new UnsupportedOperationException("非法操作");
        }
        if (lastNode == 0) {
            System.out.println("将进行最后一次回滚");
            lastNode--;
            return new CalcNode();
        }
        lastNode--;
        return lastNumList.get(lastNode);
    }


    private BigDecimal calculate(BigDecimal preNum, String operator, BigDecimal newNum) {
        if (null == operator || Objects.isNull(newNum)) {
            System.out.println("操作或输入数字为空");
            throw new UnsupportedOperationException("非法操作");
        }
        preNum = preNum == null ? BigDecimal.ZERO : preNum;
        BigDecimal result = calcOperate(preNum, operator, newNum);
        CalcNode calcNode = new CalcNode();
        calcNode.setPreNum(preNum);
        calcNode.setOperator(operator);
        calcNode.setNewNum(newNum);
        calcNode.setResultNum(result);
        lastNumList.add(calcNode);
        lastNode++;
        return result;
    }


    private BigDecimal calcOperate(BigDecimal preNum, String curOperator, BigDecimal newNum) {

        int scale = 2;
        if(curOperator == null){
            System.out.println("操作为空");
            throw new UnsupportedOperationException("非法操作");
        }
        BigDecimal ret = BigDecimal.ZERO;

        if (MATH_ADD.equals(curOperator)) {
            ret = preNum.add(newNum);
        }
        if (MATH_SUBTRACT.equals(curOperator)) {
            ret = preNum.subtract(newNum).setScale(scale, RoundingMode.HALF_UP);
        }
        if (MATH_MULTIPLY.equals(curOperator)) {
            ret = preNum.multiply(newNum).setScale(scale, RoundingMode.HALF_UP);
        }
        if (MATH_DIVIDE.equals(curOperator)) {
            ret = preNum.divide(newNum,scale,RoundingMode.HALF_UP);
        }
        return ret;
    }



    @Data
    private static class CalcNode {

        public String display(){
            StringBuilder sb = new StringBuilder();
            if(preNum != null){
                sb.append(preNum);
            }
            if(operator != null){
                sb.append(operator);
            }
            if(newNum != null){
                sb.append(newNum).append("=");
            }
            if(resultNum != null){
                sb.append(resultNum);
            }
            return sb.toString();
        }

        private int nodeId;

        // 计算数据入参：参数a（包括但不限于加数，减数）
        private BigDecimal preNum ;

        // 计算数据入参：参数b（包括但不限于加数，被减数，被除数）
        private BigDecimal newNum;

        // 计算操作：+，-，*，/
        private String operator;

        // 计算结果
        private BigDecimal resultNum;
    }

}
