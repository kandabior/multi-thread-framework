package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

public class MissionReceivedEvent implements Event {
    private MissionInfo missionInfo;

    public MissionReceivedEvent(MissionInfo info){
        missionInfo=info;
    }

    public MissionInfo getMissionInfo() {
        return missionInfo;
    }


}


