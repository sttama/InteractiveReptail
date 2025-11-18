package InteractiveReptail;

import java.awt.*;
import java.awt.geom.Path2D;

import static java.lang.Math.*;

public class Edge {
    Vertebra parent;
    double size;
    double x, y;

    Edge(Vertebra middleParent, double middleSize){
        size = middleSize;
        parent = middleParent;
        parent.edge = this;
    }

    void follow() {
        x = parent.x;
        y = parent.y;
    }

    void draw(Graphics2D middleG){

        middleG.setStroke(new BasicStroke(2.5f));// line thickness

        // specially calculated constants
        double r1 = size * sqrt(5) / 2;
        double r2 = size;
        double r3 = size * 0.5;
        double rotation1 = atan(0.5) + PI/2;
        double rotation2 = atan(0.294117) + PI/2;
        double rotation3 = PI/2;

        Path2D.Double edge = new Path2D.Double();

        //drawing an edge
        edge.moveTo(
                x + r1 * cos(parent.angle - rotation1), y + r1 * sin(parent.angle - rotation1)
        );
        edge.curveTo(
                x + r2 * cos(parent.angle - rotation2), y + r2 * sin(parent.angle - rotation2),
                x + r3 * cos(parent.angle - rotation3), y + r3 * sin(parent.angle - rotation3),
                x, y
        );
        edge.curveTo(
                x + r3 * cos(parent.angle + rotation3), y + r3 * sin(parent.angle + rotation3),
                x + r2 * cos(parent.angle + rotation2), y + r2 * sin(parent.angle + rotation2),
                x + r1 * cos(parent.angle + rotation1), y + r1 * sin(parent.angle + rotation1)
        );

        middleG.draw(edge);
    }
}