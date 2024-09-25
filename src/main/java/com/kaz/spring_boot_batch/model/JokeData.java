package com.kaz.spring_boot_batch.model;

import java.util.StringJoiner;

public record JokeData(Long id, String setup, String punchline, String type) {
    @Override
    public String toString() {
        return new StringJoiner(", ", JokeData.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("setup='" + setup + "'")
                .add("punchline='" + punchline + "'")
                .add("type='" + type + "'")
                .toString();
    }
}
