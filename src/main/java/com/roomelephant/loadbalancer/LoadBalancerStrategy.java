package com.roomelephant.loadbalancer;

import com.roomelephant.loadbalancer.stategy.Strategy;

import java.util.Set;

public interface LoadBalancerStrategy {

    boolean add(ServerDetails serverDetails);

    boolean addAll(Set<ServerDetails> serversDetails);

    boolean remove(ServerDetails serverDetails);

    int activeServers();

    ServerDetails getNext();

    Strategy getStrategy();
}
