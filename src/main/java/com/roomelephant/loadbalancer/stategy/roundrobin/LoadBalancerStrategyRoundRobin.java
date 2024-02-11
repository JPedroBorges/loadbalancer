package com.roomelephant.loadbalancer.stategy.roundrobin;

import com.roomelephant.loadbalancer.LoadBalancerStrategy;
import com.roomelephant.loadbalancer.ServerDetails;
import com.roomelephant.loadbalancer.stategy.Strategy;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancerStrategyRoundRobin implements LoadBalancerStrategy {

    private static final Strategy strategy = Strategy.ROUND_ROBIN;
    private final ArrayList<RoundRobinServer> servers = new ArrayList<>();
    private final AtomicInteger position = new AtomicInteger(0);

    @Override
    public boolean add(ServerDetails serverDetails) {
        return this.servers.add(RoundRobinServer.fromServerInfo(serverDetails));
    }

    @Override
    public boolean addAll(Set<ServerDetails> serversDetails) {
        return serversDetails.stream().map(this::add).reduce(false, (result, wasAdded) -> result || wasAdded);
    }

    @Override
    public boolean remove(ServerDetails serverDetails) {
        synchronized (this) {
            return this.servers.remove(RoundRobinServer.fromServerInfo(serverDetails));
        }
    }

    @Override
    public int activeServers() {
        return this.servers.size();
    }

    @Override
    public ServerDetails getNext() {
        if (servers.isEmpty()) throw new RuntimeException();
        int currentPosition = position.getAndIncrement();

        if (currentPosition >= servers.size()) {
            synchronized (this) {
                currentPosition = position.get();
                if (currentPosition >= servers.size()) {
                    position.set(1);
                    currentPosition = 0;
                }
            }
        }

        return servers.get(currentPosition).info();
    }

    @Override
    public Strategy getStrategy() {
        return strategy;
    }
}
