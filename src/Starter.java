import InteractiveReptail.SimulationReptail;

import javax.swing.*;

public class Starter {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        SimulationReptail scene = new SimulationReptail();
        frame.setUndecorated(true); // remove the title bar
        frame.add(scene);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}
