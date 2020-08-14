package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;


public class TickBroadcast implements Broadcast {
    private int currentTime;


    public TickBroadcast (int duration){
        this.currentTime =duration;

    }

    public int getCurrentTime() {
        return currentTime;
    }


}
