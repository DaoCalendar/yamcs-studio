package org.csstudio.platform.libs.yamcs;

import java.net.URI;
import java.net.URISyntaxException;

public class YamcsConnectionProperties {
    private String host;
    private int port;
    private String instance;
    
    public YamcsConnectionProperties(String host, int port, String instance) {
        this.host = host;
        this.port = port;
        this.instance = instance;
    }

    public URI webResourceURI(String relativePath) {
        if (!relativePath.startsWith("/")) {
            relativePath = "/" + relativePath;
        }
        try {
            return new URI("http://" + host + ":" + port + "/" + instance + relativePath);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public URI webSocketURI() {
        try {
            return new URI("ws://" + host + ":" + port + "/" + instance + "_websocket");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
