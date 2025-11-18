package InteractiveReptail;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class Head {
    double x, y;
    double speed;
    double accel;
    double dist;
    double targetAngle, tempAngle;
    List<Vertebra> children = new ArrayList<>();
    double size;

    Head(double middleX, double middleY, double middleAccel, double middleSize){
        x = middleX;
        y = middleY;
        size = middleSize;
        accel = middleAccel;
    }

    void follow(double targetX, double targetY){
        dist = sqrt(Math.pow(targetX - x, 2) + pow(targetY - y, 2));
        if (dist > 10) targetAngle = atan2(targetY - y, targetX - x);

        // normalize (0 ; 2PI)
        if (tempAngle > PI*2) tempAngle -= PI * 2;
        if (tempAngle < 0) tempAngle += PI * 2;
        if (targetAngle < 0) targetAngle += PI * 2;

        // Counting the speed
        speed += accel * (dist > 150 ? 1 : -0.2);
        speed *= 0.9;
        speed = Math.max(0, speed - 0.5);
        if (dist < 10) speed = 0;

        double step = PI / 12; // head rotation speed
        double intervalNumber = targetAngle - PI;

        // we turn in the direction that is much closer to the goal
        if (intervalNumber < 0){
            intervalNumber += PI*2;
            if (targetAngle + step / 2 < tempAngle && tempAngle < intervalNumber) {
                tempAngle -= step;
            } else if (targetAngle - step /2 < tempAngle && tempAngle < targetAngle + step / 2
                    || (targetAngle - step / 2 < 0 && tempAngle > targetAngle - step / 2 + PI * 2)) {
                tempAngle = targetAngle;
            } else {
                tempAngle += step;
            }
        } else {
            if (intervalNumber < tempAngle && tempAngle < targetAngle - step / 2) {
                tempAngle += step;
            } else if (targetAngle - step / 2 < tempAngle && tempAngle < targetAngle + step / 2
                    || (targetAngle + step / 2 > PI * 2 && tempAngle < targetAngle + step / 2 - PI * 2)) {
                tempAngle = targetAngle;
            } else {
                tempAngle -= step;
            }
        }

        x += speed * cos(tempAngle);
        y += speed * sin(tempAngle);

        // launching the remaining elements
        for (Vertebra child : children) {
            if (child.parent == null) {
                child.x = x;
                child.y = y;
            }
            child.follow();
        }
    }

    void draw(Graphics2D middleG){

        middleG.setStroke(new BasicStroke(2.5f)); // line thickness

        // specially calculated constants
        double r1 = size * 0.85;
        double r2 = size * sqrt(370) / 20;
        double r3 = size * sqrt(1394) / 20;
        double r4 = size * 1.85;
        double r5 = size * sqrt(2581) / 50;
        double r6 = size * sqrt(9689) / 100;
        double r7 = size * 0.9;
        double rotation1 = PI/2;
        double rotation2 = 1.0838970949836275;
        double rotation3 = 0.13432144195296852;
        double rotation4 = 0;
        double rotation5 = PI / 2 + 0.6316906343389693;
        double rotation6 = PI / 2 + 1.2072694498204581;
        double rotation7 = PI;

        Path2D.Double head = new Path2D.Double();

        // drawing the head
        head.moveTo( // initially, the angle is 0 degrees (turn to the right)
                x + r1 * cos(tempAngle - rotation1), y + r1 * sin(tempAngle - rotation1)
        );
        head.curveTo( // the first quarter of the head (upper left)
                x + r2 * cos(tempAngle - rotation2), y + r2 * sin(tempAngle - rotation2),
                x + r3 * cos(tempAngle - rotation3), y + r3 * sin(tempAngle - rotation3),
                x + r4 * cos(tempAngle - rotation4), y + r4 * sin(tempAngle - rotation4)
        );
        head.curveTo( // the second quarter of the head (upper right)
                x + r3 * cos(tempAngle + rotation3), y + r3 * sin(tempAngle + rotation3),
                x + r2 * cos(tempAngle + rotation2), y + r2 * sin(tempAngle + rotation2),
                x + r1 * cos(tempAngle + rotation1), y + r1 * sin(tempAngle + rotation1)
        );
        head.curveTo( // the third quarter of the head (bottom right)
                x + r5 * cos(tempAngle + rotation5), y + r5 * sin(tempAngle + rotation5),
                x + r6 * cos(tempAngle + rotation6), y + r6 * sin(tempAngle + rotation6),
                x + r7 * cos(tempAngle + rotation7), y + r7 * sin(tempAngle + rotation7)
        );
        head.curveTo( // the fourth quarter of the head (bottom left)
                x + r6 * cos(tempAngle - rotation6), y + r6 * sin(tempAngle - rotation6),
                x + r5 * cos(tempAngle - rotation5), y + r5 * sin(tempAngle - rotation5),
                x + r1 * cos(tempAngle - rotation1), y + r1 * sin(tempAngle - rotation1)
        );
        middleG.draw(head);

        // drawing the remaining elements
        for (Vertebra child : children) {
            child.draw(middleG);
        }
    }
}