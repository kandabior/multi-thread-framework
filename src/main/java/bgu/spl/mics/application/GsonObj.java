package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Squad;

public class GsonObj {

     String[] inventory;
     Service services;
     Agent[] squad;



    public class Service {
        int M;
        int Moneypenny;
        GsonIntelligence[] intelligence;
        int time;

         class GsonIntelligence{
             MissionInfo[] missions;
         }
    }

}


