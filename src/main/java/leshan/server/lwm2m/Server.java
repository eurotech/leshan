package leshan.server.lwm2m;

import leshan.server.lwm2m.client.Client;
import leshan.server.lwm2m.client.ClientRegistry;
import leshan.server.lwm2m.resource.RegisterResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.inf.vs.californium.server.resources.Resource;

/**
 * A Lightweight M2M server.
 * <p>
 * It is a simplified version of a Resource Directory as described in the CoRE RD specification The CoAP
 * {@link Resource} tree is used to host the description of all the registered LW-M2M clients. {@link Client} lookups
 * can be performed through the {@link ClientRegistry}.
 * </p>
 * <p>
 * A {@link RequestHandler} is provided to perform server-initiated requests to LW-M2M clients.
 * </p>
 */
public class Server extends ch.ethz.inf.vs.californium.server.Server {

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    /** IANA assigned UDP port for CoAP (so for LWM2M) */
    private static final int port = 5684;

    private final ClientRegistry clientRegistry;

    private final RequestHandler requestHandler;

    public Server() {
        super(port);

        RegisterResource rdResource = new RegisterResource();
        this.add(rdResource);

        this.clientRegistry = rdResource;
        this.requestHandler = new RequestHandler(this.getEndpoints().get(0));
    }

    @Override
    public void start() {
        super.start();
        LOG.info("LW-M2M server started on port " + port);
    }

    public ClientRegistry getClientRegistry() {
        return this.clientRegistry;
    }

    public RequestHandler getRequestHandler() {
        return this.requestHandler;
    }

}
