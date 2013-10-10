package org.nolat.scrolls.utils;

import org.nolat.scrolls.network.ScrollsConnection;

/**
 * Creates a connection to the Scrolls load balancing server in order to retrieve an IP address for the main lobby.
 */
public class ScrollsLoadBalancer {

    public ScrollsLoadBalancer() {
        ScrollsConnection connection = new ScrollsConnection();
    }
}
