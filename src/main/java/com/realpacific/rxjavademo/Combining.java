package com.realpacific.rxjavademo;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;

@SuppressWarnings("ALL")
public class Combining {
    static int start = 0;
    static int count = 10;

    private static Observable<String> globalSource = Observable.just("Alpha", "Beta", "Gamma", "Omega", "Theta", "Zeta");
    private static Observable<String> globalNepaliSource = Observable.just("Ek", "Dui", "Teen", "Char", "Panch", "Cha");

    private static Observable<Integer> globalIntegerSource = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    public static void main(String[] args) {
        System.out.print("globalSource: ");
        globalSource.toList().subscribe(System.out::print);
        System.out.println("");

        System.out.print("globalNepaliSource: ");
        globalNepaliSource.toList().subscribe(System.out::print);
        System.out.println("");

        System.out.print("globalIntegerSource: ");
        globalIntegerSource.toList().subscribe(System.out::print);
        System.out.println("");
        System.out.println("Result: ");

        // exMerge();
        exFlatMap();
        // exAmb();
        // exZipping();
        //	exCombineLatest();
        //	exWithLatestFrom();
//        exGroupBy();
//        exGroupByGetKey();
    }

    private static void exGroupByGetKey() {
        Observable<GroupedObservable<Integer, String>> sources = globalSource.groupBy(s -> s.length());
        sources.flatMapSingle(grp -> grp.reduce("", (x, y) -> x.equals("") ? y : x + ", " + y).map(s -> grp.getKey() + ": " + s)).subscribe(System.out::println);
    }

    private static void exGroupBy() {
        Observable<GroupedObservable<Integer, String>> sources = globalSource.groupBy(s -> s.length());
        //The key holds the name of groups that the streams will be grouped on: In this case, it's 4 & 5.
        sources.map(g -> g.getKey()).subscribe(System.out::println);
        sources.flatMapSingle(grp -> grp.toList()).subscribe(System.out::println);
    }

    private static void exWithLatestFrom() {
        Observable<String> source1 = Observable.interval(500, TimeUnit.MILLISECONDS)
                .concatMap(t -> Observable.fromArray("SOURCE 500s: " + t));
        Observable<String> source2 = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .concatMap(t -> Observable.fromArray("SOURCE 1000s: " + t));
        source2.withLatestFrom(source1, (s2, s1) -> s2 + " -- " + s1).subscribe(System.out::println);
        sleep((long) 5000);

    }

    private static void exCombineLatest() {
        Observable<String> source1 = Observable.interval(500, TimeUnit.MILLISECONDS)
                .concatMap(t -> Observable.fromArray("500s: " + t));
        Observable<String> source2 = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .concatMap(t -> Observable.fromArray("1000s: " + t));
        Observable.combineLatest(source1, source2, (s1, s2) -> s1 + " -- " + s2).subscribe(System.out::println);
        sleep((long) 10000);
    }

    private static void exZipping() {
        Observable.zip(globalNepaliSource, globalIntegerSource, globalSource, (a, i, s) -> a + " " + i + " " + s)
                .subscribe(t -> System.out.println(t));
    }

    private static void exAmb() {
        Observable<String> source1 = Observable.interval(2000, TimeUnit.MILLISECONDS)
                .concatMap(t -> Observable.fromArray("SOURCE 2000s: " + t));
        Observable<String> source2 = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .concatMap(t -> Observable.fromArray("SOURCE 1000s: " + t));

        Observable.amb(Arrays.asList(source1, source2)).subscribe(t -> System.out.println(t));
        sleep((long) 5000);

    }

    private static void exFlatMap() {
        globalSource.flatMap(t -> Observable.fromArray(t.split(""))).subscribe(t -> System.out.print(t + ", "));
        System.out.println("\n");
        globalSource.flatMap(t -> Observable.fromArray(t.split(""))).flatMap(t -> Observable.fromArray(t.toUpperCase()))
                .sorted().distinct().flatMap(t -> Observable.fromArray(t.charAt(0)))
                .flatMap(c -> Observable.fromArray(Integer.valueOf(c))).toList()
                .subscribe(list -> System.out.print(list));
        System.out.println("\n");

        globalSource.flatMap(s -> Observable.fromArray(s.charAt(0)), (x, y) -> (x + "-" + y)).toList()
                .subscribe(list -> System.out.print(list));

        System.out.println("\n");

        globalSource.flatMap(s -> Observable.fromArray(s.charAt(0)), (v, q) -> (v + " - " + q)).toList()
                .subscribe(list -> System.out.print(list));

    }

    @SuppressWarnings("unchecked")
    private static void exMerge() {
        globalSource.mergeWith(globalIntegerSource.map(i -> String.valueOf(i)))
                .subscribe(t -> System.out.print(t + ", "));
        printSeparators();
        Observable.merge(globalSource, globalIntegerSource.map(i -> String.valueOf(i)))
                .subscribe(t -> System.out.print(t + ", "));
        printSeparators();
        Observable
                .mergeArray(globalSource, globalIntegerSource.map(i -> String.valueOf(i)),
                        globalIntegerSource.map(i -> String.valueOf(i * 10)), Observable.empty())
                .subscribe(t -> System.out.print(t + ", "));

    }

    private static void printSeparators() {
        Observable.range(0, 20).subscribe(t -> {
            System.out.print("_");
            if (t == 19)
                System.out.print("\n");
        });
    }

    private static void sleep(Long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
