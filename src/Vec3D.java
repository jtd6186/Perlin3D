import java.text.DecimalFormat;

public class Vec3D {
    public double x;
    public double y;
    public double z;
    DecimalFormat df = new DecimalFormat("0.00");

    public Vec3D(double a, double b, double c)
    {
        x = a;
        y = b;
        z = c;
    }
    public Vec3D()
    {
        x = 0;
        y = 0;
        z = 0;
    }
    public Vec3D(Vec3D a)
    {
        x = a.x;
        y = a.y;
        z = a.z;
    }
    public Vec3D(int a, int b, int c)
    {
        x = a;
        y = b;
        z = c;
    }
    public void set(Vec3D a)
    {
        x = a.x;
        y = a.y;
        z = a.z;
    }
    public void set(double a, double b,double c)
    {
        x = a;
        y = b;
        z = c;
    }
    public void set(int a, int b, int c)
    {
        x = a;
        y = b;
        z = c;
    }
    public Vec3D add(double a)
    {
        return new Vec3D(x+a,y+a,z+a);
    }
    public Vec3D add(Vec3D a)
    {
        return new Vec3D(x+a.x,y+a.y,z+a.z);
    }
    public Vec3D subtract(Vec3D a)
    {
        return new Vec3D(x-a.x,y-a.y,z-a.z);
    }
    public Vec3D multiply(double a)
    {
        return new Vec3D(x*a,y*a,z*a); //PLEASE MAKE SURE THAT IT IS x,y , IVE BEEN MESSING WITH THIS FOR TWO DAYS TO REALIZE ITS x,x :((((
    }
    public Vec3D multiply(Vec3D a)
    {
        return new Vec3D(x*a.x,y*a.y,z*a.z);
    }
    public Vec3D divideBy(double a)
    {
        return new Vec3D(x/a,y/a,z/a);
    }
    public Vec3D randPos(int m)
    {
        return new Vec3D(Math.random()*m,Math.random()*m,Math.random()*m);
    }
    public Vec3D abs()
    {
        return new Vec3D(Math.abs(x),Math.abs(y),Math.abs(z));
    }
    public double length()
    {
        return Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2));
    }
    public Vec3D reverse()
    {
        return new Vec3D(-x,-y,-z);
    }
    public Vec3D difference(Vec3D a)
    {
        return new Vec3D(a.x-x,a.y-y,a.z-z);
    }
    public Vec3D normalize()
    {
        double len = length();
        len = len==0?.000001:len;
        return new Vec3D(x/len,y/len,z/len);
    }
    public String toString()
    {
        return "<" + df.format(x) + " ," + df.format(y) + " ," + df.format(z) + ">";
    }
}
