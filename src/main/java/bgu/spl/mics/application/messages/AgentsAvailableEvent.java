package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.application.passiveObjects.Agent;

import java.util.List;

public class AgentsAvailableEvent implements Event<Boolean> {

    private  List<String>agents;
    private int Moneypenny_id;
    private List<String > names;


    public AgentsAvailableEvent(List<String> agents){
        this.agents = agents;
    }

    public List<String> getAgents() {
        return agents;
    }

    public void setMoneypenny_id(int moneypenny_id) {
        Moneypenny_id = moneypenny_id;
    }

    public int getMoneypenny_id() {
        return Moneypenny_id;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }


}
