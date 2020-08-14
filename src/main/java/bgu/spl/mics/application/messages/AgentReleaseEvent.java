package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class AgentReleaseEvent implements Event<Boolean> {
    private List<String> agents;

    public AgentReleaseEvent(List<String> agents){
        this.agents=agents;
    }

    public List<String> getAgents() {
        return agents;
    }
}
