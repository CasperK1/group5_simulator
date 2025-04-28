package simu.framework;

public interface IEngine {
	public void setSimulationTime(double time);
	public void setDelay(long time);
	public long getDelay();
	public void setPaused(boolean paused);
	public boolean isPaused();
}