package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Publisher;
import bgu.spl.mics.SimplePublisher;
import bgu.spl.mics.application.messages.TerminateBrodcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.time.Clock;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.lang.Object;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link TickBroadcast Broadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {

	private int duration;
	private int count=0;

	public TimeService(int duration) {
		super("TimeService");
		this.duration=duration;
	}

	@Override
	protected void initialize() {

	}

	@Override
	public void run() {
		while (count<duration) {
			try{
				Thread.sleep(100);
				count++;
			}
			catch (InterruptedException E){}
			TickBroadcast tickBroadcast;
			if(count==duration) {
				tickBroadcast= new TickBroadcast(count);
				getSimplePublisher().sendBroadcast(tickBroadcast);
				TerminateBrodcast terminateBrodcast = new TerminateBrodcast();
				getSimplePublisher().sendBroadcast(terminateBrodcast);
			}
			else {
				tickBroadcast = new TickBroadcast(count);
				getSimplePublisher().sendBroadcast(tickBroadcast);
			}
		}
	}
}
