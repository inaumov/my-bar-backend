package mybar.common.providers.availability;

public interface IAvailabilityCalculator<T extends IAvailabilitySettable> {

    void doUpdate(T item);
}