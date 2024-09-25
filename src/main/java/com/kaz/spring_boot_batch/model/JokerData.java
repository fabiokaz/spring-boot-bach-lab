package com.kaz.spring_boot_batch.model;

import java.util.StringJoiner;

public record JokerData(String name, String email, String birth, JokeData jokeData) {
    @Override
    public String toString() {
        return new StringJoiner(", ", JokerData.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("email='" + email + "'")
                .add("birth='" + birth + "'")
                .add("jokeData=" + jokeData)
                .toString();
    }
}
