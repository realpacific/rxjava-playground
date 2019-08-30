package com.realpacific.rxjavademo;

import io.reactivex.Observable;

import java.util.concurrent.TimeUnit;

public class TestingDebugging {

    static Observable<String> globalSource = Observable.just("Alpha", "Beta", "Gamma", "Omega", "Theta", "Zeta");

    static Observable<Integer> globalIntegerSource = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    public static void main(String[] args) {
        Observable.interval(1, TimeUnit.SECONDS).take(5).subscribe(System.out::println);
        sleep(5000);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
