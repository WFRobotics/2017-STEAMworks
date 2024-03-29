package org.wfrobotics.robot.driveoi;

import org.wfrobotics.reuse.controller.Xbox;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class Tank
{
    public interface TankIO
    {
        public double getL();
        public double getR();
        public double getThrottleSpeedAdjust();
    }

    public static class TankXbox implements TankIO
    {
        private final Xbox controller;

        public TankXbox(Xbox controller)
        {
            this.controller = controller;
        }

        public double getL()
        {
            return controller.getY(Hand.kLeft);
        }

        public double getR()
        {
            return controller.getY(Hand.kRight);
        }

        public double getThrottleSpeedAdjust()
        {
            return 0.5 + .5 * controller.getTrigger(Hand.kLeft);
        }
    }
}