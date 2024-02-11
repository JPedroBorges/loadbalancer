package com.roomelephant.loadbalancer;

import java.net.URI;

public record ServerDetails(String name, URI url) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerDetails that)) return false;

        return url.equals(that.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
