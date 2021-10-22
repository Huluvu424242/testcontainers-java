package org.testcontainers.containers;

import com.github.dockerjava.core.LocalDirectorySSLConfig;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.utility.CommandLine;
import org.testcontainers.utility.DockerMachineClient;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Use Docker machine (if available on the PATH) to locate a Docker environment.
 *
 * @deprecated this class is used by the SPI and should not be used directly
 */
@Slf4j
@Deprecated
public final class MachineClientProviderStrategyReplacement {

    @Getter(lazy = true)
    private final TransportConfigReplacement transportConfig = resolveTransportConfig();


    private TransportConfigReplacement resolveTransportConfig() throws InvalidConfigurationException {
        boolean installed = DockerMachineClient.instance().isInstalled();
        checkArgument(installed, "docker-machine executable was not found on PATH (" + Arrays.toString(CommandLine.getSystemPath()) + ")");

        Optional<String> machineNameOptional = DockerMachineClient.instance().getDefaultMachine();
        checkArgument(machineNameOptional.isPresent(), "docker-machine is installed but no default machine could be found");
        String machineName = machineNameOptional.get();

        log.info("Found docker-machine, and will use machine named {}", machineName);

        DockerMachineClient.instance().ensureMachineRunning(machineName);

        String dockerDaemonUrl = DockerMachineClient.instance().getDockerDaemonUrl(machineName);

        log.info("Docker daemon URL for docker machine {} is {}", machineName, dockerDaemonUrl);

        return TransportConfigReplacement.builder()
            .dockerHost(URI.create(dockerDaemonUrl))
            .sslConfig(
                new LocalDirectorySSLConfig(
                    Paths.get(System.getProperty("user.home") + "/.docker/machine/certs/").toString()
                )
            )
            .build();
    }


    protected boolean isPersistable() {
        return false;
    }

    protected int getPriority() {
        return ClientProviderStrategyReplacement.PRIORITY - 100;
    }

    public String getDescription() {
        return "docker-machine-replacement";
    }

}
