package com.roomelephant.loadbalancer.stategy.roundrobin;

import com.roomelephant.loadbalancer.ServerDetails;

public record RoundRobinServer(ServerDetails info) {

    public static RoundRobinServer fromServerInfo(ServerDetails serverDetails) {
        return new RoundRobinServer(serverDetails);
    }
}
