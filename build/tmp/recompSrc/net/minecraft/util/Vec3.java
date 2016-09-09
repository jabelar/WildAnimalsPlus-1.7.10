package net.minecraft.util;

public class Vec3
{
    /** X coordinate of Vec3D */
    public double xCoord;
    /** Y coordinate of Vec3D */
    public double yCoord;
    /** Z coordinate of Vec3D */
    public double zCoord;
    private static final String __OBFID = "CL_00000612";

    /**
     * Static method for creating a new Vec3D given the three x,y,z values. This is only called from the other static
     * method which creates and places it in the list.
     */
    public static Vec3 createVectorHelper(double x, double y, double z)
    {
        return new Vec3(x, y, z);
    }

    protected Vec3(double x, double y, double z)
    {
        if (x == -0.0D)
        {
            x = 0.0D;
        }

        if (y == -0.0D)
        {
            y = 0.0D;
        }

        if (z == -0.0D)
        {
            z = 0.0D;
        }

        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    /**
     * Sets the x,y,z components of the vector as specified.
     */
    protected Vec3 setComponents(double x, double y, double z)
    {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        return this;
    }

    /**
     * Returns a new vector with the result of the specified vector minus this.
     */
    public Vec3 subtract(Vec3 vec)
    {
        /**
         * Static method for creating a new Vec3D given the three x,y,z values. This is only called from the other
         * static method which creates and places it in the list.
         */
        return createVectorHelper(vec.xCoord - this.xCoord, vec.yCoord - this.yCoord, vec.zCoord - this.zCoord);
    }

    /**
     * Normalizes the vector to a length of 1 (except if it is the zero vector)
     */
    public Vec3 normalize()
    {
        double d0 = (double)MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
        return d0 < 1.0E-4D ? createVectorHelper(0.0D, 0.0D, 0.0D) : createVectorHelper(this.xCoord / d0, this.yCoord / d0, this.zCoord / d0);
    }

    public double dotProduct(Vec3 vec)
    {
        return this.xCoord * vec.xCoord + this.yCoord * vec.yCoord + this.zCoord * vec.zCoord;
    }

    /**
     * Returns a new vector with the result of this vector x the specified vector.
     */
    public Vec3 crossProduct(Vec3 vec)
    {
        /**
         * Static method for creating a new Vec3D given the three x,y,z values. This is only called from the other
         * static method which creates and places it in the list.
         */
        return createVectorHelper(this.yCoord * vec.zCoord - this.zCoord * vec.yCoord, this.zCoord * vec.xCoord - this.xCoord * vec.zCoord, this.xCoord * vec.yCoord - this.yCoord * vec.xCoord);
    }

    /**
     * Adds the specified x,y,z vector components to this vector and returns the resulting vector. Does not change this
     * vector.
     */
    public Vec3 addVector(double x, double y, double z)
    {
        /**
         * Static method for creating a new Vec3D given the three x,y,z values. This is only called from the other
         * static method which creates and places it in the list.
         */
        return createVectorHelper(this.xCoord + x, this.yCoord + y, this.zCoord + z);
    }

    /**
     * Euclidean distance between this and the specified vector, returned as double.
     */
    public double distanceTo(Vec3 vec)
    {
        double d0 = vec.xCoord - this.xCoord;
        double d1 = vec.yCoord - this.yCoord;
        double d2 = vec.zCoord - this.zCoord;
        return (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }

    /**
     * The square of the Euclidean distance between this and the specified vector.
     */
    public double squareDistanceTo(Vec3 vec)
    {
        double d0 = vec.xCoord - this.xCoord;
        double d1 = vec.yCoord - this.yCoord;
        double d2 = vec.zCoord - this.zCoord;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * The square of the Euclidean distance between this and the vector of x,y,z components passed in.
     */
    public double squareDistanceTo(double x, double y, double z)
    {
        double d3 = x - this.xCoord;
        double d4 = y - this.yCoord;
        double d5 = z - this.zCoord;
        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    /**
     * Returns the length of the vector.
     */
    public double lengthVector()
    {
        return (double)MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
    }

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithXValue(Vec3 vec, double x)
    {
        double d1 = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;

        if (d1 * d1 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double d4 = (x - this.xCoord) / d1;
            return d4 >= 0.0D && d4 <= 1.0D ? createVectorHelper(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithYValue(Vec3 vec, double y)
    {
        double d1 = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;

        if (d2 * d2 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double d4 = (y - this.yCoord) / d2;
            return d4 >= 0.0D && d4 <= 1.0D ? createVectorHelper(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithZValue(Vec3 vec, double z)
    {
        double d1 = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;

        if (d3 * d3 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double d4 = (z - this.zCoord) / d3;
            return d4 >= 0.0D && d4 <= 1.0D ? createVectorHelper(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
        }
    }

    public String toString()
    {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
    }

    /**
     * Rotates the vector around the x axis by the specified angle.
     */
    public void rotateAroundX(float angle)
    {
        float f1 = MathHelper.cos(angle);
        float f2 = MathHelper.sin(angle);
        double d0 = this.xCoord;
        double d1 = this.yCoord * (double)f1 + this.zCoord * (double)f2;
        double d2 = this.zCoord * (double)f1 - this.yCoord * (double)f2;
        this.setComponents(d0, d1, d2);
    }

    /**
     * Rotates the vector around the y axis by the specified angle.
     */
    public void rotateAroundY(float angle)
    {
        float f1 = MathHelper.cos(angle);
        float f2 = MathHelper.sin(angle);
        double d0 = this.xCoord * (double)f1 + this.zCoord * (double)f2;
        double d1 = this.yCoord;
        double d2 = this.zCoord * (double)f1 - this.xCoord * (double)f2;
        this.setComponents(d0, d1, d2);
    }

    /**
     * Rotates the vector around the z axis by the specified angle.
     */
    public void rotateAroundZ(float angle)
    {
        float f1 = MathHelper.cos(angle);
        float f2 = MathHelper.sin(angle);
        double d0 = this.xCoord * (double)f1 + this.yCoord * (double)f2;
        double d1 = this.yCoord * (double)f1 - this.xCoord * (double)f2;
        double d2 = this.zCoord;
        this.setComponents(d0, d1, d2);
    }
}