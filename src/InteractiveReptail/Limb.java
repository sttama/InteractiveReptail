package InteractiveReptail;

import java.awt.*;
import java.util.Random;

import static java.lang.Math.*;

public class Limb {
    double size, palmSize;
    double angularSpeed;
    double angularRelativeSpeed;
    double startPositionXLeft, startPositionYLeft;
    double startPositionXRight, startPositionYRight;
    double rotationStart;
    double distStart;
    Vertebra parent;
    Vertebra segmentFirstLeft, segmentLastLeft;
    Vertebra segmentFirstRight, segmentLastRight;
    Head head;
    Edge edge;
    boolean currentLocateLeft, currentLocateRight;
    double angleLeft, angleRight;
    double distLeft, distRight;
    double tempAngleLeft, tempAngleRight;
    double xL1, xL2, xL3, xL4;
    double yL1, yL2, yL3, yL4;
    double xR1, xR2, xR3, xR4;
    double yR1, yR2, yR3, yR4;
    double palmAngleLeft, palmAngleRight;

    Limb(double middleSize, double middleConstSpeed, Vertebra middleParent, Head middleHead, Edge middleEdge) {
        size = middleSize;
        angularSpeed = middleConstSpeed;
        parent = middleParent;
        head = middleHead;
        edge = middleEdge;
        parent.limb = this;

        // create the left paw (counterclockwise)
        segmentFirstLeft = new Vertebra(parent, size);
        segmentLastLeft = new Vertebra(segmentFirstLeft, size);
        segmentFirstLeft.otherParent = segmentLastLeft;

        //create the right paw (clockwise)
        segmentFirstRight = new Vertebra(parent, size);
        segmentLastRight = new Vertebra(segmentFirstRight, size);
        segmentFirstRight.otherParent = segmentLastRight;

        // initial initialization
        palmSize = size * 0.25;
        distStart = edge.size * sqrt(5) / 2;
        rotationStart = atan(0.5) + PI/2;

        // We put the left paw in its place
        startPositionXLeft = parent.x + distStart * cos(edge.parent.angle - rotationStart);
        startPositionYLeft = parent.y + distStart * sin(edge.parent.angle - rotationStart);
        segmentFirstLeft.x = startPositionXLeft + size * 1 * cos(parent.angle - PI / 2);
        segmentFirstLeft.y = startPositionYLeft + size * 1 * sin(parent.angle - PI / 2);
        segmentLastLeft.x = startPositionXLeft + size * 2 * cos(parent.angle - PI / 2);
        segmentLastLeft.y = startPositionYLeft + size * 2 * sin(parent.angle - PI / 2);

        // We put the right paw in its place
        startPositionXRight = parent.x + distStart * cos(edge.parent.angle + rotationStart);
        startPositionYRight = parent.y + distStart * sin(edge.parent.angle + rotationStart);
        segmentFirstRight.x = startPositionXRight + size * 1 * cos(parent.angle + PI / 2);
        segmentFirstRight.y = startPositionYRight + size * 1 * sin(parent.angle + PI / 2);
        segmentLastRight.x = startPositionXRight + size * 2 * cos(parent.angle + PI / 2);
        segmentLastRight.y = startPositionYRight + size * 2 * sin(parent.angle + PI / 2);

        size *= 2; // for convenience
    }

    void follow() {
        angularRelativeSpeed = acos(1 - pow(head.speed, 2) / (2 * pow(size * 2, 2))) * 1.6; // derived from the cosine theorem

        // Left side

        // updating the necessary data for the left paw
        startPositionXLeft = parent.x + distStart * cos(edge.parent.angle - rotationStart);
        startPositionYLeft = parent.y + distStart * sin(edge.parent.angle - rotationStart);
        distLeft = sqrt(pow(startPositionXLeft - segmentLastLeft.x, 2) + pow(startPositionYLeft - segmentLastLeft.y, 2));
        angleLeft = atan2(segmentLastLeft.y - startPositionYLeft, segmentLastLeft.x - startPositionXLeft);
        tempAngleLeft = angleLeft;

        // we calculate the angle between the parent segment and ours, and we get in the interval [0; 2PI)
        if (angleLeft < 0) angleLeft += 2 * PI;
        angleLeft = angleLeft - (parent.angle < 0 ? parent.angle + 2 * PI : parent.angle);
        if (angleLeft < 0) angleLeft = PI * 2 + angleLeft;

        // shorten the left limb
        if (distLeft > size) {
            segmentLastLeft.x = startPositionXLeft + (distLeft * 0.96) * cos(tempAngleLeft);
            segmentLastLeft.y = startPositionYLeft + (distLeft * 0.96) * sin(tempAngleLeft);
        }

        // adding randomness to the movement of paws
        Random random = new Random();
        int locateFalseLeft = 0;
        if (angleLeft < PI * 3 / 2) {
            locateFalseLeft = random.nextInt(3);
        }

        // we turn on or off the movement of the paw, depending on where it is located
        if (PI / 4 <= angleLeft && angleLeft <= PI
                || (PI < angleLeft && angleLeft < PI * 5 / 4 && distLeft > size * 0.95))
            currentLocateLeft = true;
        else if ((PI * 23 / 12 - PI / 12 * locateFalseLeft) - angularSpeed < angleLeft
                && angleLeft < (PI * 23 / 12 - PI / 12 * locateFalseLeft) + angularSpeed)
            currentLocateLeft = false;

        // we turn depending on where it is closer
        if (currentLocateLeft) {
            if (PI * 3 / 4 < angleLeft && angleLeft < (PI * 23 / 12 - PI / 12 * locateFalseLeft) - angularSpeed) {
                // lengthen the limb
                if (distLeft < size * 0.95 && angleLeft <= PI * 3 / 2) {
                    segmentLastLeft.x = startPositionXLeft + (distLeft * 1.05)
                            * cos(tempAngleLeft + (angularSpeed + angularRelativeSpeed));
                    segmentLastLeft.y = startPositionYLeft + (distLeft * 1.05)
                            * sin(tempAngleLeft + (angularSpeed + angularRelativeSpeed));
                } else {
                    segmentLastLeft.x = startPositionXLeft + size
                            * cos(tempAngleLeft + (angularSpeed + angularRelativeSpeed));
                    segmentLastLeft.y = startPositionYLeft + size
                            * sin(tempAngleLeft + (angularSpeed + angularRelativeSpeed));
                }
            }
            if ((PI * 23 / 12 - PI / 12 * locateFalseLeft) + angularSpeed < angleLeft || angleLeft < PI * 3 / 4) {
                // lengthen the limb
                if (distLeft < size * 0.85) {
                    segmentLastLeft.x = startPositionXLeft + (distLeft * 1.1)
                            * cos(tempAngleLeft - (angularSpeed + angularRelativeSpeed));
                    segmentLastLeft.y = startPositionYLeft + (distLeft * 1.1)
                            * sin(tempAngleLeft - (angularSpeed + angularRelativeSpeed));
                } else {
                    segmentLastLeft.x = startPositionXLeft + size
                            * cos(tempAngleLeft - (angularSpeed + angularRelativeSpeed));
                    segmentLastLeft.y = startPositionYLeft + size
                            * sin(tempAngleLeft - (angularSpeed + angularRelativeSpeed));
                }
            }
        }

        // for palm

        palmAngleLeft = atan2(segmentLastLeft.y - segmentFirstLeft.y, segmentLastLeft.x - segmentFirstLeft.x);

        xL1 = segmentLastLeft.x + palmSize * cos(palmAngleLeft - PI / 6);
        yL1 = segmentLastLeft.y + palmSize * sin(palmAngleLeft - PI / 6);

        xL2 = segmentLastLeft.x + palmSize * cos(palmAngleLeft - PI / 18);
        yL2 = segmentLastLeft.y + palmSize * sin(palmAngleLeft - PI / 18);

        xL3 = segmentLastLeft.x + palmSize * cos(palmAngleLeft + PI / 18);
        yL3 = segmentLastLeft.y + palmSize * sin(palmAngleLeft + PI / 18);

        xL4 = segmentLastLeft.x + palmSize * cos(palmAngleLeft + PI / 6);
        yL4 = segmentLastLeft.y + palmSize * sin(palmAngleLeft + PI / 6);



        // Right side

        // updating the necessary data for the right paw
        startPositionXRight = parent.x + distStart * cos(edge.parent.angle + rotationStart);
        startPositionYRight = parent.y + distStart * sin(edge.parent.angle + rotationStart);
        distRight = sqrt(pow(startPositionXRight - segmentLastRight.x, 2) + pow(startPositionYRight - segmentLastRight.y, 2));
        angleRight = atan2(segmentLastRight.y - startPositionYRight, segmentLastRight.x - startPositionXRight);
        tempAngleRight = angleRight;

        // we calculate the angle between the parent segment and ours, and we get in the interval [0; 2PI)
        if (angleRight < 0) angleRight += 2 * PI;
        angleRight = angleRight - (parent.angle < 0 ? parent.angle + 2 * PI : parent.angle);
        if (angleRight < 0) angleRight = PI * 2 + angleRight;

        // shorten the right limb
        if (distRight > size) {
            segmentLastRight.x = startPositionXRight + (distRight * 0.96) * cos(tempAngleRight);
            segmentLastRight.y = startPositionYRight + (distRight * 0.96) * sin(tempAngleRight);
        }

        // adding randomness to the movement of paws
        int locateFalseRight = 0;
        if (angleRight > PI / 2) {
            locateFalseRight = random.nextInt(3);
        }

        // we turn on or off the movement of the paw, depending on where it is located
        if (PI <= angleRight && angleRight <= PI * 7 / 4
                || PI * 3 / 4 < angleRight && angleRight < PI && distRight > size * 0.9)
            currentLocateRight = true;
        else if ((PI / 12 + PI / 12 * locateFalseRight) - angularSpeed < angleRight
                && angleRight < (PI / 12 + PI / 12 * locateFalseRight) + angularSpeed)
            currentLocateRight = false;

        // we turn depending on where it is closer
        if (currentLocateRight) {
            if ((PI / 12 + PI / 12 * locateFalseRight) + angularSpeed < angleRight && angleRight < PI * 5 / 4) {
                // lengthen the limb
                if (distRight < size * 0.95 && angleRight >= PI / 2) {
                    segmentLastRight.x = startPositionXRight + (distRight * 1.05)
                            * cos(tempAngleRight - (angularSpeed + angularRelativeSpeed));
                    segmentLastRight.y = startPositionYRight + (distRight * 1.05)
                            * sin(tempAngleRight - (angularSpeed + angularRelativeSpeed));
                } else {
                    segmentLastRight.x = startPositionXRight + size
                            * cos(tempAngleRight - (angularSpeed + angularRelativeSpeed));
                    segmentLastRight.y = startPositionYRight + size
                            * sin(tempAngleRight - (angularSpeed + angularRelativeSpeed));
                }
            }
            if (PI * 5 / 4 < angleRight || angleRight < (PI / 12 + PI / 12 * locateFalseRight) - angularSpeed) {
                // lengthen the limb
                if (distRight < size * 0.85) {
                    segmentLastRight.x = startPositionXRight + (distRight * 1.1)
                            * cos(tempAngleRight + (angularSpeed + angularRelativeSpeed));
                    segmentLastRight.y = startPositionYRight + (distRight * 1.1)
                            * sin(tempAngleRight + (angularSpeed + angularRelativeSpeed));
                } else {
                    segmentLastRight.x = startPositionXRight + size
                            * cos(tempAngleRight + (angularSpeed + angularRelativeSpeed));
                    segmentLastRight.y = startPositionYRight + size
                            * sin(tempAngleRight + (angularSpeed + angularRelativeSpeed));
                }
            }
        }

        // for palm

        palmAngleRight = atan2(segmentLastRight.y - segmentFirstRight.y, segmentLastRight.x - segmentFirstRight.x);

        xR1 = segmentLastRight.x + palmSize * cos(palmAngleRight - PI / 6);
        yR1 = segmentLastRight.y + palmSize * sin(palmAngleRight - PI / 6);

        xR2 = segmentLastRight.x + palmSize * cos(palmAngleRight - PI / 18);
        yR2 = segmentLastRight.y + palmSize * sin(palmAngleRight - PI / 18);

        xR3 = segmentLastRight.x + palmSize * cos(palmAngleRight + PI / 18);
        yR3 = segmentLastRight.y + palmSize * sin(palmAngleRight + PI / 18);

        xR4 = segmentLastRight.x + palmSize * cos(palmAngleRight + PI / 6);
        yR4 = segmentLastRight.y + palmSize * sin(palmAngleRight + PI / 6);

        //------------------------

        // updating the parent for the first segments (since we have the distance between the chord and the origin for the paws)
        segmentFirstLeft.tempParentX = startPositionXLeft;
        segmentFirstLeft.tempParentY = startPositionYLeft;

        segmentFirstRight.tempParentX = startPositionXRight;
        segmentFirstRight.tempParentY = startPositionYRight;

        // starting the movement for the first segments of the paws
        segmentFirstLeft.follow();
        segmentFirstRight.follow();
    }

    void draw(Graphics2D middleG) {
        // draw the left paw
        segmentFirstLeft.draw(middleG);
        segmentLastLeft.draw(middleG);
        // draw the left palm
        middleG.drawLine((int) segmentLastLeft.x, (int) segmentLastLeft.y, (int) xL1, (int) yL1);
        middleG.drawLine((int) segmentLastLeft.x, (int) segmentLastLeft.y, (int) xL2, (int) yL2);
        middleG.drawLine((int) segmentLastLeft.x, (int) segmentLastLeft.y, (int) xL3, (int) yL3);
        middleG.drawLine((int) segmentLastLeft.x, (int) segmentLastLeft.y, (int) xL4, (int) yL4);

        // draw the right paw
        segmentFirstRight.draw(middleG);
        segmentLastRight.draw(middleG);
        // draw the right palm
        middleG.drawLine((int) segmentLastRight.x, (int) segmentLastRight.y, (int) xR1, (int) yR1);
        middleG.drawLine((int) segmentLastRight.x, (int) segmentLastRight.y, (int) xR2, (int) yR2);
        middleG.drawLine((int) segmentLastRight.x, (int) segmentLastRight.y, (int) xR3, (int) yR3);
        middleG.drawLine((int) segmentLastRight.x, (int) segmentLastRight.y, (int) xR4, (int) yR4);
    }
}