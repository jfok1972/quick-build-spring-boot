package com.jhopesoft.framework.core.objectquery.filter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.jhopesoft.framework.utils.Constants;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

public class DateSectionFilter {

  public static final String THISYEAR = "thisyear";
  public static final String THISQUARTER = "thisquarter";
  public static final String THISMONTH = "thismonth";
  public static final String THISDAY = "thisday";
  public static final String YEAR = "year";
  public static final String YEARQUARTER = "yearquarter";
  public static final String YEARMONTH = "yearmonth";
  public static final String DAY = "day";
  public static final String YEARSECTION = "yearsection";
  public static final String MONTHSECTION = "monthsection";
  public static final String QUARTERSECTION = "quartersection";
  public static final String DATESECTION = "datesection";
  public static final String DAYSECTION = "daysection";

  public static final String RELATIVEYEARSECTION = "relativeyearsection";
  public static final String RELATIVEMONTHSECTION = "relativemonthsection";
  public static final String RELATIVEQUARTERSECTION = "relativequartersection";
  public static final String RELATIVEDATESECTION = "relativedatesection";
  public static final String RELATIVEDAYSECTION = "relativedaysection";

  public static final int TWO = 2;

  private String property;
  private String type;
  private String value;

  public DateSectionFilter(String property, String type, String value) {
    super();
    this.property = property;
    this.type = type;
    this.value = value;
  }

  public static boolean isDataSectionFilter(String type) {
    return (type != null) && (type.equals(THISYEAR) || type.equals(THISQUARTER) || type.equals(THISMONTH)
        || type.equals(THISDAY) || type.equals(YEAR) || type.equals(YEARQUARTER) || type.equals(YEARMONTH)
        || type.equals(DAY) || type.equals(YEARSECTION) || type.equals(MONTHSECTION) || type.equals(QUARTERSECTION)
        || type.equals(DATESECTION) || type.equals(DAYSECTION) || type.equals(RELATIVEYEARSECTION)
        || type.equals(RELATIVEMONTHSECTION) || type.equals(RELATIVEQUARTERSECTION) || type.equals(RELATIVEDATESECTION)
        || type.equals(RELATIVEDAYSECTION));
  }

  public String getWhereSql() {
    Calendar cal = Calendar.getInstance();
    Integer nowyear = cal.get(Calendar.YEAR);
    Integer nowmonth = cal.get(Calendar.MONTH) + 1;
    Integer nowquarter = (nowmonth - 1) / 3 + 1;
    Integer nowday = cal.get(Calendar.DAY_OF_MONTH);
    String result = null;
    if (THISYEAR.equals(type)) {
      result = " year(" + property + ") = " + nowyear;
    } else if (THISQUARTER.equals(type)) {
      result = String.format(" year( %s ) = %d and month( %s ) between  %d and %d ", property, nowyear, property,
          (nowquarter - 1) * 3 + 1, (nowquarter - 1) * 3 + 3);
    } else if (THISMONTH.equals(type)) {
      result = String.format(" year( %s ) = %d and month( %s ) = %d ", property, nowyear, property, nowmonth);
    } else if (THISDAY.equals(type)) {
      result = String.format(" year( %s ) = %d and month( %s ) = %d and day( %s ) = %d  ", property, nowyear, property,
          nowmonth, property, nowday);
    } else if (YEAR.equals(type)) {
      result = " year(" + property + ") = " + value;
    } else if (YEARQUARTER.equals(type)) {
      String[] yq = value.split("-");
      int q = Integer.parseInt(yq[1]);
      result = String.format(" year( %s ) = %s and month( %s ) between  %d and %d ", property, yq[0], property,
          (q - 1) * 3 + 1, (q - 1) * 3 + 3);
    } else if (YEARMONTH.equals(type)) {
      String[] yq = value.split("-");
      int q = Integer.parseInt(yq[1]);
      result = String.format(" year( %s ) = %s and month( %s ) = %d ", property, yq[0], property, q);
    } else if (DAY.equals(type)) {
      String[] yq = value.split("-");
      result = String.format(" year( %s ) = %s and month( %s ) = %s and day( %s ) = %s  ", property, yq[0], property,
          yq[1], property, yq[2]);
    } else if (YEARSECTION.equals(type)) {
      String[] y = value.split("--");
      if (y.length == TWO && y[0].length() > 0 && y[1].length() > 0) {
        result = String.format(" year( %s ) between %s and %s ", property, y[0], y[1]);
      } else {
        if (y[0].length() > 0) {
          result = String.format(" year( %s ) >= %s ", property, y[0]);
        } else {
          result = String.format(" year( %s ) <= %s ", property, y[1]);
        }
      }
    } else if (RELATIVEYEARSECTION.equals(type)) {
      Calendar date = Calendar.getInstance();
      int year = date.get(Calendar.YEAR);
      String[] y = value.split("--");
      if (y.length == TWO && y[0].length() > 0 && y[1].length() > 0) {
        result = String.format(" year( %s ) between %s and %s ", property, getRelaviteYear(year, y[0]),
            getRelaviteYear(year, y[1]));
      } else {
        if (y[0].length() > 0) {
          result = String.format(" year( %s ) >= %s ", property, getRelaviteYear(year, y[0]));
        } else {
          result = String.format(" year( %s ) <= %s ", property, getRelaviteYear(year, y[1]));
        }
      }
    } else if (MONTHSECTION.equals(type)) {
      String[] y = value.split("--");
      if (y.length == TWO && y[0].length() > 0 && y[1].length() > 0) {
        result = getmonthSql(property, y[0], ">") + " and " + getmonthSql(property, y[1], "<");
      } else {
        if (y[0].length() > 0) {
          result = getmonthSql(property, y[0], ">");
        } else {
          result = getmonthSql(property, y[1], "<");
        }
      }
    } else if (RELATIVEMONTHSECTION.equals(type)) {
      String[] y = value.split("--");
      if (y.length == TWO && y[0].length() > 0 && y[1].length() > 0) {
        result = getmonthSql(property, getRelaviteMonth(y[0]), ">") + " and "
            + getmonthSql(property, getRelaviteMonth(y[1]), "<");
      } else {
        if (y[0].length() > 0) {
          result = getmonthSql(property, getRelaviteMonth(y[0]), ">");
        } else {
          result = getmonthSql(property, getRelaviteMonth(y[1]), "<");
        }
      }
    } else if (QUARTERSECTION.equals(type)) {
      String[] y = value.split("--");
      if (y.length == TWO && y[0].length() > 0 && y[1].length() > 0) {
        result = getquarterSql(property, y[0], ">") + " and " + getquarterSql(property, y[1], "<");
      } else {
        if (y[0].length() > 0) {
          result = getquarterSql(property, y[0], ">");
        } else {
          result = getquarterSql(property, y[1], "<");
        }
      }
    } else if (RELATIVEQUARTERSECTION.equals(type)) {
      String[] y = value.split("--");
      if (y.length == TWO && y[0].length() > 0 && y[1].length() > 0) {
        result = getquarterSql(property, getRelaviteQuarter(y[0]), ">") + " and "
            + getquarterSql(property, getRelaviteQuarter(y[1]), "<");
      } else {
        if (y[0].length() > 0) {
          result = getquarterSql(property, getRelaviteQuarter(y[0]), ">");
        } else {
          result = getquarterSql(property, getRelaviteQuarter(y[1]), "<");
        }
      }
    } else if (DATESECTION.equals(type) || DAYSECTION.equals(type)) {
      String[] y = value.split("--");
      if (y.length == TWO && y[0].length() > 0 && y[1].length() > 0) {
        result = getDateSql(property, y[0], ">") + " and " + getDateSql(property, y[1], "<");
      } else {
        if (y[0].length() > 0) {
          result = getDateSql(property, y[0], ">");
        } else {
          result = getDateSql(property, y[1], "<");
        }
      }
    } else if (RELATIVEDATESECTION.equals(type) || RELATIVEDAYSECTION.equals(type)) {
      String[] y = value.split("--");
      if (y.length == TWO && y[0].length() > 0 && y[1].length() > 0) {
        result = getDateSql(property, getRelaviteDate(y[0]), ">") + " and "
            + getDateSql(property, getRelaviteDate(y[1]), "<");
      } else {
        if (y[0].length() > 0) {
          result = getDateSql(property, getRelaviteDate(y[0]), ">");
        } else {
          result = getDateSql(property, getRelaviteDate(y[1]), "<");
        }
      }
    }
    if (result != null) {
      return "(" + result + ")";
    } else {
      return null;
    }

  }

  private Integer getRelaviteYear(int base, String spos) {
    int pos = 0;
    try {
      pos = Integer.parseInt(spos);
    } catch (Exception e) {
    }
    return base + pos;
  }

  private String getRelaviteMonth(String spos) {
    Calendar date = Calendar.getInstance();
    int pos = 0;
    try {
      pos = Integer.parseInt(spos);
    } catch (Exception e) {
    }
    date.add(Calendar.MONTH, pos);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
    return sdf.format(date.getTime());
  }

  private String getRelaviteDate(String spos) {
    Calendar date = Calendar.getInstance();
    int pos = 0;
    try {
      pos = Integer.parseInt(spos);
    } catch (Exception e) {
    }
    date.add(Calendar.DATE, pos);
    SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
    return sdf.format(date.getTime());
  }

  private String getRelaviteQuarter(String spos) {
    Calendar date = Calendar.getInstance();
    int pos = 0;
    try {
      pos = Integer.parseInt(spos);
    } catch (Exception e) {
    }
    date.add(Calendar.MONTH, pos * 3);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-");
    return sdf.format(date.getTime()) + getSeason(date);
  }

  /**
   * 
   * 1 第一季度 2 第二季度 3 第三季度 4 第四季度
   * 
   * @param date
   * @return
   */
  public int getSeason(Calendar c) {
    int season = 0;
    int month = c.get(Calendar.MONTH);
    switch (month) {
      case Calendar.JANUARY:
      case Calendar.FEBRUARY:
      case Calendar.MARCH:
        season = 1;
        break;
      case Calendar.APRIL:
      case Calendar.MAY:
      case Calendar.JUNE:
        season = 2;
        break;
      case Calendar.JULY:
      case Calendar.AUGUST:
      case Calendar.SEPTEMBER:
        season = 3;
        break;
      case Calendar.OCTOBER:
      case Calendar.NOVEMBER:
      case Calendar.DECEMBER:
        season = 4;
        break;
      default:
    }
    return season;
  }

  /**
   * 取得一个大于或小于月份的条件
   * 
   * @param fn
   * @param value
   * @param equal
   * @return
   */
  public String getmonthSql(String fn, String value, String equal) {
    String[] yq = value.split("-");
    return String.format("( year( %s ) %s %s or ( year( %s ) = %s and month( %s ) %s= %s ))", fn, equal, yq[0], fn,
        yq[0], fn, equal, yq[1]);
  }

  /**
   * 取得一个大于或小于季度份的条件
   * 
   * @param fn
   * @param value
   * @param equal
   * @return
   */
  public String getquarterSql(String fn, String value, String equal) {
    String[] yq = value.split("-");
    int jd = Integer.parseInt(yq[1]);
    return String.format("( year( %s ) %s %s or ( year( %s ) = %s and month( %s ) %s= %s ))", fn, equal, yq[0], fn,
        yq[0], fn, equal, ">".equals(equal) ? (jd - 1) * 3 + 1 : (jd - 1) * 3 + 3);
  }

  /**
   * 取得一个大于或小于日期的条件
   * 
   * @param fn
   * @param value
   * @param equal
   * @return
   */
  public String getDateSql(String fn, String value, String equal) {
    String[] yq = value.split("-");
    return String.format(
        "( year( %s ) %s %s or ( year( %s ) = %s and month( %s ) %s %s ) or "
            + " ( year( %s ) = %s and month( %s ) = %s and day( %s ) %s= %s ))",
        fn, equal, yq[0], fn, yq[0], fn, equal, yq[1], fn, yq[0], fn, yq[1], fn, equal, yq[2]);
  }

  public String getProperty() {
    return property;
  }

  public void setProperty(String property) {
    this.property = property;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
