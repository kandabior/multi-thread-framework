package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TerminateBrodcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
	private List<MissionInfo> info;


	public Intelligence(MissionInfo[] infos) {
		super("Intelligence");
		info=new LinkedList<>();
		for(MissionInfo m: infos) {
			info.add(m);
		}
	}

	@Override
	protected void initialize() {
		MessageBrokerImpl.getInstance().register(this);
		subscribeBroadcast(TickBroadcast.class, (message)->{
			int CurrentTick = message.getCurrentTime();
			for(MissionInfo mission: info){
				if(mission.getTimeIssued()==CurrentTick){
					getSimplePublisher().sendEvent(new MissionReceivedEvent(mission));
					}
				}

		});
		subscribeBroadcast(TerminateBrodcast.class, message -> {
			terminate();
		});
	}
}
