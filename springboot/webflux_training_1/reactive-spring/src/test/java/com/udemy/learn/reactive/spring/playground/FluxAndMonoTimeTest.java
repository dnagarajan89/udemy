package com.udemy.learn.reactive.spring.playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoTimeTest {

    @Test
    public void fluxInfiniteSequence() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100));
        infiniteFlux.log().subscribe(elem -> System.out.println("Value is : " + elem));
        Thread.sleep(3000);
    }


    @Test
    public void testFluxInfiniteSequence() {
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .take(3)
                .log();
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void finiteSequenceMap() {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .map(l -> Integer.valueOf(l.intValue()))
                .take(3)
                .log();
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

    @Test
    public void finiteSequenceMap_withDelay() {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .delayElements(Duration.ofSeconds(1))
                .map(l -> Integer.valueOf(l.intValue()))
                .take(3)
                .log();
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

}
