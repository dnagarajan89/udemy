package com.udemy.learn.reactive.spring.playground;


import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTest {

    @Test
    public void testFlux() {
        Flux<String> stringFlux = Flux.just("anna", "peter", "jenny", "rachel");
        StepVerifier.create(stringFlux.log())
                .expectNext("anna", "peter", "jenny", "rachel")
                .verifyComplete();
    }

    @Test
    public void testMono() {
        Mono<String> stringMono = Mono.just("anna");
        StepVerifier.create(stringMono.log())
                .expectNext("anna")
                .verifyComplete();
    }

    @Test
    public void testFlux_withError() {
        Flux<String> stringFlux = Flux.just("anna", "peter", "jenny", "rachel")
                .concatWith(Flux.error(new RuntimeException("Test Error")));

        StepVerifier.create(stringFlux.log())
                .expectNext("anna", "peter", "jenny", "rachel")
                .expectErrorMatches((err) -> err instanceof RuntimeException)
                .verify();
    }


}
