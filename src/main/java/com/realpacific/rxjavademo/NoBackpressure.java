package com.realpacific.rxjavademo;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class NoBackpressure {

    static Observable<String> globalSource = Observable.just("Alpha", "Beta", "Gamma", "Omega", "Theta", "Zeta");
    static Observable<Integer> globalIntegerSource = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    public static void main(String[] args) {
        // exBuffer();
        // exBufferWithHashSet();
        // exBufferWithSkip();
        // exBufferWithTime();
        // exWIndow();
        exThrottle();

    }

    private static void exThrottle() {
        Observable<String> source1 = Observable.interval(2, TimeUnit.MILLISECONDS).map(i -> (i + 3) * 4)
                .map(i -> "SOURCE 1: " + i).take(10);
        Observable<String> source2 = Observable.interval(300, TimeUnit.MILLISECONDS).map(i -> (i + 1) * 300)
                .map(i -> "SOURCE 2: " + i).take(3);
        Observable<String> source3 = Observable.interval(2000, TimeUnit.MILLISECONDS).map(i -> (i + 1) * 2000)
                .map(i -> "SOURCE 3: " + i).take(2);
        Observable.concat(source1, source2, source3)
                .throttleLast(500, TimeUnit.MILLISECONDS)
                .subscribe(System.out::println);
        sleep(10000L);
    }

    private static void exWIndow() {
        Observable.range(1, 30).window(3).flatMapSingle(obs -> obs.toList()).subscribe(System.out::println);
    }

    private static void exBufferWithTime() {
        Observable.range(1, 1000).buffer(3, TimeUnit.MICROSECONDS).subscribe(System.out::println);

    }

    private static void exBufferWithSkip() {
        Observable.range(1, 30).buffer(5, 1).subscribe(System.out::println);
    }

    private static void exBufferWithHashSet() {
        Observable.fromArray(1, 2, 3, 4, 5, 1, 1, 1, 1, 4, 3, 4, 3).buffer(5, HashSet::new)
                .subscribe(System.out::println);
    }

    private static void exBuffer() {
        Observable.range(10, 43).buffer(5).subscribe(System.out::println);
    }

    private static void sleep(Long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
