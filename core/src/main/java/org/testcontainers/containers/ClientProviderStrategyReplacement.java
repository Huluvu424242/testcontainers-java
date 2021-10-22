package org.testcontainers.containers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class ClientProviderStrategyReplacement {

    public static final int PRIORITY = 100;

    private static final AtomicBoolean FAIL_FAST_ALWAYS = new AtomicBoolean(false);

    /**
     * @return highest to lowest priority value
     */
    protected int getPriority() {
        return 0;
    }

    /**
     * @throws InvalidConfigurationException if this strategy fails
     */
    public abstract TransportConfigReplacement getTransportConfig() throws InvalidConfigurationException;


    /**
     * Determine the right DockerClientConfig to use for building clients by trial-and-error.
     *
     * @return a working DockerClientConfig, as determined by successful execution of a ping command
     */
    public static ClientProviderStrategyReplacement getFirstValidStrategy(List<ClientProviderStrategyReplacement> strategies) {

        if (FAIL_FAST_ALWAYS.get()) {
            throw new IllegalStateException("Previous attempts to find a Docker environment failed. Will not retry. Please see logs and check configuration");
        }

        List<String> configurationFailures = new ArrayList<>();
        List<ClientProviderStrategyReplacement> allStrategies = new ArrayList<>();

        // The environment has the highest priority
        allStrategies.add(new ClientProviderStrategyReplacement());

        // Next strategy to try out is the one configured using the Testcontainers configuration mechanism
        loadConfiguredStrategy().ifPresent(allStrategies::add);

        // Finally, add all other strategies ordered by their internal priority
        strategies
            .stream()
            .sorted(Comparator.comparing(ClientProviderStrategyReplacement::getPriority).reversed())
            .collect(Collectors.toCollection(() -> allStrategies));


        Predicate<ClientProviderStrategyReplacement> distinctStrategyClassPredicate = new Predicate<ClientProviderStrategyReplacement>() {
            final Set<Class<? extends ClientProviderStrategyReplacement>> classes = new HashSet<>();

            public boolean test(ClientProviderStrategyReplacement dockerClientProviderStrategy) {
                return classes.add(dockerClientProviderStrategy.getClass());
            }
        };

        return allStrategies
            .stream()
            .filter(distinctStrategyClassPredicate)
            .filter(ClientProviderStrategyReplacement::isApplicable)
//            .filter(strategy -> tryOutStrategy(configurationFailures, strategy))
            .findFirst()
            .orElseThrow(() -> {
//                log.error("Could not find a valid Docker environment. Please check configuration. Attempted configurations were:");
//                for (String failureMessage : configurationFailures) {
//                    log.error("    " + failureMessage);
//                }
//                log.error("As no valid configuration was found, execution cannot continue");
//
//                FAIL_FAST_ALWAYS.set(true);
                return new IllegalStateException("Could not find a valid Docker environment. Please see logs and check configuration");
            });
    }
}
