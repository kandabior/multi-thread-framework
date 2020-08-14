package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;


import java.util.List;


public class GadgetAvailableEvent implements Event<Boolean> {

    private String gadget;
    private int timeHandled;

    public GadgetAvailableEvent(String gadget){
        this.gadget=gadget;
    }

    public String getGadget() {
        return gadget;
    }

    public void setTimeHandled(int timeHandled) {
        this.timeHandled = timeHandled;
    }

    public int getTimeHandled() {
        return timeHandled;
    }
}
