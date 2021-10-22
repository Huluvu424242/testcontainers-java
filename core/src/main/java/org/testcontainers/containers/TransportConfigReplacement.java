package org.testcontainers.containers;


import com.github.dockerjava.transport.SSLConfig;
import java.net.URI;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

@Builder
@Value
public class TransportConfigReplacement {
    URI dockerHost;

    @Nullable
    SSLConfig sslConfig;
}
