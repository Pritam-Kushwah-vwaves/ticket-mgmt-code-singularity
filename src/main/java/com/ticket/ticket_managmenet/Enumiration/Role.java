package com.ticket.ticket_managmenet.Enumiration;

import java.util.Set;

public enum Role {

    ADMIN(Set.of(
            Permissions.TICKET_VIEW_ALL,
            Permissions.TICKET_ASSIGN,
            Permissions.USER_VIEW,
            Permissions.TICKET_CREATE
    )),

    GENERAL(Set.of(
            Permissions.TICKET_CREATE,
            Permissions.TICKET_VIEW_OWN
    ));

    private final Set<Permissions> permissions;

    Role(Set<Permissions> permissions) {
        this.permissions = permissions;
    }

    public Set<Permissions> getPermissions() {
        return permissions;
    }
}

