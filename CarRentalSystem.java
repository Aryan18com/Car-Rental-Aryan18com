package CarRental;

import java.util.ArrayList;
import java.util.List;

public class CarRentalSystem {
    private List<Car> cars;
    private List<Customer> customers;
    private List<Rental> rentals;

    public CarRentalSystem() {
        cars = new ArrayList<>();
        customers = new ArrayList<>();
        rentals = new ArrayList<>();
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void rentCar(Car car, Customer customer, int days) {
        if (car.isAvailable()) {
            car.rent();
            rentals.add(new Rental(car, customer, days));
        } else {
            System.out.println("Car is not available for rent.");
        }
    }

    public void returnCar(Car car) {
        car.returnCar();
        Rental rentalToRemove = null;
        for (Rental rental : rentals) {
            if (rental.getCar().equals(car)) {
                rentalToRemove = rental;
                break;
            }
        }
        if (rentalToRemove != null) {
            rentals.remove(rentalToRemove);
        } else {
            System.out.println("Car was not rented.");
        }
    }

    public List<Car> getCars() {
        return new ArrayList<>(cars);
    }

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    public List<Rental> getRentals() {
        return new ArrayList<>(rentals);
    }
}
