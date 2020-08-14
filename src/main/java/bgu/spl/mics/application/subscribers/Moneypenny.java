package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Squad;

import java.util.LinkedList;
import java.util.List;

/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {

	private int id;
	private Squad squad;

	public Moneypenny(int id) {
		super("Moneypenny");
		this.id=id;
		this.squad=Squad.getInstance();
	}


	@Override
	protected void initialize() {
		MessageBrokerImpl.getInstance().register(this);
		subscribeBroadcast(TerminateBrodcast.class , message ->{
			this.terminate();
		});
		if( id % 2 != 0) {
			subscribeEvent(AgentsAvailableEvent.class, message -> {
				boolean result = squad.getAgents(message.getAgents());
				if(result){
					message.setMoneypenny_id(id);
					message.setNames(squad.getAgentsNames(message.getAgents()));
				}
				complete(message, result);

			});
		}
		if( id % 2 == 0) {
			subscribeEvent(AgentReleaseEvent.class, message -> {
				squad.releaseAgents(message.getAgents());
				complete(message, true);
			});
			subscribeEvent(AgentSendEvent.class, message -> {
				squad.sendAgents(message.getAgents(), message.getDuration());
				complete(message, true);
			});
		}
	}

	public static void releaseAgents(List<String> agents){
		Squad.getInstance().releaseAgents(agents);
	}
}
