package org.testcontainers.utility;

import org.testcontainers.ContainerControllerFactory;
import org.testcontainers.containers.ClientFactoryReplacement;
import org.testcontainers.containers.MachineClientProviderStrategyReplacement;
import org.testcontainers.controller.ContainerProvider;

/**
 * Provides utility methods for determining facts about the test environment.
 */
public class TestEnvironment {

    private TestEnvironment() {
    }

    public static boolean dockerApiAtLeast(String minimumVersion) {
        ComparableVersion min = new ComparableVersion(minimumVersion);
        ComparableVersion current = new ComparableVersion(ClientFactoryReplacement.instance().getActiveApiVersion());

        return current.compareTo(min) >= 0;
    }

    /**
     *
     * @deprecated Use {@link ContainerProvider#supportsExecution()}
     */
    @Deprecated // TODO: Remove
    public static boolean dockerExecutionDriverSupportsExec() {
        return ContainerControllerFactory.instance().supportsExecution();
    }

    public static boolean dockerIsDockerMachine() {
        return ClientFactoryReplacement.instance().isUsing(MachineClientProviderStrategyReplacement.class);
    }
}

