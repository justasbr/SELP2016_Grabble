package com.example.justas.grabble.helper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Based on stackOverflow example
public final class Callbacks {
    private static final Callback<Object> EMPTY = new Callback<Object>() {
        @Override
        public void onResponse(Call<Object> call, Response<Object> response) {
        }

        @Override
        public void onFailure(Call<Object> call, Throwable t) {
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> Callback<T> empty() {
        return (Callback<T>) EMPTY;
    }
}

