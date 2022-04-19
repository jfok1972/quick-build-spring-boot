package com.jhopesoft.framework.core.objectquery.export;

/**
 * 每一个导出的列的具体属性
 * 
 * @author jiangfeng
 *
 */
public enum ExcelColumnType {

  /** 整型 */
  Integer,
  /** 整型有数值单位 */
  IntegerMonetary,
  /** 实数型 */
  Double,
  /** 实数型有数值单位 */
  DoubleMonetary,
  /** 百分比 */
  Percent,
  /** 加权平均 */
  WeightedAverage,
  /** 字符串 */
  String,
  /** 日期 */
  Date,
  /** 日期时间 */
  Datetime

}
