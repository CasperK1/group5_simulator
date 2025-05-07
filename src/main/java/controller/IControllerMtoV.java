package controller;

import simu.model.Customer;
import simu.model.ServicePointType;

public interface IControllerMtoV {
    void showEndTime(double time);

    void customerCreated(Customer customer);

    void customerMoved(int customerId, ServicePointType from, ServicePointType to);

    void customerCompleted(int customerId, ServicePointType type);

    void updateTimeLeft(int secondsLeft);
}