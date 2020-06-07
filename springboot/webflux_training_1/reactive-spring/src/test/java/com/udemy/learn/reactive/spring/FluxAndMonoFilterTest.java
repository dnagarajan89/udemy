package com.udemy.learn.reactive.spring;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void filterTest() {
        Flux namesFlux = Flux
                .fromIterable(names)
                .filter(str -> str.startsWith("a"));

        StepVerifier
                .create(namesFlux)
                .expectNext("adam", "anna")
                .verifyComplete();

    }
}
