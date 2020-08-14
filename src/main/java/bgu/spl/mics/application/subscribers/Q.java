package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.TerminateBrodcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;

import java.util.List;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {
	private Inventory inventory;
	private int currentTick;

	public Q() {
		super("Q");
		inventory=Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		MessageBrokerImpl.getInstance().register(this);
		subscribeBroadcast(TerminateBrodcast.class , message ->{
			this.terminate();
		});
		subscribeBroadcast(TickBroadcast.class,massage-> {
			if(currentTick<massage.getCurrentTime()) {
				currentTick = massage.getCurrentTime();
			}
		});
		subscribeEvent(GadgetAvailableEvent.class,message->{
			message.setTimeHandled(currentTick);
			String gadget=message.getGadget();
			boolean result = inventory.getItem(gadget);
			complete(message,result);

		});
	}


}
