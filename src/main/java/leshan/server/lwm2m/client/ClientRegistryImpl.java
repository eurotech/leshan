/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package leshan.server.lwm2m.client;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In memory client registry
 */
public class ClientRegistryImpl implements ClientRegistry {

    private ConcurrentHashMap<String /*end-point*/, Client> clientsByEp = new ConcurrentHashMap<>();

    private List<RegistryListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void addListener(RegistryListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(RegistryListener listener) {
        listeners.remove(listener);
    }

    @Override
    public Collection<Client> allClients() {
        return clientsByEp.values();
    }

    @Override
    public Client get(String endpoint) {
        return clientsByEp.get(endpoint);
    }

    @Override
    public Client registerClient(Client client) {
        Client previous = clientsByEp.put(client.getEndpoint(), client);
        if (previous != null) {
            for (RegistryListener l : listeners) {
                l.unregistered(previous);
            }
        }
        for (RegistryListener l : listeners) {
            l.registered(client);
        }

        return previous;
    }

    @Override
    public Client deregisterClient(String endpoint) {
        Client c = clientsByEp.remove(endpoint);
        for (RegistryListener l : listeners) {
            l.unregistered(c);
        }
        return c;
    }
}
