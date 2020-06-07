package com.udemy.learn.reactive.spring;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    @Test
    public void monoWithJustOrEmpty() {
        Mono<String> mono = Mono.justOrEmpty(null);
        StepVerifier
                .create(mono.log())
                .verifyComplete();

    }

    @Test
    public void monoFromSupplier() {
        Mono<String> mono = Mono.fromSupplier(() -> "test");
        StepVerifier
                .create(mono.log())
                .expectNext("test")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1, 5);
        StepVerifier
                .create(integerFlux.log())
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

}
