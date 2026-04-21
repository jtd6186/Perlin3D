import java.text.DecimalFormat;

public class Vector {
	public double x;
	public double y;
	DecimalFormat df = new DecimalFormat("0.00");

	public Vector(double a, double b)
	{
		x = a;
		y = b;
	}
	public Vector()
	{
		x = 0;
		y = 0;
	}
	public Vector(Vector a)
	{
		x = a.x;
		y = a.y;
	}
	public Vector(int a, int b)
	{
		x = a;
		y = b;
	}
	public void set(Vector a)
	{
		x = a.x;
		y = a.y;
	}
	public void set(double a, double b)
	{
		x = a;
		y = b;
	}
	public void set(int a, int b)
	{
		x = a;
		y = b;
	}
	public int getX(){return (int) x; }
	public int getY(){return (int) y; }
	public void boxLimit(double a, double b){
		if(x>a) {
			x = a;
		} else if(x<0) {
			x = 0;
		}
		if(y>b) {
			y = b;
		} else if(y<0) {
			y = 0;
		}
	}
	public Vector boxLimitVelA(double a, double b,Vector v){
		if(x>a) {
			x = a;
			v.x = -v.x;
		} else if(x<0) {
			x = 0;
			v.x = -v.x;
		}
		if(y>b) {
			y = b;
			v.y = -v.y;
		} else if(y<0) {
			y = 0;
			v.y = -v.y;
		}
		return v;
	}
	public Vector boxLimitVelB(double a, double b,Vector v){
		if(x>a) {
			x = 0;
		} else if(x<0) {
			x = a;
		}
		if(y>b) {
			y = 0;
		} else if(y<0) {
			y = b;
		}
		return v;
	}
	public Vector add(double a)
	{
		return new Vector(x+a,y+a);
	}
	public Vector add(Vector a)
	{
		return new Vector(x+a.x,y+a.y);
	}
	public Vector subtract(Vector a)
	{
		return new Vector(x-a.x,y-a.y);
	}
	public Vector multiply(double a)
	{
		return new Vector(x*a,y*a); //PLEASE MAKE SURE THAT IT IS x,y , IVE BEEN MESSING WITH THIS FOR TWO DAYS TO REALIZE ITS x,x :((((
	}
	public Vector multiply(Vector a)
	{
		return new Vector(x*a.x,y*a.y);
	}
	public Vector divideBy(double a)
	{
		return new Vector(x/a,y/a);
	}
	public Vector randNorm(){
		double seed = Math.random()*360;
		return new Vector(Math.sin(seed),Math.cos(seed));
	}
	public Vector randVel(int m)
	{
		return new Vector(Math.random()*m*Math.sin(Math.random()*360),Math.random()*m*Math.cos(Math.random()*360));
	}
	public Vector randPos(int m)
	{
		return new Vector(Math.random()*m,Math.random()*m);
	}
	public Vector abs()
	{
		return new Vector(Math.abs(x),Math.abs(y));
	}
	public Vector log()
	{
		int polx = (int) (Math.abs(x)/x);
		int poly = (int) (Math.abs(y)/y);
		double tempx = Math.log10(Math.abs(x)+1);
		double tempy = Math.log10(Math.abs(y)+1);
		return new Vector(polx*tempx,poly*tempy);
	}
	public double length()
	{
		return Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
	}
	public Vector reverse()
	{
		return new Vector(-x,-y);
	}
	public Vector swap()
	{
		return new Vector(y,x);

	}
	public Vector difference(Vector a)
	{
		return new Vector(a.x-x,a.y-y);
	}
	public Vector limit(double lois) //Bounds
	{
		if(this.length()>lois)
			return this.normalize().multiply(lois);
		else
			return this;
	}
	public double dotProd(Vector a)
	{
		return x*a.x+y*a.y;
	}
	public double dotProd(double a, double b){
		return x*a+y*b;
	}
	public Vector rotate(double degrees){
		double deg = Math.atan2(y,x);
		deg+=degrees;
		double length = length();
		return new Vector(Math.cos(deg)*length,Math.sin(deg)*length);
	}
	public boolean inBounds()
	{
		return (!((Double) x).isNaN()&&!((Double) y).isNaN());
	}
	public Vector average(Vector a)
	{
		return new Vector((x+a.x)/2,(y+a.y)/2);
	}
	public Vector normalize()
	{
		double len = length();
		if(len==0) return new Vector(0,0);
		return new Vector(x/len,y/len);
	}
	public Vector radNorm(double a)
	{
		return new Vector(Math.cos(a),Math.sin(a));
	}
	public Vector lerp(Vector a, double t)
	{
		return this.normalize().multiply(t).add(a.multiply(1-t)).normalize().multiply(this.length());
	}
	public String toString()
	{
		return "<" + df.format(x) + " ," + df.format(y) + ">";
	}
}
