package com.udemy.learn.reactive.spring.playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import javax.print.attribute.IntegerSyntax;

public class FluxAndMonoBackpressureTest {

    @Test
    public void backPressureTest() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();
        finiteFlux.subscribe(
                element -> System.out.println("Element is : " + element),
                (err -> System.err.println("Exception is : " + err)),
                () -> System.out.println("Completed"),
                (subscription -> subscription.request(2))
        );
    }

    @Test
    public void backPressure_cancel() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();
        finiteFlux.subscribe(
                element -> System.out.println("Element is : " + element),
                (err -> System.err.println("Exception is : " + err)),
                () -> System.out.println("Completed"),
                (subscription -> {
                    subscription.request(2) ;
                    subscription.cancel();
                })
        );
    }

    @Test
    public void backPressure_customized() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();
        finiteFlux.subscribe(new BaseSubscriber<Integer>() {
                                 @Override
                                 protected void hookOnNext(Integer value) {
                                     request(1);
                                     System.out.println("value is : " + value);
                                     if(value == 4) cancel();
                                 }
                             }
        );
    }

}
