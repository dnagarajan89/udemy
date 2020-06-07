package com.udemy.learn.reactive.spring;

import jdk.jfr.FlightRecorder;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoCombineTest {

    @Test
    public void combineUsingMerge() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> fluxMerged = Flux.merge(flux1, flux2);

        StepVerifier
                .create(fluxMerged.log())
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingMerge_withDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> fluxMerged = Flux.merge(flux1, flux2);

        StepVerifier
                .create(fluxMerged.log())
                .expectNextCount(6)
                .verifyComplete();
    }


    @Test
    public void combineUsingConcat_withDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> fluxMerged = Flux.concat(flux1, flux2);

        StepVerifier
                .create(fluxMerged.log())
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }


    @Test
    public void combineUsingZip_withDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> fluxMerged = Flux.zip(flux1, flux2, (t1, t2) -> t1.concat(t2));

        StepVerifier
                .create(fluxMerged.log())
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

}
