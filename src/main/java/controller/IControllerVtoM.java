package controller;

import simu.data.SimulationConfig;

/* interface for the UI */
public interface IControllerVtoM {
		public void startSimulation();
		public void increaseSpeed();
		public void decreaseSpeed();

	SimulationConfig getConfig();
}
