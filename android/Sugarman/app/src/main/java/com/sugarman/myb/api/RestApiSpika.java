package com.sugarman.myb.api;

import com.clover_studio.spikachatmodule.models.Login;
import com.clover_studio.spikachatmodule.models.User;
import com.sugarman.myb.models.chat_refactor.GetMessagesModelRefactored;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by nikita on 29.01.2018.
 */
@AllArgsConstructor
public class RestApiSpika {
  private final SpikaOSRetroApiInterfaceRx api;

  public Observable<GetMessagesModelRefactored> fetchMessagesSpika(String roomId, String lastMessageId,
      String token) {
    return api.getMessages(roomId,lastMessageId,token);
  }

  public Observable<Login> loginSpika(User user) {
    return api.login(user);
  }
}
