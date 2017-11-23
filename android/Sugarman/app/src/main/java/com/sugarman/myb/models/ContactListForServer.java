package com.sugarman.myb.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by yegoryeriomin on 11/22/17.
 */

@AllArgsConstructor public class ContactListForServer {
  @Setter @Getter @SerializedName("contacts") List<ContactForServer> contactForServerList;
}
