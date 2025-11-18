package InteractiveReptail;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class Vertebra {
    double x, y;
    double size;
    List<Vertebra> children = new ArrayList<>();
    Vertebra parent;
    Vertebra otherParent;
    Edge edge;
    Limb limb;
    double dist;
    double angle;
    double intermediateParentX, intermediateParentY;
    double tempParentX, tempParentY;

    Vertebra(Vertebra middleParent, double middleSize){
        size = middleSize;
        parent = middleParent;

        if (parent != null) {
            parent.children.add(this);
            x = parent.x - size;
            y = parent.y;
        }
    }

    void follow(){
        if (parent == null) return;

        // initializing the variables (if you comment out this block of code, it will be a cool feature)
        intermediateParentX = parent.x;
        intermediateParentY = parent.y;
        // for the limbs
        if (otherParent != null) {
            intermediateParentX = tempParentX;
            intermediateParentY = tempParentY;
        }

        dist = sqrt(pow(x - intermediateParentX, 2) + pow(y - intermediateParentY, 2));
        angle = atan2(intermediateParentY - y, intermediateParentX - x);

        if (dist > 1) {
            // we subtract PI because our angle is calculated from our segment to the parent,
            // and the segments should follow from the parent to ours, so we take the opposite angle
            x = intermediateParentX + size * cos(angle - PI);
            y = intermediateParentY + size * sin(angle - PI);
        }
        // for the limbs
        if (otherParent != null) {
            dist = sqrt(pow(x - otherParent.x, 2) + pow(y - otherParent.y, 2));
            if (dist > 1) {
                x = otherParent.x + size * (x - otherParent.x) / dist; // size * cos()
                y = otherParent.y + size * (y - otherParent.y) / dist; // size * sin()
            }
        }

        // starting the movement of the edges
        if (edge != null){
            edge.follow();
        }
        // we start the movement of limb
        if (limb != null){
            limb.follow();
        }
    }

    void draw(Graphics2D middleG) {
        middleG.setStroke(new BasicStroke(2.5f)); // line thickness

        // drawing segments
        if (parent == null) {
            middleG.drawLine((int) x, (int) y, (int) x, (int) y);
        }
        else {
            intermediateParentX = parent.x;
            intermediateParentY = parent.y;
            if (otherParent != null) {
                intermediateParentX = tempParentX;
                intermediateParentY = tempParentY;
            }
            middleG.drawLine((int) intermediateParentX, (int) intermediateParentY, (int) x, (int) y);
        }

        // we draw everything else
        if (edge != null){
            edge.draw(middleG);
        }
        if (limb != null){
            limb.draw(middleG);
        }
    }
}