# spring-boot-batch-lab

A Spring Boot project designed to merge Joker data with Jokes using Spring Batch asynchronously. This is because the latency of the external application, official_joke_api
And an explicit management of the lifecycle, using context.close() to gracefully shut down the application.

Jokes from https://github.com/15Dkatz/official_joke_api