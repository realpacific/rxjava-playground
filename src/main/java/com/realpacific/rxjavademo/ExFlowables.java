package com.realpacific.rxjavademo;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class ExFlowables {

    public static void main(String[] args) {
        // exFlowable1();
        // exFlowableSubscriber();
//	exFlowableCustomSubscriber();
        exFlowableCustomSubscriberWithCustomNumberOfEmissions();
    }

    private static void exFlowableCustomSubscriberWithCustomNumberOfEmissions() {
        Flowable.range(1, 1000)
                .doOnNext(s -> System.out.println("Source pushed " + s)).observeOn(Schedulers.io())
                .map(i -> intenseCalculation(i)).subscribe(new Subscriber<Integer>() {

            Subscription subscription;
            AtomicInteger count = new AtomicInteger(0);

            @Override
            public void onSubscribe(Subscription s) {
                this.subscription = s;
                s.request(40);
            }

            @Override
            public void onNext(Integer t) {
                sleep(50);
                System.out.println("on Next: " + t);
                if (count.incrementAndGet() % 20 == 0 && count.get() >= 40) {
                    System.out.println("Requesting 20---------------");
                    subscription.request(20);
                }

            }


            @Override
            public void onError(Throwable t) {
                System.out.println("ERRor");
            }

            @Override
            public void onComplete() {
                System.out.println("-----------THE END ");
            }

        });
        sleep(Integer.MAX_VALUE);

    }

    private static void exFlowableCustomSubscriber() {
        Flowable.range(1, 1000).doOnNext(s -> System.out.println("Source pushed " + s)).observeOn(Schedulers.io())
                .map(i -> intenseCalculation(i)).subscribe(new Subscriber<Integer>() {

            Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                this.subscription = s;
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("ERRor");
            }

            @Override
            public void onComplete() {
                System.out.println("-----------THE END ");
            }

            @Override
            public void onNext(Integer t) {
                sleep(50);
                System.out.println("on Next: " + t);

            }
        });
        sleep(Integer.MAX_VALUE);

    }

    private static void exFlowableSubscriber() {
        Flowable.range(1, 1000).doOnNext(s -> System.out.println("Source pushed " + s)).observeOn(Schedulers.io())
                .map(i -> intenseCalculation(i)).subscribe(s -> System.out.println("REceived " + s));
        sleep(Integer.MAX_VALUE);
    }

    private static void exFlowable1() {
        Flowable.range(1, 9999).map(MyItem::new).observeOn(Schedulers.io()).subscribe(myItem -> {
            sleep(1);
            System.out.println("----------------Received MyItem " + myItem.id);
        });
        sleep(Long.MAX_VALUE);
    }

    static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static <T> T intenseCalculation(T value) {
        // sleep up to 200 milliseconds
        sleep(ThreadLocalRandom.current().nextInt(200));
        return value;
    }

    static final class MyItem {
        final int id;

        MyItem(int id) {
            this.id = id;
            System.out.println("Constructing MyItem " + id);
        }
    }
}
