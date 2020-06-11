package com.udemy.learn.reactive.spring.playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandlingTest() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Error occurred")))
                .concatWith(Flux.just("D"))
                .onErrorResume((err) -> {
                    System.out.println("Error occurred : " + err);
                    return Flux.just("default", "default1");
                });

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                //.expectError(RuntimeException.class)
                .expectNext("default", "default1")
                .verifyComplete();

    }

    @Test
    public void fluxErrorHandling_OnErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Error occurred")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default");

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                //.expectError(RuntimeException.class)
                .expectNext("default")
                .verifyComplete();
    }


    @Test
    public void fluxErrorHandling_OnErrorMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Error occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap((err) -> new CustomException(err));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                //.expectNext("default")
                .verify();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap_withRetry() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Error occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap((err) -> new CustomException(err))
                .retry(2);

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                //.expectNext("default")
                .verify();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap_withRetryBackoff() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Error occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap((err) -> new CustomException(err))
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(5))); // Replaced retryBackoff with retryWhen as retryBackoff is deprecated

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(IllegalStateException.class)
                //.expectNext("default")
                .verify();
    }

    private class CustomException extends Throwable {

        private String message;

        @Override
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public CustomException(Throwable err) {
            this.message = err.getMessage();
        }
    }
}
