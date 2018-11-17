package com.kkoza.starter.information

import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

@Configuration
class InformationRouter(
        private val reactiveWebApplicationContext: ReactiveWebApplicationContext
) {

    @Bean
    fun helloWorld(): RouterFunction<ServerResponse> = RouterFunctions.route(
            RequestPredicates.GET("/status/hello"),
            HandlerFunction { ServerResponse.ok().body(Mono.just("Hello world!")) })

    @Bean
    fun getBeans(): RouterFunction<ServerResponse> = RouterFunctions.route(
            RequestPredicates.GET("/status/beans"),
            HandlerFunction { ServerResponse.ok().body(Mono.just(reactiveWebApplicationContext.beanDefinitionNames)) }
    )

}