package bgu.spl.mics.application.passiveObjects;
import java.util.*;
import java.util.concurrent.Semaphore;


/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	private Map<String, Agent> agents=new HashMap();
	private static Squad instance = new Squad();
	private Semaphore semaphore=new Semaphore(1,true);
	private boolean interruptMode=false;


	private Squad(){}


	/**
	 * Retrieves the single instance of this class.
	 */

	public static Squad getInstance() {
		return instance;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] agents) {
		for(Agent agent: agents) {
			this.agents.put(agent.getSerialNumber(), agent);
		}
	}

	/**
	 * Releases agents.
	 */

	public void releaseAgents(List<String> serials){
		for(String str : serials){
			agents.get(str).release();
		}
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   time ticks to sleep
	 */

	public void sendAgents(List<String> serials, int time){
		try {
			Thread.sleep(time*100);
		}
		catch(InterruptedException E){
			Thread.currentThread().interrupt();
			interruptMode=true;
		}
		releaseAgents(serials);
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials){
		if(!interruptMode) {
			try {
				boolean allAvailable = false;
				for (String s : serials) {
					Agent agent = agents.get(s);
					if (agent == null) {
						return false;
					}
				}
				while (!allAvailable && !Thread.currentThread().isInterrupted()) {
					allAvailable = true;
					semaphore.acquire();
					for (String s : serials) {
						Agent agent = agents.get(s);
						if (allAvailable && (!agent.isAvailable())) {
							allAvailable = false;
						}
					}
					if (allAvailable) {
						for (String s : serials) {
							Agent agent = agents.get(s);
							agent.acquire();
						}
					}
					semaphore.release();
				}
			} catch (InterruptedException e) {
				interruptMode=true;
				releaseAgents(serials);
				Thread.currentThread().interrupt();
				return false;
			}

			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * gets the agents names
	 * @param serials the serial numbers of the agents
	 * @return a list of the names of the agents with the specified serials.
	 */
	public List<String> getAgentsNames(List<String> serials){
		List<String> AgentsName = new LinkedList<>();
		for(String serialNumber: serials){
			AgentsName.add(agents.get(serialNumber).getName());
		}
		return AgentsName;
	}

}
