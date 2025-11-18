package InteractiveReptail;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import javax.swing.*;
import static java.lang.Math.*;

public class SimulationReptail extends JPanel implements Runnable{
    private double mouseX = 0, mouseY = 0;
    int WIDTH = 1920, HEIGHT = 1080;
    Head head;
    Vertebra vertebra;
    Edge edge;
    double quantityVertebrae = 250; // the size of the reptile

    public SimulationReptail(){
        setBackground(Color.BLACK); // background color

        // Creating an exit button
        JButton exitButton = new JButton(){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // for smoothing pixels

                // Creating a button outline using a Bezier curve
                Path2D path = new Path2D.Double();

                path.moveTo(0, 0); // the starting point

                path.curveTo(
                        getWidth() * cos(-PI / 3), getHeight() + getHeight() * sin(-PI / 3),
                        getWidth() * cos(-PI / 6), getHeight() + getHeight() * sin(-PI / 6),
                        getWidth(), getHeight()
                );
                path.lineTo(getWidth(), 0);
                path.lineTo(0, 0);

                path.closePath(); // closing the contour

                // painting over the background of the button
                g2.setColor(getBackground()); // assigning the status of the background
                g2.fill(path);

                // we draw a cross
                g2.setColor(getForeground()); // assigning the status of the foreground
                g2.drawLine((int) (getWidth() * 0.9), (int) (getHeight() * 0.1),
                        (int) (getWidth() * cos(PI / 4)), (int) (getHeight() - getHeight() * cos(PI / 4)));
                g2.drawLine((int) (getWidth() * cos(PI / 4)), (int) (getHeight() * 0.1),
                        (int) (getWidth() * 0.9), (int) (getHeight() - getHeight() * cos(PI / 4)));
            }

            @Override
            public boolean contains(int x, int y) {
                // Creating the same outline as in the paintComponent
                Path2D path = new Path2D.Double();

                path.moveTo(0, 0);

                path.curveTo(
                        getWidth() * Math.cos(-Math.PI / 3), getHeight() + getHeight() * Math.sin(-Math.PI / 3),
                        getWidth() * Math.cos(-Math.PI / 6), getHeight() + getHeight() * Math.sin(-Math.PI / 6),
                        getWidth(), getHeight()
                );
                path.lineTo(getWidth(), 0);
                path.lineTo(0, 0);

                path.closePath();

                // Checking if the point fits into the contour
                return path.contains(x, y);
            }
        };

        // disabling unnecessary actions
        exitButton.setBorderPainted(false);
        exitButton.setRolloverEnabled(false);
        exitButton.setContentAreaFilled(false);

        setLayout(null); // it is needed to position the button itself

        exitButton.setBounds(WIDTH - 40, 0, 40,40); // upper right corner
        exitButton.setBackground(Color.WHITE);
        exitButton.setForeground(Color.BLACK);

        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);

        //-------------------

        setupInputHandlers();

        // creating a reptile

        head = new Head(  WIDTH / 2, 1.4 * HEIGHT, 2.5, 12);

        // initialization of the initial vertebra
        vertebra = new Vertebra(null, 0);
        vertebra.x = head.x;
        vertebra.y = head.y;
        head.children.add(vertebra);

        // parameters for edges
        double stepEdge = 3;
        double tempSizeEdge = 0;
        double maxSizeEdge = 40;
        double tempArgForSinSizeEdge = 0;
        double stepArgForSinSizeEdge = PI * 8 / (9 * (quantityVertebrae / stepEdge));

        // initializing the entire reptile
        for (int i = 1; i <= quantityVertebrae; i++) {
            vertebra = new Vertebra(vertebra, 10);
            head.children.add(vertebra);

            // initializing the edges
            if (i % stepEdge == 0 && i < quantityVertebrae) {
                // different size for the edges
                tempSizeEdge = maxSizeEdge * sin(tempArgForSinSizeEdge + PI / 9);
                tempArgForSinSizeEdge += stepArgForSinSizeEdge;

                edge = new Edge(vertebra, tempSizeEdge);
            }

            // initializing the limbs
            if (i % (stepEdge * 7) == 0 && i < quantityVertebrae * 0.9) {
                Limb limb = new Limb(55, Math.PI / 20, vertebra, head, edge);
            }
        }

        new Thread(this).start();
    }

    void setupInputHandlers(){
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // for smoothing pixels
        g2D.setColor(Color.WHITE); // the color of the reptile

        // assigning the drawing of the head
        if (head != null){
            head.draw(g2D);
        }
    }

    @Override
    public void run(){
        // starting the simulation
        while (true){
            if (head != null) {
                // launching the head
                head.follow(mouseX, mouseY);

                // music (comment to disable it)
                SoundPlayer.playSound("PR1SVX_-_CRYSTALS_Slowed.wav");
            }

            // redrawing the scene
            repaint();

            // frame rate
            try {
                Thread.sleep(30); // 33 fps (1000 / 30)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}