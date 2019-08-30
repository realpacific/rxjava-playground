package com.realpacific.rxjavademo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.nio.channels.ScatteringByteChannel;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.Subject;

@SuppressWarnings("ALL")
public class Concurrency {
    public static void main(String[] args) {
//         exSimple();
//         exSimpleWithRxJava();
//         exSimpleWithZip();
//         exExecutorService();
//         exNetworkRequest();
//         exObserveOn();
//        exParallerlization();
        exParallerlizationByGrouping();
//        exCustomScheduler();
    }

    private static Observable<String> globalSource = Observable.just("Alpha", "Beta", "Gamma", "Omega", "Theta", "Zeta");
    private static Observable<String> globalNepaliSource = Observable.just("Ek", "Dui", "Teen", "Char", "Panch", "Cha");

    private static void exCustomScheduler() {
        ExecutorService executor = Executors.newFixedThreadPool(200000000);
        Scheduler scheduler = Schedulers.from(executor);
        globalSource.concatMap(s -> Observable.just(s))
                .subscribeOn(scheduler)
                .doFinally(executor::shutdown)
                .subscribe(System.out::println);
    }

    private static void exParallerlizationByGrouping() {
        int coreCount = Runtime.getRuntime().availableProcessors();
        System.out.println("# of cores: " + coreCount);
        AtomicInteger assigner = new AtomicInteger(0);
        Observable.range(10, 20)
                .groupBy(i -> assigner.incrementAndGet() % coreCount)
                .flatMap(grp -> grp.observeOn(Schedulers.io())
                        .map(i2 -> {
                            System.out.println("Value of map = " + i2 + "; KEY = " + grp.getKey());
                            return intenseCalculation(i2);
                        })
                ).subscribe(i -> System.out.println("Received " + i + " "
                + Thread.currentThread().getName()));
        sleep(20000);
    }

    private static void exParallerlization() {
        Observable.range(1, 100)
                .flatMap(
                        i -> Observable.just(i)
                                .subscribeOn(Schedulers.newThread())
                                .map(i2 -> intenseCalculation(i2))
                ).subscribe(i -> System.out.println(i + " " + Thread.currentThread().getName()));
        sleep(20000L);
    }

    private static void exObserveOn() {
        // Happens on IO Scheduler
        Observable.just("WHISKEY/27653/TANGO", "6555/BRAVO", "232352/5675675/FOXTROT")
                .subscribeOn(Schedulers.io())
                .flatMap(s -> Observable.fromArray(s.split("/")))
                .doOnNext(s -> System.out.println("Split out " + s + " on thread " + Thread.currentThread().getName()))
                // Happens on Computation Scheduler
                .observeOn(Schedulers.computation()).filter(s -> s.matches("[0-9]+")).map(Integer::valueOf)
                .reduce((total, next) -> total + next)
                .doOnSuccess(i -> System.out
                        .println("Calculated sum " + i + " on thread " + Thread.currentThread().getName()))
                // Switch back to IO Scheduler
                .observeOn(Schedulers.io()).map(i -> i.toString())
                .doOnSuccess(s -> System.out
                        .println("Writing " + s + " to file on thread " + Thread.currentThread().getName()))
                .subscribe(s -> write(s, "C://cms/output.txt"));
        sleep(3000L);
    }

    public static void write(String text, String path) {
        BufferedWriter writer = null;
        try {
            // create a temporary file
            File file = new File(path);
            writer = new BufferedWriter(new FileWriter(file));
            writer.append(text);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    private static void exNetworkRequest() {
        Observable.fromCallable(() -> getResponse("https://api.github.com/users/realpacific/starred"))
                .subscribeOn(Schedulers.io()).subscribe(System.out::println);
        sleep(10000);
    }

    private static String getResponse(String path) {
        try {
            return new Scanner(new URL(path).openStream(), "UTF-8").useDelimiter("\\A").next();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private static void exExecutorService() {
        ExecutorService executor = Executors.newFixedThreadPool(6);
        Scheduler scheduler = Schedulers.from(executor);
        Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon").map(s -> intenseCalculation(s))
                .subscribeOn(scheduler).doFinally(executor::shutdown).subscribe(System.out::println);
    }

    private static void exSimpleWithZip() {
        Observable<String> source1 = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                .map(s -> intenseCalculation((s)));

        Observable<Integer> source2 = Observable.range(1, 6).map(s -> intenseCalculation((s)));

        Observable.zip(source1, source2, (s, i) -> s + "-" + i).subscribe(System.out::println);
        sleep(20000L);
    }

    private static void exSimpleWithRxJava() {
        Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon").subscribeOn(Schedulers.computation())
                .map(s -> intenseCalculation((s))).subscribe(System.out::println);
        Observable.range(1, 6).map(s -> intenseCalculation((s))).subscribeOn(Schedulers.computation())
                .subscribe(System.out::println);

        sleep(20000L);

    }

    private static void exSimple() {
        Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon").map(s -> intenseCalculation((s)))
                .subscribe(System.out::println);
        Observable.range(1, 6).map(s -> intenseCalculation((s))).subscribe(System.out::println);

    }

    public static <T> T intenseCalculation(T value) {
        sleep(ThreadLocalRandom.current().nextInt(3000));
        return value;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
