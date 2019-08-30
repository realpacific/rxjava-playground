package com.realpacific.rxjavademo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;

public class Test {
    
    static Observable<String> globalSource = Observable.just("Alpha", "Beta", "Gamma", "Omega", "Theta", "Zeta");

    static Observable<Integer> globalIntegerSource = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    @org.junit.Test
    public void testingBlockingSubscriber() {
	AtomicInteger hitCount = new AtomicInteger(0);
	Observable<Long> source = Observable.interval(1, TimeUnit.SECONDS).take(5);
	source.blockingSubscribe(i -> hitCount.incrementAndGet());
	assertTrue(hitCount.get() == 5);
	
    }
    
    @org.junit.Test
    public void testingBlockingFirst() {
	Observable<String> source = globalSource.filter(t -> t.length() == 4);
	assertTrue(source.blockingFirst().equals("Beta"));
	
    }
    
    @org.junit.Test
    public void testingBlockingIterable() {
	Observable<String> source = globalSource.filter(t -> t.length() == 4);
	Iterable<String> itr = source.blockingIterable();
	for(String s : itr) {
	    assertTrue(s.length() >= 3);
	}
	
    }
    @org.junit.Test
    public void testingBlockingForEach() {
	Observable<String> source = globalSource.filter(t -> t.length() == 4);
	source.filter(s -> s.length() == 5).blockingForEach(s -> assertTrue(s.length() == 5));	
    }
}
