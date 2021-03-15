package com.baddragon.shapes;

import java.util.Random;

public class Triangle implements Shape{

    public String name = "Triangle";

    double edge;

    public double area;

    public Triangle(){
        Random random = new Random();
        edge = random.doubles(-10, 10)
                .findFirst().getAsDouble();
    }

    @Override
    public double getArea() {
        area = (edge*edge * Math.sqrt(3))/4;
        return area;
    }

    @Override
    public String toString() {
        return "\nname: " + name + "\n" +
                "radius: " + edge + "\n" +
                "area: " + area;
    }
}
