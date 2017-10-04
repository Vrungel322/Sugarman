package com.sugarman.myb.eventbus.events;

import com.sugarman.myb.api.models.responses.me.requests.Request;
import java.util.ArrayList;
import java.util.List;

public class RequestsUpdatedEvent {

  private final List<Request> requests = new ArrayList<>(0);

  public RequestsUpdatedEvent(List<Request> requests) {
    this.requests.clear();
    this.requests.addAll(requests);
  }

  public List<Request> getRequests() {
    return requests;
  }
}
