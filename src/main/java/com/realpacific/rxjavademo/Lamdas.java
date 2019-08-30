package com.realpacific.rxjavademo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Lamdas {

    public static void main(String[] args) {
        Supplier<List<String>> suppplier = () -> new ArrayList<>();
        List<String> list = suppplier.get();
    }
}
