package com.oneitthing.swingcontrollerizer.listener;

import java.util.EventListener;

public interface ComponentCreationListener extends EventListener {
    public void componentCreated(ComponentCreationEvent event);
}
