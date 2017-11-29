package com.sugarman.myb.models.custom_events;

import com.google.gson.annotations.SerializedName;
import io.realm.RealmList;
import io.realm.RealmObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nikita on 29.11.2017.
 */
@NoArgsConstructor
public class RuleSet extends RealmObject {
  @Getter @Setter @SerializedName("rules") private RealmList<Rule> rules;
}
