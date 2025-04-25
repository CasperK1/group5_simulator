package controller;

import simu.model.Customer;
import simu.model.ServicePointType;

public interface IControllerMtoV {
	public void showEndTime(double time);
	public void customerCreated(Customer customer);
	public void customerMoved(int customerId, ServicePointType from, ServicePointType to);
	public void customerCompleted(int customerId, ServicePointType type);
}