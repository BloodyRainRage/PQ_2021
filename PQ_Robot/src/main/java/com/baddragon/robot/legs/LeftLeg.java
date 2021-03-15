package com.baddragon.robot.legs;

import com.baddragon.robot.Robot;

import static java.lang.Thread.sleep;

public class LeftLeg implements Runnable {
    @Override
    public void run() {
        for (int index = 0; index < 10; index++) {
            synchronized (Robot.monitor) {
                String name = Robot.nextLegSteps;
                if (name != null && name.isEmpty()) {
                    Robot.nextLegSteps = "left";
                }
                if (name.equals("left")) {
                    System.out.println("Left foot step");
                    Robot.nextLegSteps = "right";
                }
            }
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
