package com.roomelephant.loadbalancer.stategy.roundrobin;

import com.roomelephant.loadbalancer.ServerDetails;
import com.roomelephant.loadbalancer.stategy.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static org.junit.jupiter.api.Assertions.*;

class LoadBalancerStrategyRoundRobinTest {
    LoadBalancerStrategyRoundRobin victim;
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    @BeforeEach
    void setUp() {
        victim = new LoadBalancerStrategyRoundRobin();
    }

    @Test
    void addSuccessfully() throws URISyntaxException {
        assertTrue(victim.add(new ServerDetails("local", new URI("127.0.0.1"))));
    }

    @Test
    void addAllSuccessfully() throws URISyntaxException {
        HashSet<ServerDetails> servers = new HashSet<>();
        servers.add(new ServerDetails("local1", new URI("127.0.0.1")));
        servers.add(new ServerDetails("local2", new URI("127.0.0.2")));
        servers.add(new ServerDetails("local3", new URI("127.0.0.3")));

        assertTrue(victim.addAll(servers));
    }

    @Test
    void activeServersSuccessfully() throws URISyntaxException {
        HashSet<ServerDetails> servers = new HashSet<>();
        servers.add(new ServerDetails("local1", new URI("127.0.0.1")));
        servers.add(new ServerDetails("local2", new URI("127.0.0.2")));
        servers.add(new ServerDetails("local3", new URI("127.0.0.3")));

        assertTrue(victim.addAll(servers));
        assertEquals(3, victim.activeServers());
    }

    @Test
    void getServerSuccessfully() throws URISyntaxException {
        ServerDetails server = new ServerDetails("local", new URI("127.0.0.1"));
        victim.add(server);

        ServerDetails result = victim.getNext();

        assertEquals(server, result);
    }

    @Test
    void getServerFirstElementSuccessfully() throws URISyntaxException {
        ServerDetails server = new ServerDetails("local", new URI("127.0.0.1"));
        ServerDetails server2 = new ServerDetails("local2", new URI("127.0.0.2"));
        victim.add(server);
        victim.add(server2);

        ServerDetails result = victim.getNext();

        assertEquals(server, result);
    }

    @Test
    void getServerSecondElementSuccessfully() throws URISyntaxException {
        ServerDetails server = new ServerDetails("local", new URI("127.0.0.1"));
        ServerDetails server2 = new ServerDetails("local2", new URI("127.0.0.2"));
        victim.add(server);
        victim.add(server2);

        victim.getNext();
        ServerDetails result2 = victim.getNext();
        assertEquals(server2, result2);
    }

    @Test
    void getServerFirstElementAfterUsingAllOthersSuccessfully() throws URISyntaxException {
        ServerDetails server = new ServerDetails("local", new URI("127.0.0.1"));
        ServerDetails server2 = new ServerDetails("local2", new URI("127.0.0.2"));
        victim.add(server);
        victim.add(server2);

        victim.getNext();
        victim.getNext();
        ServerDetails result = victim.getNext();
        assertEquals(server, result);
    }

    @Test
    void getServerDistributionSuccessfully() throws URISyntaxException {
        ServerDetails server = new ServerDetails("local", new URI("127.0.0.1"));
        ServerDetails server2 = new ServerDetails("local2", new URI("127.0.0.2"));
        ServerDetails server3 = new ServerDetails("local3", new URI("127.0.0.3"));
        victim.add(server);
        victim.add(server2);
        victim.add(server3);

        List<String> results = new ArrayList<>();
        executorService.submit(() -> {
            for (int i = 0; i < 30; i++) {
                String name = victim.getNext().name();
                results.add(name);
            }
        });

        Map<String, List<String>> resultByServer = results.stream().collect(groupingBy(Function.identity()));
        resultByServer.forEach((key, value) -> assertEquals(value.size(), 10));
    }

    @Test
    void getServerFailsWithRuntimeExceptionOnEmptyArray() {
        assertThrows(RuntimeException.class, () -> victim.getNext());
    }


    @Test
    void getStrategySuccessfully() {
        assertEquals(Strategy.ROUND_ROBIN, victim.getStrategy());
    }


}