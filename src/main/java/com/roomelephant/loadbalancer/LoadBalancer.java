package com.roomelephant.loadbalancer;

import com.roomelephant.loadbalancer.stategy.roundrobin.LoadBalancerStrategyRoundRobin;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class LoadBalancer {
    private final Set<ServerDetails> serversDetails;
    private final LoadBalancerStrategy strategy;

    public LoadBalancer(Set<ServerDetails> serversDetails, LoadBalancerStrategy strategy) {
        this.serversDetails = serversDetails;
        this.strategy = strategy;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Set<ServerDetails> serversDetails = new HashSet<>();
        private LoadBalancerStrategy strategy = new LoadBalancerStrategyRoundRobin();

        public Builder withServer(String name, URI url) {
            this.serversDetails.add(new ServerDetails(name, url));
            return this;
        }

        public Builder withServer(ServerDetails serversDetails) {
            this.serversDetails.add(serversDetails);
            return this;
        }

        public Builder withServers(Set<ServerDetails> serversDetails) {
            if (serversDetails.isEmpty()) {
                this.serversDetails = serversDetails;
            } else {
                this.serversDetails.addAll(serversDetails);
            }
            return this;
        }

        public Builder withStrategy(LoadBalancerStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public LoadBalancer build() {
            return new LoadBalancer(serversDetails, strategy);
        }
    }
}
