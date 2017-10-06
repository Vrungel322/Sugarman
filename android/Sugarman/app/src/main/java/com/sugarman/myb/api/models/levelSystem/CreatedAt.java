
package com.sugarman.myb.api.models.levelSystem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreatedAt {

    @SerializedName("$date")
    @Expose
    private Integer date;

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer $date) {
        this.date = $date;
    }

}
