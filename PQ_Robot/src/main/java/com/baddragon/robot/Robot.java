package com.baddragon.robot;

import com.baddragon.robot.legs.LeftLeg;
import com.baddragon.robot.legs.RightLeg;

public class Robot {

    public static final String monitor = "";
    public static String nextLegSteps = "";

    public Robot() {
        LeftLeg leftLeg = new LeftLeg();
        RightLeg rightLeg = new RightLeg();
        new Thread(leftLeg).start();
        rightLeg.start();

    }


}
