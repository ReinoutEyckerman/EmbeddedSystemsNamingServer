package com.bonkers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public Server() throws IOException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        List<Callable<Double>> Callables = Arrays.asList(
                new GetIPThread(),
                new RMIServer()
        );
        executor.invokeAll(Callables).stream().map(future -> {
            try {
                return future.get();
            }
            catch (Exception e)
            {
                throw new IllegalStateException(e);
            }
        }).forEach(System.out::println);
        executor.shutdownNow();
    }
}

