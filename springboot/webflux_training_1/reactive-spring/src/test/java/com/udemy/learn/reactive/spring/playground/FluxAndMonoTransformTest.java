package com.udemy.learn.reactive.spring.playground;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap() {
        Flux<String> namesFlux = Flux.fromIterable(names);
        StepVerifier
                .create(namesFlux.map(str -> StringUtils.capitalize(str)).log())
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .verifyComplete();
    }


    @Test
    public void transformUsingMap_toLength() {
        Flux<String> namesFlux = Flux
                .fromIterable(names)
                .log()
                .repeat(1);
        StepVerifier
                .create(namesFlux.map(str -> str.length()))
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMapAndFilter() {
        Flux<String> namesFlux = Flux
                .fromIterable(names)
                .filter(str -> str.length() > 4).map(str -> StringUtils.capitalize(str))
                .log();
        StepVerifier
                .create(namesFlux)
                .expectNext("Jenny")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {
        Flux<String> stringFlux = Flux
                .fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(str -> Flux.fromIterable(convertToList(str)))
                .log();

        StepVerifier
                .create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapParallel() {
        Flux<String> stringFlux = Flux
                .fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .flatMap(f -> {
                    return f.map(this::convertToList).subscribeOn(Schedulers.parallel());
                })
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier
                .create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String str) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(str, "newValue");
    }


    @Test
    public void transformUsingFlatMap_InParallel() {
        Flux<String> stringFlux = Flux
                .fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .flatMap(f -> {
                    return f.map(this::convertToList).subscribeOn(Schedulers.parallel());
                })
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier
                .create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }


}
