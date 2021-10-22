package org.testcontainers.containers;

import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;
import lombok.Synchronized;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.testcontainers.UnstableAPI;

public class ClientFactoryReplacement {

    public static final ThreadGroup TESTCONTAINERS_THREAD_GROUP = new ThreadGroup("testcontainers");

    public static final String TESTCONTAINERS_LABEL = ClientFactoryReplacement.class.getPackage().getName();
    public static final String TESTCONTAINERS_SESSION_ID_LABEL = TESTCONTAINERS_LABEL + ".sessionId";
    public static final String SESSION_ID = UUID.randomUUID().toString();

    public static final Map<String, String> DEFAULT_LABELS = ImmutableMap.of(
        TESTCONTAINERS_LABEL, "true",
        TESTCONTAINERS_SESSION_ID_LABEL, SESSION_ID
    );


    private static ClientFactoryReplacement instance;

    /**
     * Obtain an instance of the DockerClientFactory.
     *
     * @return the singleton instance of DockerClientFactory
     */
    public synchronized static ClientFactoryReplacement instance() {
        if (instance == null) {
            instance = new ClientFactoryReplacement();
        }

        return instance;
    }

    @UnstableAPI
    public String getRemoteDockerUnixSocketPath() {
        String dockerSocketOverride = System.getenv("TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE");
        if (!StringUtils.isBlank(dockerSocketOverride)) {
            return dockerSocketOverride;
        }

        URI dockerHost = getTransportConfig().getDockerHost();
        String path = "unix".equals(dockerHost.getScheme())
            ? dockerHost.getRawPath()
            : "/var/run/docker.sock";
        return SystemUtils.IS_OS_WINDOWS
            ? "/" + path
            : path;
    }


    @Synchronized
    private ClientProviderStrategyReplacement getOrInitializeStrategy() {
        if (strategy != null) {
            return strategy;
        }

        List<ClientProviderStrategyReplacement> configurationStrategies = new ArrayList<>();
        ServiceLoader.load(ClientProviderStrategyReplacement.class).forEach(configurationStrategies::add);

        strategy = ClientProviderStrategyReplacement.getFirstValidStrategy(configurationStrategies);
        return strategy;
    }

    @UnstableAPI
    public TransportConfigReplacement getTransportConfig() {
        return getOrInitializeStrategy().getTransportConfig();
    }

}
