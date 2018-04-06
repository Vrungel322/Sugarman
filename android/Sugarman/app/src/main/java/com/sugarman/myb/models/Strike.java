package com.sugarman.myb.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class Strike {
  @Getter @Setter int startingPosition;
  @Getter @Setter int value;
}
