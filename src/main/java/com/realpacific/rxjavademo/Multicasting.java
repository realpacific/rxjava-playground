package com.realpacific.rxjavademo;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.*;

@SuppressWarnings("ALL")
public class Multicasting {
    static int start = 0;
    static int count = 10;

    static Observable<String> globalSource = Observable.just("Alpha", "Beta", "Gamma", "Omega", "Theta", "Zeta");
    static Observable<String> globalNepaliSource = Observable.just("Ek", "Dui", "Teen", "Char", "Panch", "Cha");
    static Observable<Integer> globalIntSource = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    public static void main(String[] args) {
        // exConnectableObservables();
        // exAutoConnect();
        // exAutoConnect_2();
        // exReplay();
        // exReplay_withArgument();
        // exReplay_withTimeUnit();
        // exCache();
//        exPublishSubject();
//        exPublishSubject_2();
//	exBehaviourSubject();
//	exReplaySubject();
//        exAsyncSubject();
        exUnicastSubject();

    }

    private static void exUnicastSubject() {
        Subject<Integer> subject = UnicastSubject.create();
        subject.subscribe(s -> System.out.println("Observer 1 : " + s));
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);
        subject.onNext(4);
        subject.onNext(5);
        subject.onNext(6);
        subject.onComplete();
    }

    private static void exAsyncSubject() {
        Subject<Integer> subject = AsyncSubject.create();

        // It will get 1, 2, 3, 4, 5, 6 and onComplete
        subject.subscribe(s -> System.out.println("Observer 1 : " + s));

        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        // It will get 3(last emitted)and 4, 5, 6(subsequent item) for second observer also.
        subject.subscribe(s -> System.out.println("Observer 2 : " + s));

        subject.onNext(4);
        subject.onNext(5);
        subject.onNext(6);
        subject.onComplete();
    }

    private static void exReplaySubject() {
        Subject<Integer> subject = ReplaySubject.create();

        // It will get 1, 2, 3, 4, 5, 6 and onComplete
        subject.subscribe(s -> System.out.println("Observer 1 : " + s));

        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        // It will get 3(last emitted)and 4, 5, 6(subsequent item) for second observer also.
        subject.subscribe(s -> System.out.println("Observer 2 : " + s));

        subject.onNext(4);
        subject.onNext(5);
        subject.onNext(6);
        subject.onComplete();

    }

    private static void exBehaviourSubject() {
        Subject<Integer> subject = BehaviorSubject.create();

        // It will get 1, 2, 3, 4, 5, 6 and onComplete
        subject.subscribe(s -> System.out.println("Observer 1 : " + s));

        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        // It will get 3(last emitted)and 4, 5, 6(subsequent item) for second observer also.
        subject.subscribe(s -> System.out.println("Observer 2 : " + s));

        subject.onNext(4);
        subject.onNext(5);
        subject.onNext(6);
        subject.onComplete();
    }

    private static void exPublishSubject_2() {
        PublishSubject<Integer> subject = PublishSubject.create();

        // It will get 1, 2, 3, 4 and onComplete
        subject.subscribe(s -> System.out.println("Observer 1 : " + s));

        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        // It will get 4 and onComplete for second observer also.
        subject.subscribe(s -> System.out.println("Observer 2 : " + s));

        subject.onNext(4);
        subject.onComplete();
    }

    private static void exPublishSubject() {
        Subject<String> subject = PublishSubject.create();
        System.out.println("--------");
        subject.flatMap(t -> Observable.fromArray(t.split("")))
                .map(v -> Character.isUpperCase(v.charAt(0)) ? Character.toLowerCase(v.charAt(0)) : Character.toUpperCase(v.charAt(0)))
                .subscribe(t -> System.out.print(t + ", "));
        subject.onNext("AlPhA");
        subject.onNext("BeTa");
        subject.onNext("gaMmA");
        Random rand = new Random();
        for (char c = 'a'; c <= 'z'; c++) {
            if ((rand.nextInt(30)) % 2 == 0)
                subject.onNext(String.valueOf(c).toLowerCase());
            else
                subject.onNext(String.valueOf(c).toUpperCase());
        }
    }

    private static void exCache() {
        Observable<Integer> cachedRollingTotals = Observable.just(6, 2, 5, 7, 1, 4, 9, 8, 3)
                .scan(0, (total, next) -> total + next).cache();
        cachedRollingTotals.subscribe(System.out::println);

        cachedRollingTotals.subscribe(i -> System.out.println("Sources 1: " + i));

    }

    private static void exReplay_withTimeUnit() {
        ConnectableObservable<Long> replayer = Observable.interval(1, TimeUnit.SECONDS).replay(2, TimeUnit.SECONDS);
        Observable<Long> sources = replayer.autoConnect();

        sources.subscribe(i -> System.out.println("Sources 1: " + i));
        sleep(5000L);

        sources.subscribe(i -> System.out.println("Sources 2: " + i));
        sleep(3000L);
    }

    private static void exReplay_withArgument() {
        ConnectableObservable<Long> replayer = Observable.interval(1, TimeUnit.SECONDS).replay(2);
        Observable<Long> sources = replayer.autoConnect();

        sources.subscribe(i -> System.out.println("Sources 1: " + i));
        sleep(5000L);

        sources.subscribe(i -> System.out.println("Sources 2: " + i));
        sleep(3000L);

    }

    private static void exReplay() {
        ConnectableObservable<Long> replayer = Observable.interval(1, TimeUnit.SECONDS).replay();
        Observable<Long> sources = replayer.autoConnect();

        sources.subscribe(i -> System.out.println("Sources 1: " + i));
        sleep(3000L);

        sources.subscribe(i -> System.out.println("Sources 2: " + i));
        sleep(3000L);

    }

    private static void exAutoConnect_2() {
        Observable<Long> sources = Observable.interval(1, TimeUnit.SECONDS).publish().autoConnect();
        sources.subscribe(i -> System.out.println("Sources 1: " + i));
        sleep(3000L);
        sources.subscribe(i -> System.out.println("Sources 2: " + i));
        sleep(3000L);
    }

    private static void exAutoConnect() {
        Observable<Integer> sources = Observable.range(0, 5).map(i -> randomInt()).publish().autoConnect(2);
        sources.subscribe(i -> System.out.println("Sources 1: " + i));
        sources.subscribe(i -> System.out.println("Sources 2: " + i));
    }

    private static void exConnectableObservables() {
        ConnectableObservable<Integer> sources = Observable.range(0, 5).map(i -> randomInt()).publish();
        sources.subscribe(i -> System.out.println("Sources 1: " + i));
        sources.subscribe(i -> System.out.println("Sources 2: " + i));
        sources.connect();
    }

    public static int randomInt() {
        return ThreadLocalRandom.current().nextInt(100000);
    }

    private static void sleep(Long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
