package com.baddragon.factory;

import com.baddragon.shapes.Circle;
import com.baddragon.shapes.Rectangle;
import com.baddragon.shapes.Shape;
import com.baddragon.shapes.Triangle;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShapeFactory implements Factory {

    Class[] shapeClasses = new Class[]{
            Circle.class,
            Triangle.class,
            Rectangle.class
    };

    Random random = new Random();

    @Override
    public Shape getNextShape() {

        Class shapeClass = shapeClasses[random.nextInt(shapeClasses.length)];

        try {
            return (Shape) shapeClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException |
                InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Shape> getShapeList() {
        List<Shape> shapes = new ArrayList<>();

        while (shapes.size() != 10) {
            Shape shape = getNextShape();
            if (shape == null) continue;

            shapes.add(getNextShape());
        }

        return shapes;
    }
}
