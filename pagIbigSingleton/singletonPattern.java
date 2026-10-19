package pagIbigSingleton;

import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class singletonPattern {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        QueueSystem queue = QueueSystem.getInstance();

        queue.addListener((number, message) ->
                System.out.println("\n[ONLINE MONITOR] " + message
                        + " | Current Queue Number: " + number)
        );

        HelpDeskStation desk1 = new HelpDeskStation("Help Desk 1", queue);
        HelpDeskStation desk2 = new HelpDeskStation("Help Desk 2", queue);
        HelpDeskStation desk3 = new HelpDeskStation("Help Desk 3", queue);

        HelpDeskStation[] desks = {desk1, desk2, desk3};

        while (true) {
            System.out.println("\n=== PAG-IBIG CENTRALIZED QUEUING SYSTEM ===");
            System.out.println("1. Help Desk 1");
            System.out.println("2. Help Desk 2");
            System.out.println("3. Help Desk 3");
            System.out.println("4. Online Monitoring System");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = readInt(sc);

            if (choice == 0) {
                System.out.println("Queuing system closed.");
                break;
            }

            if (choice >= 1 && choice <= 3) {
                HelpDeskStation selectedDesk = desks[choice - 1];

                while (true) {
                    System.out.println("\n--- " + selectedDesk.getName() + " ---");
                    System.out.println("1. Get Next Queue Number");
                    System.out.println("2. Reset Queue Number");
                    System.out.println("3. View Current Queue Number");
                    System.out.println("0. Back to Main Menu");
                    System.out.print("Choose an option: ");

                    int action = readInt(sc);

                    if (action == 0) {
                        break;
                    }

                    switch (action) {
                        case 1:
                            selectedDesk.getNextNumber();
                            break;

                        case 2:
                            System.out.print("Enter new queue number: ");
                            int newNumber = readInt(sc);
                            selectedDesk.resetQueueNumber(newNumber);
                            break;

                        case 3:
                            System.out.println("Current Queue Number: "
                                    + queue.getCurrentNumber());
                            break;

                        default:
                            System.out.println("Invalid option.");
                    }
                }

            } else if (choice == 4) {
                System.out.println("\n=== ONLINE MONITORING SYSTEM ===");
                System.out.println("Current Queue Number: "
                        + queue.getCurrentNumber());
                System.out.println("The displayed number updates whenever "
                        + "any help desk issues or resets a number.");

            } else {
                System.out.println("Invalid option.");
            }
        }

        sc.close();
    }

    private static int readInt(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            sc.next();
        }
        return sc.nextInt();
    }
}

// SINGLETON CLASS
class QueueSystem {

    private static volatile QueueSystem instance;

    private int currentNumber = 0;

    private final CopyOnWriteArrayList<QueueListener> listeners =
            new CopyOnWriteArrayList<>();

    private QueueSystem() {
    }

    public static QueueSystem getInstance() {
        if (instance == null) {
            synchronized (QueueSystem.class) {
                if (instance == null) {
                    instance = new QueueSystem();
                }
            }
        }
        return instance;
    }

    public synchronized int getNextNumber() {
        currentNumber++;
        notifyListeners("New queue number issued: " + currentNumber);
        return currentNumber;
    }

    public synchronized void resetQueueNumber(int newNumber) {
        if (newNumber < 0) {
            System.out.println("Queue number cannot be negative.");
            return;
        }

        currentNumber = newNumber;
        notifyListeners("Queue number reset to: " + currentNumber);
    }

    public synchronized int getCurrentNumber() {
        return currentNumber;
    }

    public void addListener(QueueListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(String message) {
        for (QueueListener listener : listeners) {
            listener.onQueueUpdated(currentNumber, message);
        }
    }
}

// HELP DESK STATION CLASS
class HelpDeskStation {

    private final String name;
    private final QueueSystem queueSystem;

    public HelpDeskStation(String name, QueueSystem queueSystem) {
        this.name = name;
        this.queueSystem = queueSystem;
    }

    public String getName() {
        return name;
    }

    public void getNextNumber() {
        int number = queueSystem.getNextNumber();
        System.out.println(name + " issued queue number: " + number);
    }

    public void resetQueueNumber(int newNumber) {
        queueSystem.resetQueueNumber(newNumber);
    }
}

// ONLINE MONITORING LISTENER
interface QueueListener {
    void onQueueUpdated(int currentNumber, String message);
}