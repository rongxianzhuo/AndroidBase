package com.zhimeng.base;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * author:rongxianzhuo create at 2016/8/7
 * email: rongxianzhuo@gmail.com
 */
public class RxBus {

    private final static Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    public static void send(Object o) {
        bus.onNext(o);
    }

    public static Observable<Object> toObservable() {
        return bus;
    }
}
