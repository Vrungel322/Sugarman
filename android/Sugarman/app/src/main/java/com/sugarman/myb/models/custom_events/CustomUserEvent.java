package com.sugarman.myb.models.custom_events;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

/**
 * Created by nikita on 27.11.2017.
 */
@AllArgsConstructor @NoArgsConstructor @Builder
public class CustomUserEvent {
  @Getter @Setter private int type;                                 // dialog or activity or other
  @Getter @Setter private Integer numValue;                         // count of smth (groups, days in a row, steps)
  @Getter @Setter private String strValue;                          // poka ne ebu
  @Getter @Setter private String eventName;                         // event title
  @Getter @Setter private String eventImage;                        //event img if needed
  @Getter @Setter @Singular private List<String> eventExtraStrings; // some text body
}
