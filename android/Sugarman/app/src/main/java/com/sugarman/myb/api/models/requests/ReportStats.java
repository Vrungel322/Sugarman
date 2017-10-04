package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.utils.StringHelper;
import java.util.Comparator;
import java.util.Date;

public class ReportStats {

  public static final Comparator<ReportStats> BY_DATE_DESC = new Comparator<ReportStats>() {
    @Override public int compare(ReportStats o1, ReportStats o2) {
      return (int) (o2.getDayTimestamp() - o1.getDayTimestamp());
    }
  };

  @SerializedName("steps_count") private int stepsCount;

  @SerializedName("date") private String date; // 2017-01-26T00:00:00-05:00

  private Date utcDate;

  public int getStepsCount() {
    return stepsCount;
  }

  public void setStepsCount(int stepsCount) {
    this.stepsCount = stepsCount;
  }

  public String getDate() {
    return date;
  }

  public Date getUTCDate() {
    if (utcDate == null) {
      utcDate = StringHelper.convertReportStatDate(date);
    }

    return utcDate;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public void setDate(Date date) {
    this.date = StringHelper.getReportStepsDate(date);
  }

  public long getDayTimestamp() {
    return StringHelper.getStatsTimestamp(date);
  }

  @Override public String toString() {
    return "ReportStats{"
        + "stepsCount="
        + stepsCount
        + ", date='"
        + date
        + '\''
        + ", utcDate="
        + utcDate
        + '}';
  }
}
