package com.sugarman.myb.api;

import com.clover_studio.spikachatmodule.models.Login;
import com.clover_studio.spikachatmodule.models.User;
import com.clover_studio.spikachatmodule.utils.Const;
import com.sugarman.myb.models.chat_refactor.GetMessagesModelRefactored;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by nikita on 29.01.2018.
 */

public interface SpikaOSRetroApiInterfaceRx {

  @POST(Const.Api.USER_LOGIN) Observable<Login> login(@Body User user);

  @GET(Const.Api.MESSAGES) rx.Observable<GetMessagesModelRefactored> getMessages(
      @Path(Const.Params.ROOM_ID) String roomId,
      @Path(Const.Params.LAST_MESSAGE_ID) String lastMessageId,
      @Header(Const.Params.ACCESS_TOKEN) String token);
}
