package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.List;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {
	private int M_id;
	private Diary diary;
	private int currentTime;


	public M(int M_id) {
		super("M");
		diary = Diary.getInstance();
		this.M_id=M_id;
	}

	@Override
	protected void initialize() {
		MessageBrokerImpl.getInstance().register(this);
		subscribeBroadcast(TerminateBrodcast.class, message -> {
			this.terminate();
		});
		subscribeBroadcast(TickBroadcast.class, message -> {
			if (currentTime < message.getCurrentTime()) {
				currentTime = message.getCurrentTime();
			}
		});
		subscribeEvent(MissionReceivedEvent.class, message -> {
			diary.incrementTotal();
			List<String> agents = message.getMissionInfo().getSerialAgentsNumbers();
			AgentsAvailableEvent agentsAvailableEvent = new AgentsAvailableEvent(agents);
			Future<Boolean> futureAgent = getSimplePublisher().sendEvent(agentsAvailableEvent);
			if (futureAgent == null) {
				complete(message, false);
				return;
			}
			else{
			Boolean result = futureAgent.get();
				if (result == null || !result ){
					complete(message, false);
					return;
				}
			}

			//agent is available
			String gadget = message.getMissionInfo().getGadget();
			GadgetAvailableEvent gadgetAvailableEvent = new GadgetAvailableEvent(gadget);
			Future<Boolean> futureGadget = getSimplePublisher().sendEvent(gadgetAvailableEvent);
			if (futureGadget == null || futureGadget.get() == null || !futureGadget.get()) {
				AgentReleaseEvent agentReleaseEvent = new AgentReleaseEvent(agents);
				Future<Boolean> future = getSimplePublisher().sendEvent(agentReleaseEvent);
				complete(message, false);
			} else {
				if (currentTime >= message.getMissionInfo().getTimeExpired()) {
					AgentReleaseEvent agentReleaseEvent = new AgentReleaseEvent(agents);
					Future<Boolean> future = getSimplePublisher().sendEvent(agentReleaseEvent);
					complete(message, false);
				} else {
					int duration = message.getMissionInfo().getDuration();
					AgentSendEvent agentSendEvent = new AgentSendEvent(agents, duration);
					Future<Boolean> future = getSimplePublisher().sendEvent(agentSendEvent);
					if (future != null) {
						Report report = createReport(message, agentsAvailableEvent, gadgetAvailableEvent);
						diary.addReport(report);
						complete(message, true);
					}
					complete(message, false);
				}
			}

		});
	}

	private Report createReport(MissionReceivedEvent message, AgentsAvailableEvent agent, GadgetAvailableEvent gadget) {

		Report report = new Report();
		report.setMissionName(message.getMissionInfo().getName());
		report.setM(M_id);
		report.setMoneypenny(agent.getMoneypenny_id());
		report.setAgentsSerialNumbers(message.getMissionInfo().getSerialAgentsNumbers());
		report.setAgentsNames(agent.getNames());
		report.setGadgetName(message.getMissionInfo().getGadget());
		report.setTimeIssued(message.getMissionInfo().getTimeIssued());
		report.setQTime(gadget.getTimeHandled());
		report.setTimeCreated(currentTime);
		return report;
	}

}
