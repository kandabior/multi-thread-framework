package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class AgentSendEvent implements Event<Boolean> {
    private List<String> agents;
    private int duration;

    public AgentSendEvent(List<String> agents, int duration){
        this.agents = agents;
        this.duration = duration;
    }

    public List<String> getAgents() {
        return agents;
    }

    public int getDuration() {
        return duration;
    }
}
