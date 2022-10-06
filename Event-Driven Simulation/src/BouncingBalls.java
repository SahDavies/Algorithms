import edu.princeton.cs.algs4.StdDraw;

public class BouncingBalls {
    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        Ball[] balls = new Ball[N];
        // initialise array
        for (int i = 0; i < (N/2); i++) {
            balls[i] = new Ball();
        }
        for (int i = (N/2); i < (N/2 + N/4 + 10); i++) {
            balls[i] = new Ball(0.01);
        }
        for (int i = (N/2+N/4+10); i < N; i++) {
            balls[i] = new Ball(0.017);
        }


        while (true) {
            StdDraw.clear();
            for (int i = 0; i < N; i++) {
                balls[i].move(0.5);
                balls[i].draw();
            }
            StdDraw.show();
        }
    }
}
