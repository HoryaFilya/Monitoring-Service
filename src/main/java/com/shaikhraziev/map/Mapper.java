package com.shaikhraziev.map;

public interface Mapper<F, T> {

    T map(F object);

    default T map(F objectFrom, T objectTo) {
        return objectTo;
    };
}