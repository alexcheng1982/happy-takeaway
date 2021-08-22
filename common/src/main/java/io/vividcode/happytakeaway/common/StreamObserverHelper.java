package io.vividcode.happytakeaway.common;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.function.Function;

public class StreamObserverHelper {

  private StreamObserverHelper() {
  }

  public static <R, S> void sendSingleValue(StreamObserver<S> observer,
      R req, Function<R, S> handler) {
    try {
      observer.onNext(handler.apply(req));
      observer.onCompleted();
    } catch (Exception e) {
      observer.onError(getStatus(e).asRuntimeException());
    }
  }

  public static <R, S> StreamObserver<R> sendStream(StreamObserver<S> response,
      Function<R, S> handler) {
    return new StreamObserver<>() {
      @Override
      public void onNext(R req) {
        try {
          response.onNext(handler.apply(req));
        } catch (Exception e) {
          response.onError(StreamObserverHelper.getStatus(e).asRuntimeException());
        }
      }

      @Override
      public void onError(Throwable throwable) {
        response.onCompleted();
      }

      @Override
      public void onCompleted() {
        response.onCompleted();
      }
    };
  }

  private static Status getStatus(Throwable cause) {
    return cause instanceof GrpcStatusAware ?
        ((GrpcStatusAware) cause).toStatus()
        : Status.INTERNAL.withDescription(cause.getMessage()).withCause(cause);
  }
}
