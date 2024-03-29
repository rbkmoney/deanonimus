package com.rbkmoney.deanonimus.kafka.handler.party.management;


import com.rbkmoney.geck.filter.Filter;
import org.apache.commons.lang3.NotImplementedException;

public interface Handler<T, E> {

    default boolean accept(T change) {
        return getFilter().match(change);
    }

    default void handle(T change, E event, Integer changeId) {
        throw new NotImplementedException("Override it!");
    }

    Filter<T> getFilter();

}
