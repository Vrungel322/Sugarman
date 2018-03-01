package com.sugarman.myb.api.models.responses.me.stats;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.utils.StringHelper;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java.util.Comparator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor public class Stats extends RealmObject
    implements Parcelable {

  public static final Comparator<Stats> BY_DATE_DESC = new Comparator<Stats>() {
    @Override public int compare(Stats o1, Stats o2) {
      return (int) (o2.getDayTimestamp() - o1.getDayTimestamp());
    }
  };
  public static final Creator<Stats> CREATOR = new Creator<Stats>() {
    @Override public Stats createFromParcel(Parcel in) {
      return new Stats(in);
    }

    @Override public Stats[] newArray(int size) {
      return new Stats[size];
    }
  };
  @PrimaryKey Integer id;
  @SerializedName("date") private String date; // format 2017-01-04
  @SerializedName("label") private String label;
  @SerializedName("steps_count") private int stepsCount;

  //public Stats(){}
  @SerializedName("timestamp") private long dayTimestamp;

  public Stats()
  {

  }

  private Stats(Parcel in) {
    dayTimestamp = in.readLong();
    date = in.readString();
    label = in.readString();
    stepsCount = in.readInt();
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public int getStepsCount() {
    return stepsCount;
  }

  public void setStepsCount(int stepsCount) {
    this.stepsCount = stepsCount;
  }

  public long getDayTimestamp() {
    if (dayTimestamp == 0) {
      dayTimestamp = StringHelper.getStatsTimestamp(date);
    }
    return dayTimestamp;
  }

  public void setDayTimestamp(long dayTimestamp) {
    this.dayTimestamp = dayTimestamp;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(dayTimestamp);
    dest.writeString(date);
    dest.writeString(label);
    dest.writeInt(stepsCount);
  }

  @Override public String toString() {
    return "Stats{"
        + "date='"
        + date
        + '\''
        + ", label='"
        + label
        + '\''
        + ", stepsCount="
        + stepsCount
        + ", dayTimestamp="
        + dayTimestamp
        + '}';
  }
}
