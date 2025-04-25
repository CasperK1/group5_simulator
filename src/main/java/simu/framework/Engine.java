package simu.framework;

import controller.IControllerMtoV;
import simu.model.ServicePoint;

public abstract class Engine extends Thread implements IEngine {
	private double simulationTime = 0;	// time when the simulation will be stopped
	private long delay = 0;
	private Clock clock;
	
	protected EventList eventList;
	protected ServicePoint[] servicePoints;
	protected IControllerMtoV controller;

	public Engine(IControllerMtoV controller) {
		this.controller = controller;
		clock = Clock.getInstance();
		eventList = new EventList();
	}

	@Override
	public void setSimulationTime(double time) {
		simulationTime = time;
	}
	
	@Override
	public void setDelay(long time) {
		this.delay = time;
	}
	
	@Override
	public long getDelay() {
		return delay;
	}
	
	@Override
	public void run() {
		initialization(); // creating, e.g., the first event

		while (simulate()){
			delay(); // NEW
			clock.setTime(currentTime());
			runBEvents();
			tryCEvents();
		}

		results();
	}
	
	private void runBEvents() {
		while (eventList.getNextTime() == clock.getTime()){
			runEvent(eventList.remove());
		}
	}

	private void tryCEvents() {
		for (ServicePoint p: servicePoints){
			if (!p.isReserved() && p.isOnQueue()){
				p.beginService();
			}
		}
	}

	private double currentTime(){
		return eventList.getNextTime();
	}
	
	private boolean simulate() {
		Trace.out(Trace.Level.INFO, "Time is: " + clock.getTime());
		return clock.getTime() < simulationTime;
	}

	private void delay() { // NEW
		Trace.out(Trace.Level.INFO, "Delay " + delay);
		try {
			sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected abstract void initialization();
	protected abstract void runEvent(Event t);
	protected abstract void results();
}