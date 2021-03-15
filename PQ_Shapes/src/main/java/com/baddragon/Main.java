package com.baddragon;

import com.baddragon.factory.Factory;
import com.baddragon.factory.ShapeFactory;
import com.baddragon.shapes.Shape;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        Factory factory = new ShapeFactory();
        List<Shape> shapes = factory.getShapeList();
        Shape maxShape = shapes.get(0);
        for (Shape shape : shapes) {
            if (maxShape.getArea() < shape.getArea()) {
                maxShape = shape;
            }
        }
        System.out.println(shapes);
        System.out.println("max shape");
        System.out.println(maxShape);

    }
}
