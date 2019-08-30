package com.realpacific.rxjavademo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;

public class App {
    static int start = 0;
    static int count = 10;

    static Observable<String> globalSource = Observable.just("Alpha", "Beta", "Gamma", "Omega", "Theta", "Zeta");

    static Observable<Integer> globalIntegerSource = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    public static void main(String[] args) {
        // exFilters();
        // exMaps();
        // exOnNext();
        // exFromIterables();
        // exThreeParamsOfSubscribe();
        // exConnectableObservables();
        // exInterval();
        // exRange();
        // exDefer();
        // exFromCallable();
        // exSingle();
        // exDisposable();
        // exTake();
        // exMap2();
        // exSorted();
        // exScan();
        // exToMap();
        // exCollect();
        // exDoOnNext();

    }

    private static void exDoOnNext() {
        globalSource.doOnComplete(() -> System.out.println("Completed"))
                .doOnNext(o -> System.out.println("Do on next: " + o)).map(s -> s.length() > 4)
                .doOnNext(s -> System.out.println("After map: " + s))
                .subscribe(s -> System.out.println("Subscribe: " + s));
    }

    private static void exCollect() {
        globalSource.collect(HashSet::new, HashSet::add).subscribe(System.out::println);
    }

    private static void exToMap() {
        globalSource.toMap(t -> t.charAt(1)).subscribe(System.out::println);
    }

    private static void exScan() {
        globalIntegerSource.scan((i, t) -> {
            return i - t;
        }).subscribe(r -> System.out.print(r + ", "));

        printSeparators();
        globalSource.scan((i, t) -> {
            return i + t;
        }).subscribe(r -> System.out.print(r + ", "));

        printSeparators();
        // 0 acts as initial value
        globalSource.scan(0, (i, t) -> {
            return i + t.length();
        }).subscribe(r -> System.out.print(r + ", "));

    }

    private static void exSorted() {
        System.out.println("\nREVERSE ORDER: ");
        globalIntegerSource.sorted(Comparator.reverseOrder())
                .subscribe(t -> System.out.print(t + ", "));
        System.out.println("\nREVERSE ORDER: ");
        globalSource.sorted(Comparator.reverseOrder())
                .subscribe(t -> System.out.print(t + ", "));
        System.out.println("\nLENGTH: ");
        globalSource.sorted((x, y) -> Integer.compare(x.length(), y.length()))
                .subscribe(t -> System.out.print(t + ", "));
        System.out.println("\nCHAR AT 1: ");
        globalSource.sorted((x, y) -> Character.compare(x.charAt(1), y.charAt(1)))
                .subscribe(t -> System.out.print(t + ", "));
    }

    private static void exMap2() {
        globalSource.map(s -> s.toUpperCase()).subscribe(System.out::println);
        printSeparators();
        globalSource.map(s -> s.charAt(0)).subscribe(System.out::println);

    }

    private static void printSeparators() {
        Observable.range(0, 20).subscribe(t -> {
            System.out.print("_");
            if (t == 19)
                System.out.print("\n");
        });
    }

    private static void exTake() {
        globalIntegerSource.take(3)
                .subscribe(t -> System.out.println("take() -> " + t));
        printSeparators();
        globalIntegerSource.takeLast(3)
                .subscribe(t -> System.out.println("takeLast() -> " + t));
        printSeparators();
        globalIntegerSource.skip(3)
                .subscribe(t -> System.out.println("skip() -> " + t));
        printSeparators();
        globalIntegerSource.skipLast(3)
                .subscribe(t -> System.out.println("skipLast() -> " + t));
        printSeparators();
        globalIntegerSource.take(1, TimeUnit.NANOSECONDS)
                .subscribe(t -> System.out.println("take(., .) -> " + t));
        printSeparators();
        globalIntegerSource.takeWhile(i -> i < 5)
                .subscribe(t -> System.out.println("takeWhile() -> " + t));
        printSeparators();
        globalIntegerSource.takeUntil(i -> i < 5)
                .subscribe(t -> System.out.println("takeUntil() -> " + t));
    }

    private static void exDisposable() {
        Observable<String> source = Observable.just("A", "B", "C", "D", "E", "F");
        source.subscribe(new Observer<String>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                this.disposable = d;

            }

            @Override
            public void onNext(String t) {
                System.out.println("onNext: " + t);

            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e);

            }

            @Override
            public void onComplete() {
                disposable.dispose();
                System.out.println("Complete");

            }
        });

    }

    private static void exSingle() {
        Observable<String> source = Observable.just("A", "B", "C");
        source.first("Nil").subscribe(t -> System.out.println("If not empty, " + t));

        Observable<String> source2 = Observable.empty();
        source2.first("Nil").subscribe(t -> System.out.println("If empty, " + t));

        Single<String> source3 = Single.just("A");
        source3.subscribe(t -> System.out.println("Single -> " + t));
    }

    private static void exFromCallable() {
        Observable.fromCallable(() -> 1 / 0).subscribe(i -> System.out.println(i), Throwable::printStackTrace);
        Observable.fromCallable(() -> 100 / 10).subscribe(i -> System.out.println(i), Throwable::printStackTrace);
    }

    private static void exDefer() {
        Observable<Integer> source = Observable.defer(() -> Observable.range(start, count));

        count = 5;
        source.subscribe(i -> System.out.println("Observable 1 : " + i));

        start = 3;
        count = 10;
        source.subscribe(i -> System.out.println("Observable 2 : " + i));
    }

    private static void exRange() {
        Observable.range(0, 100).filter(t -> {
            return t % 2 == 0;
        }).subscribe(i -> System.out.println(i));
    }

    private static void exInterval() {
        Observable.interval(2, TimeUnit.SECONDS).subscribe(t -> System.out.println(t));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void exConnectableObservables() {
        // publish() will convert any Observable to ConnectableObservable
        ConnectableObservable<String> source = Observable
                .fromIterable(Arrays.asList("Alpha", "Beta", "Gamma", "Delta", "Epsilon")).publish();
        source.subscribe(t -> System.out.println("Observer 1: " + t));
        // connect() will start firing its emissions

        source.map(String::length).subscribe(t -> System.out.println("Observer 2: " + t));
        source.connect();

    }

    private static void exThreeParamsOfSubscribe() {
        List<String> list = Arrays.asList("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        Observable.fromIterable(list).map(String::toUpperCase).subscribe(s -> System.out.println(s),
                Throwable::printStackTrace, () -> System.out.println("DONE!!!! On complete()"));

    }

    private static void exFromIterables() {
        List<String> list = Arrays.asList("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        Observable.fromIterable(list).map(String::toUpperCase).subscribe(element -> System.out.println(element));

    }

    private static void exOnNext() {
        Observable<String> source = Observable.create(emitter -> {
            emitter.onNext("Alpha");
            emitter.onNext("Beta");
            emitter.onNext("Gamma");
            emitter.onNext("Delta");
            emitter.onNext("Epsilon");
            emitter.onComplete();
        });
        // :: is called Method Reference. It is basically a reference to a single
        // method.
        Observable<Integer> lengths = source.map(String::length);
        Observable<Integer> filters = lengths.filter(s -> s > 5);
        filters.subscribe(s -> System.out.println("RECEIVED: " + s), Throwable::printStackTrace);

    }

    public static void exFilters() {
        Observable<String> observable = Observable.just("Nepal", "America", "China", "North Korea");
        observable.filter(s -> (s.length() > 6)).subscribe(s -> System.out.println(s));
    }

    public static void exMaps() {
        Observable.just("Nepal", "China", "India", "America", "Japan").map(s -> s.length())
                .subscribe(s -> System.out.println(s));
    }

}
