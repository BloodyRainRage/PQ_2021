package com.baddragon.factory;

import com.baddragon.shapes.Shape;

import java.util.List;

public interface Factory {

    Shape getNextShape();

    List<Shape> getShapeList();

}
