package org.csstudio.utility.pvmanager.yamcs.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.csstudio.utility.pvmanager.yamcs.InvalidIdentification;
import org.csstudio.utility.pvmanager.yamcs.YamcsPVChannelHandler;
import org.csstudio.utility.pvmanager.yamcs.ws.WebSocketClient;
import org.csstudio.utility.pvmanager.yamcs.ws.WebSocketClientCallbackListener;
import org.yamcs.protobuf.NamedObjectId;
import org.yamcs.protobuf.NamedObjectList;
import org.yamcs.protobuf.ParameterData;
import org.yamcs.protobuf.ParameterValue;

/**
 * Combines state accross the many-to-one relation from yamcs:// datasources to
 * the WebSocketClient. Everything yamcs is still in WebSocketClient to keep
 * things a bit clean (and potentially reusable).
 * <p>
 * All methods are asynchronous, with any responses or incoming data being sent
 * to the provided callback listener.
 */
public class YService implements WebSocketClientCallbackListener {
    
    private static final String USER_AGENT = "yamcs-studio"; /// + Activator.getDefault().getBundle().getVersion().toString();
    private static final Logger log = Logger.getLogger(YService.class.getName());
    
    // Store registering channel handlers while connection is not established
    private Map<String, YamcsPVChannelHandler> channelHandlersByName = new LinkedHashMap<>();
    
    private boolean connectionInitialized = false;
    private WebSocketClient wsclient;
    
    public YService() {
        try {
            URI uri = new URI("ws://localhost:8080/local/_websocket");
            wsclient = new WebSocketClient(uri, this);
            wsclient.setUserAgent(USER_AGENT);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI", e);
        }
    }
    
    public synchronized void connectChannelHandler(YamcsPVChannelHandler channelHandler) {
        if (!connectionInitialized) {
            wsclient.connect();
            connectionInitialized = true;
        }
        channelHandlersByName.put(channelHandler.getChannelName(), channelHandler);
        NamedObjectList idList = new NamedObjectList();
        NamedObjectId id = new NamedObjectId(channelHandler.getChannelName());
        if (!channelHandler.getChannelName().startsWith("/")) {
            id.setNamespace("MDB:OPS Name");
        }
        idList.setListList(Arrays.asList(id));
        wsclient.subscribe(idList);
    }
    
    public void disconnect() {
        connectionInitialized = false;
        wsclient.disconnect();
    }
    
    public void shutdown() {
        disconnect();
        wsclient.shutdown();
    }

    @Override
    public void onConnect() { // When the web socket was successfully established
        log.info("Web socket established. Notifying channel-handlers");
        // TODO we should trigger this instead on when the subscription was confirmed
        channelHandlersByName.forEach((name, handler) -> {
            handler.signalYamcsConnected();
        });
    }

    @Override
    public void onDisconnect() { // When the web socket connection state changed
        log.info("Web socket disconnected. Notifying channel-handlers");
        channelHandlersByName.forEach((name, handler) -> handler.signalYamcsDisconnected());
    }

    @Override
    public void onInvalidIdentification(NamedObjectId id) {
        channelHandlersByName.get(id.getName()).reportException(new InvalidIdentification(id));
    }

    @Override
    public void onParameterData(ParameterData pdata) {
        for (ParameterValue pval : pdata.getParameterList()) {
            YamcsPVChannelHandler handler = channelHandlersByName.get(pval.getId().getName());
            if (handler != null) {
                System.out.println("request to update channel "+handler.getChannelName()+" to val "+pval.getEngValue());
                handler.processParameterValue(pval);
            } else {
                System.out.println("No handler for incoming update of " + pval.getId().getName());
            }
        }
    }
}
