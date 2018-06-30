package com.elytradev.friendshipbracelet.util;

public interface IConfigSerializable {
    public String toConfigString();
    public boolean matches(String configName);
}