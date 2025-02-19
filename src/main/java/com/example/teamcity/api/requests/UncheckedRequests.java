package com.example.teamcity.api.requests;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import io.restassured.specification.RequestSpecification;

import java.util.EnumMap;

public class UncheckedRequests {

    private final EnumMap<Endpoint, UncheckedBase> request = new EnumMap<>(Endpoint.class);

    public UncheckedRequests(RequestSpecification spec) {
        for (var endpoint : Endpoint.values()) {
            request.put(endpoint, new UncheckedBase(spec, endpoint));
        }
    }

    public UncheckedBase getRequest(Endpoint endpoint) {
        return request.get(endpoint);
    }
}
