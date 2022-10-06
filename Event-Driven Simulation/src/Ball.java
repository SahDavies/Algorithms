import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;

public class Ball {
    private double rx, ry;          // position, also the centre of the ball
    private double vx, vy;          // velocity
    private final double radius;    // radius of ball

    // no argument constructor
    public Ball() {
        this(0.007);
    }

    // single argument constructor
    public Ball(double radius) {
        if (radius < 0.005 || radius > 0.4)
            throw new IllegalArgumentException("Radius " + radius +
                    " is out of bound. Use values in the range [0.005, 0.4)");

        this.radius = radius;
        /* velocity is inversely proportional to radius,
        * hence smaller balls are much faster
        * */
        vx = (StdRandom.uniform(-0.02, 0.02)) / (radius * 100);
        vy = (StdRandom.uniform(-0.02, 0.015)) / (radius * 100);
        rx = StdRandom.uniform() + radius;  // shift the return value of uniform()
        ry = StdRandom.uniform() + radius;  // shift the return value of uniform()

        if (rx > (1.0 - radius))    rx = rx - (2*radius);
        if (ry > (1.0 - radius))    ry = ry - (2*radius);
    }

    public void move(double dt) {
        // check for collision with walls. Note, the co-ordinate is between 0-1
        if ((rx + vx*dt < radius) || (rx + vx*dt > 1.0 - radius)) { vx = -vx; }
        if ((ry + vy*dt < radius) || (ry + vy*dt > 1.0 - radius)) { vy = -vy; }
        rx = rx + vx*dt;
        ry = ry + vy*dt;
    }

    public void draw() {
        StdDraw.filledCircle(rx, ry, radius);
    }
}
