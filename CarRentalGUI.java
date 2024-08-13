package CarRental;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CarRentalGUI extends JFrame {
    private CarRentalSystem rentalSystem;
    private JComboBox<String> carComboBox;
    private JTextField customerNameField;
    private JTextField rentalDaysField;
    private JTextArea outputArea;
    private AnimationPanel animationPanel;

    public CarRentalGUI(CarRentalSystem rentalSystem) {
        this.rentalSystem = rentalSystem;

        // GUI setup
        setTitle("Car Rental System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));
        inputPanel.setBackground(new Color(70, 130, 180));  // Steel Blue

        inputPanel.add(new JLabel("Customer Name:"));
        customerNameField = new JTextField();
        inputPanel.add(customerNameField);

        inputPanel.add(new JLabel("Rental Days:"));
        rentalDaysField = new JTextField();
        inputPanel.add(rentalDaysField);

        inputPanel.add(new JLabel("Select Car:"));
        carComboBox = new JComboBox<>();
        updateCarComboBox();
        inputPanel.add(carComboBox);

        JButton rentButton = new JButton("Rent Car");
        rentButton.setBackground(new Color(255, 69, 0));  // Red Orange
        rentButton.setForeground(Color.WHITE);
        rentButton.addActionListener(new RentButtonListener());
        inputPanel.add(rentButton);

        JButton returnButton = new JButton("Return Car");
        returnButton.setBackground(new Color(255, 69, 0));  // Red Orange
        returnButton.setForeground(Color.WHITE);
        returnButton.addActionListener(new ReturnButtonListener());
        inputPanel.add(returnButton);

        add(inputPanel, BorderLayout.NORTH);

        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(240, 248, 255));  // Alice Blue
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Animation panel
        animationPanel = new AnimationPanel();
        add(animationPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void updateCarComboBox() {
        carComboBox.removeAllItems();
        List<Car> cars = rentalSystem.getCars();
        for (Car car : cars) {
            carComboBox.addItem(car.getCarId() + " - " + car.getBrand() + " " + car.getModel());
        }
    }

    private class RentButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String customerName = customerNameField.getText();
            String rentalDaysText = rentalDaysField.getText();
            int rentalDays;

            try {
                rentalDays = Integer.parseInt(rentalDaysText);
            } catch (NumberFormatException ex) {
                outputArea.setText("Please enter a valid number of rental days.");
                return;
            }

            String selectedCar = (String) carComboBox.getSelectedItem();
            if (selectedCar == null) {
                outputArea.setText("Please select a car.");
                return;
            }

            String carId = selectedCar.split(" - ")[0];
            Car carToRent = null;
            for (Car car : rentalSystem.getCars()) {
                if (car.getCarId().equals(carId) && car.isAvailable()) {
                    carToRent = car;
                    break;
                }
            }

            if (carToRent != null) {
                Customer customer = new Customer("CUS" + (rentalSystem.getCustomers().size() + 1), customerName);
                rentalSystem.addCustomer(customer);
                rentalSystem.rentCar(carToRent, customer, rentalDays);
                outputArea.setText("Car rented successfully!\nCustomer ID: " + customer.getCustomerId() + "\nTotal Price: ₹" + carToRent.calculatePrice(rentalDays));
                animationPanel.startRentAnimation();
            } else {
                outputArea.setText("Selected car is not available for rent.");
            }

            updateCarComboBox();
        }
    }

    private class ReturnButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedCar = (String) carComboBox.getSelectedItem();
            if (selectedCar == null) {
                outputArea.setText("Please select a car to return.");
                return;
            }

            String carId = selectedCar.split(" - ")[0];
            Car carToReturn = null;
            for (Car car : rentalSystem.getCars()) {
                if (car.getCarId().equals(carId) && !car.isAvailable()) {
                    carToReturn = car;
                    break;
                }
            }

            if (carToReturn != null) {
                rentalSystem.returnCar(carToReturn);
                outputArea.setText("Car returned successfully!");
                animationPanel.startReturnAnimation();
            } else {
                outputArea.setText("Selected car is not currently rented.");
            }

            updateCarComboBox();
        }
    }

    private class AnimationPanel extends JPanel {
        private Image carImage;
        private int carX;
        private Timer timer;
        private boolean renting;

        public AnimationPanel() {
            carImage = new ImageIcon("path/to/your/car/image.png").getImage();  // Replace with the path to your car image
            carX = 0;
            timer = new Timer(30, e -> moveCar());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(240, 248, 255));  // Alice Blue
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawImage(carImage, carX, 10, this);
        }

        public void startRentAnimation() {
            renting = true;
            carX = 0;
            timer.start();
        }

        public void startReturnAnimation() {
            renting = false;
            carX = getWidth();
            timer.start();
        }

        private void moveCar() {
            if (renting) {
                if (carX < getWidth() - carImage.getWidth(null)) {
                    carX += 5;
                } else {
                    timer.stop();
                }
            } else {
                if (carX > 0) {
                    carX -= 5;
                } else {
                    timer.stop();
                }
            }
            repaint();
        }
    }

    public static void main(String[] args) {
        CarRentalSystem rentalSystem = new CarRentalSystem();

        // Sample data with Indian car names
        rentalSystem.addCar(new Car("C001", "Tata", "Nexon", 6000.0));
        rentalSystem.addCar(new Car("C002", "Mahindra", "XUV500", 7000.0));
        rentalSystem.addCar(new Car("C003", "Maruti", "Swift", 5000.0));

        SwingUtilities.invokeLater(() -> new CarRentalGUI(rentalSystem));
    }
}
