package gui;

import businesslogic.SelectionPolicy;
import businesslogic.SimulationManager;

public class MainToGo {
    public static void main(String[] args) {
        SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_QUEUE;
        SimulationManager manager = new SimulationManager(
                16, 5, 3, 4, 2,
                2, 7, selectionPolicy
        );
        SimulationFrame frame = new SimulationFrame(manager);
        manager.setFrame(frame);
        manager.clearLogFile();
        Thread simulationThread = new Thread(manager);
        simulationThread.start();
    }
}
