import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MyPanel extends JPanel implements ActionListener, KeyListener ,MouseListener {

    int panelS = 800;
    int PANEL_WIDTH = panelS;
    int PANEL_HEIGHT = panelS;
    int rows = 2;
    int cols = rows;
    double repeat = 1;
    double layers = 6;
    double lacunarity = 2;
    double amplitude = 1;
    double persistance = .3;
    double scaleX = (double) PANEL_WIDTH / cols;
    double scaleY = (double) PANEL_HEIGHT / rows;
    double max,min;
    ArrayList<Vec3D> points = new ArrayList<>();
    ArrayList<int[]> faces = new ArrayList<>();
    ArrayList<Color> colors = new ArrayList<>();
    ArrayList<Polygon> faceP = new ArrayList<>();
    Vector[][] rays = new Vector[rows+1][cols+1];
    double[][] grid = new double[20][20];
    double[] distances;
    int[] drawOrder;
    Vec3D camera = new Vec3D(0,0,0);
    double fov = .5;
    double turnSpeed = 0.05;
    double moveSpeed = 1;
    boolean w,a,s,d,q,ee,W,A,S,D,add,sub,sh,ct,slow,fast;
    Random rand = new Random();
    Timer timer = new Timer(1, this);
    int tick =0;


    MyPanel() {

        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addMouseListener(this);
        for (int i = 0; i < rows+1; i++) {
            for (int j = 0; j < cols+1; j++) {
                double random = rand.nextDouble();
                rays[i][j] = new Vector(Math.cos(random*360),Math.sin(random*360));
            }
        }
        perlin();
        for(int i = 0; i < grid.length; i++){
            for(int j = 0; j < grid[0].length; j++){
                points.add(new Vec3D(j*10,i*10,grid[i][j]*40));
            }
        }
        for(int i = 0 ; i < grid.length-1; i++){
            for(int j = 0 ; j < grid.length-1; j++){
                int stride = grid.length;

                int tl = i + j * stride;
                int tr = i + 1 + j * stride;
                int bl = i + (j + 1) * stride;
                int br = i + 1 + (j + 1) * stride;

                faces.add(new int[]{tl,bl,br,tr});
            }
        }

        drawOrder = new int[faces.size()];
        distances = new double[faces.size()];

        //makeCube();
        //System.out.println(faces.get(0));


        timer = new Timer(1, this);
        timer.start();
    }

    private double findAngle(int[] face) {
        Vec3D p1 = points.get(face[0]);
        Vec3D p2 = points.get(face[1]);
        Vec3D p3 = points.get(face[2]);

        double ax = p2.x - p1.x;
        double ay = p2.y - p1.y;
        double az = p2.z - p1.z;

        double bx = p3.x - p1.x;
        double by = p3.y - p1.y;
        double bz = p3.z - p1.z;

        double nx = ay * bz - az * by;
        double ny = az * bx - ax * bz;
        double nz = ax * by - ay * bx;

        double vx = 0;
        double vy = -100;
        double vz = 0;

        double dot = nx * vx + ny * vy + nz * vz;
        double magN = Math.sqrt(nx * nx + ny * ny + nz * nz);
        double magV = Math.sqrt(vx * vx + vy * vy + vz * vz);

        return Math.acos(dot / (magN * magV));
    }

    private void makeCube() {
        points.add(new Vec3D(.25,.25,.25));
        points.add(new Vec3D(-.25,.25,.25));
        points.add(new Vec3D(.25,-.25,.25));
        points.add(new Vec3D(-.25,-.25,.25));
        points.add(new Vec3D(.25,.25,-.25));
        points.add(new Vec3D(-.25,.25,-.25));
        points.add(new Vec3D(.25,-.25,-.25));
        points.add(new Vec3D(-.25,-.25,-.25));
        faces.add(new int[]{0,1,2,4});
        faces.add(new int[]{3,7,1,2});
        faces.add(new int[]{5,1,4,7});
        faces.add(new int[]{6,2,4,7});
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        //connectFaces(g2d);
        drawFaces(g2d);
        //drawPoints(g2d);
        g2d.drawString(("Speed: " + moveSpeed),10,10);
    }
    public void connectFaces(Graphics2D g){
        for(int j = 0; j<faces.size(); j++){
            int[] face = faces.get(j);
            Vector p0 = getPoint2D(points.get(face[0]-1));
            Vector p1 = getPoint2D(points.get(face[face.length-1]-1));
            if (!(p0 == null || p1 == null)) {
                g.drawLine((int) p0.x, (int) p0.y, (int) p1.x, (int) p1.y);
            }
            Vector p2;
            for(int i = 1; i<face.length; i++){
                p1 = getPoint2D(points.get(face[i]-1));
                p2 = getPoint2D(points.get(face[i-1]-1));
                //System.out.println(z);
                if ((!(p1 == null || p2 == null))) {
                    double dist = points.get(face[i]-1).length()+points.get(face[i-1]-1).length();
                    dist /= 3;
                    dist = Math.min(Math.max(dist,0),255);
                    dist = 255-dist;
                    g.setColor(new Color((int) dist, (int) dist, (int) dist));
                    g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
                }
            }
        }
    }
    public void drawFaces(Graphics2D g){
        sortFaces();
        for(int i = 0; i < drawOrder.length; i++){
            int x = drawOrder[i] % (grid[0].length - 1);
            int y = drawOrder[i] / (grid[0].length - 1);
            double noise = (map(grid[x][y])*7);
            int idx = (int) (noise*30);
            g.setColor(new Color(idx,idx,idx));
            if(drawOrder[i]<faceP.size()){
                g.fillPolygon(faceP.get(drawOrder[i]));

            }
        }
    }
    public double map(double a){
        double n = (a-min)/(max-min);
        return n-0.00000001;
    }
    public void drawPoints(Graphics2D g){
        for(int j = 0; j<points.size(); j++){
            drawPoint(g,points.get(j));
        }
    }
    public void createFaces(){
        faceP.clear();
        for (int[] face : faces) {
            int[] xPoints = new int[face.length];
            int[] yPoints = new int[face.length];
            boolean used = false;
            for (int i = 0; i < face.length; i++) {
                if (getPoint2D(points.get(face[i])) != null) {
                    xPoints[i] = (int) getPoint2D(points.get(face[i])).x;
                    yPoints[i] = (int) getPoint2D(points.get(face[i])).y;
                    used = true;
                } else {
                    used = false;
                    break;
                }
            }
            if (used) {
                faceP.add(new Polygon(xPoints, yPoints, face.length));
            } else {
                faceP.add(new Polygon(xPoints, yPoints, 0));
            }
        }
    }
    public void drawPoint(Graphics2D g2d, Vec3D point) {
        Vector main = getPoint2D(point);
        if(main!=null){
            g2d.setColor(Color.GREEN);
            g2d.drawRect((int) main.x-5, (int) main.y-5,10,10);
        }
    }
    public void sortFaces(){
        getDistances();
        boolean[] used = new boolean[faces.size()];

        for (int i = 0; i < faces.size(); i++) {
            double cur = -Double.MAX_VALUE;
            int idx = -1;

            for (int j = 0; j < faces.size(); j++) {
                if (!used[j] && distances[j] > cur) {
                    cur = distances[j];
                    idx = j;
                }
            }

            drawOrder[i] = idx;
            used[idx] = true;
        }
    }

    private void perlin() {
        max = grid[0][0];
        min = grid[0][0];
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid[0].length; j++) {
                int rayX = Math.min((int)((double)i / grid.length * rows), rows - 1);
                int rayY = Math.min((int)((double)j / grid[0].length * cols), cols - 1);
                double x = (double)i / (grid.length - 1);
                double y = (double)j / (grid[0].length - 1);

                int xi = Math.min((int)(x * rows), rows - 1);
                int yi = Math.min((int)(y * cols), cols - 1);

                double xf = (x * rows) - xi;
                double yf = (y * cols) - yi;
                double u = smooth(xf);
                double v = smooth(yf);

                double q = rays[xi][yi].dotProd(xf, yf);
                double r = rays[xi+1][yi].dotProd(xf - 1, yf);
                double s = rays[xi][yi+1].dotProd(xf, yf - 1);
                double t = rays[xi+1][yi+1].dotProd(xf - 1, yf - 1);

                double top = lerp(q, r, u);
                double bottom = lerp(s, t, u);
                double prod = lerp(top, bottom, v);
                grid[i][j] += prod*amplitude;
                max = Math.max(max,grid[i][j]);
                min = Math.min(min,grid[i][j]);
            }
        }
    }
    public double lerp(double x,double y,double a) {
        return x+(y-x)*a;
    }
    public double smooth(double x) {
        return 6*x*x*x*x*x -15*x*x*x*x + 10*x*x*x;
    }
    private void getDistances() {
        for(int i = 0; i<faces.size(); i++){
            int[] face = faces.get(i);
            double x = 0;
            double y = 0;
            double z = 0;
            for(int j = 0; j<face.length; j++){
                Vec3D p = points.get(face[j]);
                x += p.x;
                y += p.y;
                z += p.z;
            }
            x /= face.length;
            y /= face.length;
            z /= face.length;
            double dist = Math.sqrt(x*x+y*y+z*z);
            distances[i] = dist;
        }
    }

    public Vector getPoint2D(Vec3D point) {
        double tempz = point.z;
        double tempx = point.x;
        double tempy = point.y;
        if(point.z>0){
            return null;
        }
        return new Vector((tempx*fov/(tempz)+1)/2*PANEL_WIDTH,(tempy*fov/(tempz)+1)/2*PANEL_HEIGHT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //System.out.println("faces" + faces.size() + "drawO " + drawOrder.length + " faceP " + faceP.size());
        tick++;
        if(w){
            for(int i = 0; i<points.size(); i++){
                double c = Math.cos(-turnSpeed);
                double s = Math.sin(-turnSpeed);
                double tempy = points.get(i).y;
                points.get(i).y = tempy*c-points.get(i).z*s;
                points.get(i).z = tempy*s+points.get(i).z*c;
            }
        }
        if(s){
            for(int i = 0; i<points.size(); i++){
                double c = Math.cos(turnSpeed);
                double s = Math.sin(turnSpeed);
                double tempy = points.get(i).y;
                points.get(i).y = tempy*c-points.get(i).z*s;
                points.get(i).z = tempy*s+points.get(i).z*c;
            }
        }
        if(a){
            for(int i = 0; i<points.size(); i++){
                double c = Math.cos(-turnSpeed);
                double s = Math.sin(-turnSpeed);
                double tempx = points.get(i).x;
                points.get(i).x = tempx*c-points.get(i).z*s;
                points.get(i).z = tempx*s+points.get(i).z*c;

            }
        }
        if(d){
            for(int i = 0; i<points.size(); i++){
                double c = Math.cos(turnSpeed);
                double s = Math.sin(turnSpeed);
                double tempx = points.get(i).x;
                points.get(i).x = tempx*c-points.get(i).z*s;
                points.get(i).z = tempx*s+points.get(i).z*c;

            }
        }
        if(q){
            for(int i = 0; i<points.size(); i++){
                double c = Math.cos(turnSpeed);
                double s = Math.sin(turnSpeed);
                double tempx = points.get(i).x;
                points.get(i).x = tempx*c-points.get(i).y*s;
                points.get(i).y = tempx*s+points.get(i).y*c;
            }
        }
        if(ee){
            for(int i = 0; i<points.size(); i++){
                double c = Math.cos(-turnSpeed);
                double s = Math.sin(-turnSpeed);
                double tempx = points.get(i).x;
                points.get(i).x = tempx*c-points.get(i).y*s;
                points.get(i).y = tempx*s+points.get(i).y*c;
            }

        }
        if(sh){
            for(int i = 0; i<points.size(); i++){
                points.get(i).z += moveSpeed;
            }
        }
        if(ct){
            for(int i = 0; i<points.size(); i++){
                points.get(i).z -= moveSpeed;
            }
        }
        if(add) {
            fov+=.01;
            if(fov>1)
                fov=1;
        }
        if(sub) {
            fov-=.01;
            if(fov<.1)
                fov=.1;
            //System.out.println(fov);
        }
        if(slow){
            moveSpeed-=.1;
            if(moveSpeed<0.01) {
                moveSpeed = 0.01;
            }
        }
        if(fast){
            moveSpeed+=.01;
        }
        createFaces();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 87: w = true; break;
            case 65: a = true; break;
            case 83: s = true; break;
            case 68: d = true; break;
            case 81: q = true; break;
            case 69: ee = true; break;
            case 38: W = true; break;
            case 37: A = true; break;
            case 40: S = true; break;
            case 39: D = true; break;
            case 45: sub = true; break;
            case 61: add = true; break;
            case 16: sh = true; break;
            case 17: ct = true; break;
            case 91: slow = true; break;
            case 93: fast = true; break;
        }
        //System.out.println(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 87:
                w = false;
                break;
            case 65:
                a = false;
                break;
            case 83:
                s = false;
                break;
            case 68:
                d = false;
                break;
                case 81: q = false; break;
                case 69: ee = false; break;
            case 38: W = false; break;
            case 37: A = false; break;
            case 40: S = false; break;
            case 39: D = false; break;
            case 45: sub = false; break;
            case 61: add = false; break;
            case 16: sh = false; break;
            case 17: ct = false; break;
            case 91: slow = false; break;
            case 93: fast = false; break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}