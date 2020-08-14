package bgu.spl.mics;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {

	private static MessageBrokerImpl messagebroker = new MessageBrokerImpl();
	private Semaphore semaphore;
	private ConcurrentHashMap<Subscriber, Queue<Message>> Sub_MessageQueue;
	private ConcurrentHashMap<Class<? extends Event>,Queue<Subscriber>> Event_Topic_Sub_Q;
	private final Object Event_Queue_locker=new Object();
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentHashMap<Integer,Subscriber>> Broad_Topic_Sub_Q;
	private  final Object Broad_Queue_locker=new Object();
	private ConcurrentHashMap<Event, Future> FutureEvents;

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBroker getInstance() {
		return messagebroker;
	}


	private MessageBrokerImpl() {
		Sub_MessageQueue = new ConcurrentHashMap<>();
		Event_Topic_Sub_Q = new ConcurrentHashMap<>();
		Broad_Topic_Sub_Q = new ConcurrentHashMap<>();
		FutureEvents = new ConcurrentHashMap<>();
		semaphore = new Semaphore(1,true);

	}

	public ConcurrentHashMap<Event, Future> getFutureEvents() {
		return FutureEvents;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
		if (Event_Topic_Sub_Q.get(type) != null) {
			Event_Topic_Sub_Q.get(type).add(m);
			return;
		}
		synchronized (Event_Queue_locker) {
			if(Event_Topic_Sub_Q.get(type)==null) {
				Queue<Subscriber> NewQueue = new ConcurrentLinkedQueue<>();
				Event_Topic_Sub_Q.put(type, NewQueue);
				Event_Topic_Sub_Q.get(type).add(m);
				return;
			}
		}
		Event_Topic_Sub_Q.get(type).add(m);
	}


	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		if (Broad_Topic_Sub_Q.get(type) != null) {
			Broad_Topic_Sub_Q.get(type).put(System.identityHashCode(m),m);
			return;
		}
		synchronized (Broad_Queue_locker) {
			if(Broad_Topic_Sub_Q.get(type)==null) {
				ConcurrentHashMap<Integer,Subscriber> hash = new ConcurrentHashMap<>();
				Broad_Topic_Sub_Q.put(type, hash);
				Broad_Topic_Sub_Q.get(type).put(System.identityHashCode(m),m);
				return;
			}
		}
		Broad_Topic_Sub_Q.get(type).put(System.identityHashCode(m),m);
	}


	@Override
	public <T> void complete(Event<T> e, T result) {

		FutureEvents.get(e).resolve(result);

	}


	@Override
	public void sendBroadcast(Broadcast b) {
		ConcurrentHashMap<Integer,Subscriber> hash= Broad_Topic_Sub_Q.get(b.getClass());
		if(hash!=null) {
			for (Subscriber s : hash.values()) {
				if (Sub_MessageQueue.get(s) != null) {
					synchronized (Sub_MessageQueue.get(s)) {
						Sub_MessageQueue.get(s).add(b);
						Sub_MessageQueue.get(s).notifyAll();
					}
				}
			}
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		//TODO fix it
		Future future = null;
		try {
			if (!Thread.currentThread().isInterrupted()) {
				semaphore.acquire();
				if (Event_Topic_Sub_Q.get(e.getClass()).isEmpty()) {
					semaphore.release();
					return null;
				}
				Subscriber s = Event_Topic_Sub_Q.get(e.getClass()).poll();
				Event_Topic_Sub_Q.get(e.getClass()).add(s);
				synchronized (Sub_MessageQueue.get(s)) {//necessary for the notify
					Sub_MessageQueue.get(s).add(e);
					Sub_MessageQueue.get(s).notifyAll();
					future = new Future<>();
					FutureEvents.put(e, future);
				}
			}
		}
		catch(InterruptedException E){
				semaphore.release();
			    Thread.currentThread().interrupt();
				return future;
			}
			semaphore.release();
			return future;
		}



	@Override
	public void register(Subscriber m) {
		Queue<Message> Event_list = new ConcurrentLinkedQueue<>();
		Sub_MessageQueue.put(m, Event_list);
	}

	@Override
	public  void unregister(Subscriber m) {
		try {
			if (!Thread.currentThread().isInterrupted()) {
				semaphore.acquire();
				for (Queue<Subscriber> s : Event_Topic_Sub_Q.values()) {
					if (s.contains(m)) {
						s.remove(m);
					}
				}
				for (ConcurrentHashMap<Integer, Subscriber> s : Broad_Topic_Sub_Q.values()) {
					if (s.contains(System.identityHashCode(m))) {
						s.remove(System.identityHashCode(m));
					}
				}
				synchronized (Sub_MessageQueue.get(m)) {
					Queue<Message> messages = Sub_MessageQueue.get(m);
					while (!messages.isEmpty()) {
						Message message = messages.poll();
						if (message instanceof Event) {
							complete((Event) message, null);
						}
					}
				}
				semaphore.release();
			}
		}
		catch (InterruptedException e){
			Thread.currentThread().interrupt();
			semaphore.release();
		}
	}


	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		if (Sub_MessageQueue.get(m) == null) {
			throw new IllegalStateException();
		}
		synchronized (Sub_MessageQueue.get(m)) {
			while (Sub_MessageQueue.get(m).isEmpty()) {
				try {
					Sub_MessageQueue.get(m).wait();
				} catch (InterruptedException E) {

				}
			}
		}
		Message message=Sub_MessageQueue.get(m).poll();
		return message;
	}


}


