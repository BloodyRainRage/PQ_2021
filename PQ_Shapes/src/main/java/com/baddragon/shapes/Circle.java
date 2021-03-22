package com.baddragon.shapes;

import java.util.Random;

public class Circle implements Shape {

    public String name = "Circle";

    double radius;

    public double area;

    public Circle() {

        this.radius = new Random().doubles(-10, 10)
                .findFirst().getAsDouble();

        area = Math.PI * radius * radius;
    }

    @Override
    public double getArea() {
        return area;
    }

    @Override
    public String toString() {
        return "\nname: " + name + "\n" +
                "radius: " + radius + "\n" +
                "area: " + area;
    }
}
