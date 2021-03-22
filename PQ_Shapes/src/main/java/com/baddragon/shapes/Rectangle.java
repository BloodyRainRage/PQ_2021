package com.baddragon.shapes;

import java.util.Random;

public class Rectangle implements Shape {

    public String name = "Rectangle";

    double edge;

    public double area;

    public Rectangle() {
        edge = new Random().doubles(-10, 10)
                .findFirst().getAsDouble();
        this.area = edge * edge;
    }

    @Override
    public double getArea() {
        return area;
    }

    @Override
    public String toString() {
        return "\nname: " + name + "\n" +
                "radius: " + edge + "\n" +
                "area: " + area;
    }
}
