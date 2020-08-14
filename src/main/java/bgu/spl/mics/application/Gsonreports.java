package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Report;

import java.util.List;

public class Gsonreports {

    report[] reports;
    int total;

    public Gsonreports(List<Report> reports, int total){
        this.total=total;
        this.reports=new report[reports.size()];
        int i=0;
        for(Report r : reports){
            this.reports[i]=new report(r);
            i++;
        }

    }

    class report {
        String missionName;
        int m;
        int moneypenny;
        List<String> agentSerialNumbers;
        List<String> agentsNames;
        String gadgetName;
        int timeCreated;
        int timeIssued;
        int qTime;

        public report(Report report){
            missionName=report.getMissionName();
            m=report.getM();
            moneypenny=report.getMoneypenny();
            agentSerialNumbers=report.getAgentsSerialNumbers();
            agentsNames=report.getAgentsNames();
            gadgetName=report.getGadgetName();
            timeCreated=report.getTimeCreated();
            timeIssued=report.getTimeIssued();
            qTime=report.getQTime();
        }
    }
}
