package org.wfrobotics.reuse.utilities;

import java.util.logging.Level;

// TODO cross broken? parallel should have mag zero

/** To enable unit tests: Tests.java->Properties->Run/debug settings->Tests->Edit->Arguments->VM args->-ea */
public class Tests
{
    static final double width = 4;
    static final double depth = 3;

    static double maxWheelMagnitudeLast;
    static HerdVector wheelBR;
    static HerdVector wheelFR;
    static HerdVector wheelFL;
    static HerdVector wheelBL;
    static HerdVector rBR;
    static HerdVector rFR;
    static HerdVector rFL;
    static HerdVector rBL;

    static HerdLogger logS = new HerdLogger("Static Test");

    public static void main(String[] args)
    {
        FastTrig.cos(359);  // FastTrig init cache

        //debugFastTrig();
        //printDivider();

        debugHerdAngle();

        debugHerdVector();
        printDivider();

        Vector robotV = Vector.NewFromMagAngle(1, 5);
        double robotS = -.1;
        debugOldChassisToWheelVectors(robotV, robotS);
        printDivider();

        HerdVector v = new HerdVector(robotV.getMag(), robotV.getAngle() + 90);
        double spin = robotS;
        debugNewChassisToWheelVectors(v, spin);
        printDivider();

        //debugWheelVectorsToChassis();
        //printDivider();
        try
        {
            debugLogging();
            printDivider();
        }
        catch (UnsatisfiedLinkError e)
        {
            // Not on Robot, change HerdLogger handler to target System.out
        }
    }

    public static void debugHerdAngle()
    {
        // Constructor - No wrap
        assert new HerdAngle(10).getAngle() == 10 : "Positive in valid range failed to stay same angle";
        assert new HerdAngle(-10).getAngle() == -10 : "Negative in valid range failed to stay same angle";

        // Rotate positive - No wrap
        assert new HerdAngle(10).rotate(30).getAngle() == 40 : "Positive in failed to rotate positive direction";
        assert new HerdAngle(-10).rotate(30).getAngle() == 20 : "Negative in failed to rotate positive direction";

        // Rotate negative - No wrap
        assert new HerdAngle(10).rotate(-30).getAngle() == -20 : "Positive in failed to rotate negative direction";
        assert new HerdAngle(-10).rotate(-30).getAngle() == -40 : "Negative in failed to rotate negative direction";

        // Constructor - Wrap
        assert new HerdAngle(190).getAngle() == -170 : "Positive outside valid range failed to wrap angle";
        assert new HerdAngle(-190).getAngle() == 170 : "Negative outside valid range failed to wrap angle";

        // Rotate positive - Wrap
        assert new HerdAngle(10).rotate(190).getAngle() == -160 : "Positive in failed to rotate positive and wrap negative";
        assert new HerdAngle(-10).rotate(210).getAngle() == -160 : "Negative in failed to rotate positive and wrap negative";

        // Rotate negative - Wrap
        assert new HerdAngle(10).rotate(-210).getAngle() == 160 : "Positive in failed to rotate negative and wrap positive";
        assert new HerdAngle(-10).rotate(-190).getAngle() == 160 : "Negative in failed to rotate negative and wrap positive";

        // Rotate WrappedAngle
        assert new HerdAngle(10).rotate(new HerdVector(1, 30)).getAngle() == 40 : "HerdAngle cannot rotate by positive HerdVector";
        assert new HerdAngle(10).rotate(new HerdVector(1, -30)).getAngle() == -20 : "HerdAngle cannot rotate by negative HerdVector";
        assert new HerdAngle(10).rotateReverse(new HerdVector(1, 30)).getAngle() == -20 : "HerdAngle cannot inverse rotate by positive HerdVector";
        assert new HerdAngle(10).rotateReverse(new HerdVector(1, -30)).getAngle() == 40 : "HerdAngle cannot inverse rotate by negative HerdVector";
    }

    public static void debugHerdVector()
    {
        // New Vector: Trig
        HerdVector a;
        HerdVector b;
        Vector C;
        long start, end;

        debugHerdVectorWrappedAngle();

        a = new HerdVector(1, 60);
        C = Vector.NewFromMagAngle(1, 60);

        double x, y;
        start = System.nanoTime();
        x = a.getX();
        y = a.getY();
        end = System.nanoTime();
        System.out.println("A.X: " + x);
        System.out.println("A.Y: " + y);
        System.out.println("Durration: " + ((end - start)/1000) + " ns");
        start = System.nanoTime();
        x = C.getX();
        y = C.getY();
        end = System.nanoTime();
        System.out.println("B.X: " + x);
        System.out.println("B.Y: " + y);
        System.out.println("Durration: " + ((end - start)/1000) + " ns");
        System.out.println();

        // Constructor: Positive mag
        assert new HerdVector(-3, 10).getMag() == 3 : "Positive mag on > 1 mag";
        assert new HerdVector(-1, 10).getMag() == 1 : "Positive mag on 1 mag";
        assert new HerdVector(-.125, 10).getMag() == .125 : "Positive < 1 mag";

        // New Vector: Zero
        b = new HerdVector(0, 180);
        System.out.println("(0, 0): " + b);
        System.out.println();

        // Scale - By positive
        assert new HerdVector(2, 30).scale(3).getMag() == 6 : "Scale by bigger positive double failed mag";
        assert new HerdVector(2, 30).scale(3).getAngle() == 30 : "Scale by bigger positive double failed angle same";
        assert new HerdVector(2, 30).scale(1).getMag() == 2 : "Scale by double 1 failed mag same";
        assert new HerdVector(2, 30).scale(1).getAngle() == 30 : "Scale by double 1 failed angle same";
        assert new HerdVector(2, 30).scale(.125).getMag() == .25 : "Scale by smaller positive double failed mag";
        assert new HerdVector(2, 30).scale(.125).getAngle() == 30 : "Scale by smaller positive double failed angle same";

        // Scale - By negative
        assert new HerdVector(2, 30).scale(-3).getMag() == 6 : "Scale by positive double failed mag";
        assert new HerdVector(2, 30).scale(-3).getAngle() == -150 : "Scale by positive double failed angle flip";
        assert new HerdVector(2, 30).scale(-1).getMag() == 2 : "Scale by double -1 failed mag";
        assert new HerdVector(2, 30).scale(-1).getAngle() == -150 : "Scale by double -1 failed angle flip";
        assert new HerdVector(2, 30).scale(-.125).getMag() == .25 : "Scale by negative double failed mag";
        assert new HerdVector(2, 30).scale(-.125).getAngle() == -150 : "Scale by negative double failed angle flip";

        // Clone
        a = new HerdVector(1, 10);
        b = new HerdVector(a);
        b = b.rotate(45);
        assert a.getAngle() == 10 : "Clone overwrites original";

        // Addition
        a = new HerdVector(1, 0);
        b = new HerdVector(1, 90);

        System.out.println(a.add(b));
        System.out.println();

        assert new HerdVector(1, 135).add(new HerdVector(1, 135)).getMag() == 2 : "Add HerdVector to self not twice mag";
        assert new HerdVector(1, 135).add(new HerdVector(1, 135)).getAngle() == 135 : "Add HerdVector to self not same angle";

        // Cross
        a = new HerdVector(2, 0);
        b = new HerdVector(3, 85);

        System.out.format(a + " cross " + b + " = " + a.cross(b) + "\n");
        b = b.rotate(180);
        System.out.format(a + " cross " + b + " = " + a.cross(b) + "\n");

        // Cross - Limits
        //assert new HerdVector(2, 0).cross(new HerdVector(3, 0)).getMag() == 0 : "Cross zero mag on parallel";
        assert new HerdVector(2, 0).cross(new HerdVector(3, 90)).getMag() == 6 : "Cross max mag on orthogonal";

        // Clamp
        assert new HerdVector(.44, 10).clampToRange(.45, .55).getMag() == .45 : "Clamp min";
        assert new HerdVector(.5, 10).clampToRange(.45, .55).getMag() == .5 : "Clamp unaffected";
        assert new HerdVector(.56, 10).clampToRange(.45, .55).getMag() == .55 : "Clamp max";
    }

    public static void debugHerdVectorWrappedAngle()
    {
        // Constructor - No wrap
        assert new HerdVector(1, 10).getAngle() == 10 : "Positive in valid range failed to stay same angle";
        assert new HerdVector(1, -10).getAngle() == -10 : "Negative in valid range failed to stay same angle";

        // Rotate positive - No wrap
        assert new HerdVector(1, 10).rotate(30).getAngle() == 40 : "Positive in failed to rotate positive direction";
        assert new HerdVector(1, -10).rotate(30).getAngle() == 20 : "Negative in failed to rotate positive direction";

        // Rotate negative - No wrap
        assert new HerdVector(1, 10).rotate(-30).getAngle() == -20 : "Positive in failed to rotate negative direction";
        assert new HerdVector(1, -10).rotate(-30).getAngle() == -40 : "Negative in failed to rotate negative direction";

        // Constructor - Wrap
        assert new HerdVector(1, 190).getAngle() == -170 : "Positive outside valid range failed to wrap angle";
        assert new HerdVector(1, -190).getAngle() == 170 : "Negative outside valid range failed to wrap angle";

        // Rotate positive - Wrap
        assert new HerdVector(1, 10).rotate(190).getAngle() == -160 : "Positive in failed to rotate positive and wrap negative";
        assert new HerdVector(1, -10).rotate(210).getAngle() == -160 : "Negative in failed to rotate positive and wrap negative";

        // Rotate negative - Wrap
        assert new HerdVector(1, 10).rotate(-210).getAngle() == 160 : "Positive in failed to rotate negative and wrap positive";
        assert new HerdVector(1, -10).rotate(-190).getAngle() == 160 : "Negative in failed to rotate negative and wrap positive";

        // Rotate WrappedAngle
        assert new HerdVector(1, 30).rotate(new HerdAngle(10)).getAngle() == 40 : "HerdVector cannot rotate by positive HerdAngle";
        assert new HerdVector(1, 30).rotate(new HerdAngle(-10)).getAngle() == 20 : "HerdVector cannot rotate by negative HerdAngle";
        assert new HerdVector(1, 30).rotateReverse(new HerdAngle(10)).getAngle() == 20 : "HerdVector cannot inverse rotate by positive HerdAngle";
        assert new HerdVector(1, 30).rotateReverse(new HerdAngle(-10)).getAngle() == 40 : "HerdVector cannot inverse rotate by negative HerdAngle";
    }

    public static HerdVector[] debugNewChassisToWheelVectors(HerdVector v, double spin)
    {
        double start, end;
        HerdVector w = new  HerdVector(spin, 90);
        System.out.format("Command (%.2f, %.2f, %.2f)\n\n", v.getMag(), v.getAngle(), spin);

        HerdVector rWidth = new HerdVector(width, 90);
        HerdVector rHeight = new HerdVector(depth, 0);

        rFR = rWidth.add(rHeight);
        rBR = rWidth.sub(rHeight);
        rFL = rWidth.rotate(180).add(rHeight);
        rBL = rWidth.rotate(180).sub(rHeight);

        double unitVectorCorrection = 1 / rBR.getMag();
        rFR = rFR.scale(unitVectorCorrection);
        rFL = rFL.scale(unitVectorCorrection);
        rBR = rBR.scale(unitVectorCorrection);
        rBL = rBL.scale(unitVectorCorrection);

        System.out.println("FR: " + rFR);
        System.out.println("FL: " + rFL);
        System.out.println("BR: " + rBR);
        System.out.println("BL: " + rBL);
        System.out.println();

        //        System.out.println("Cross FR: " + w.cross(rFR));
        //        System.out.println("Cross FL: " + w.cross(rFL));
        //        System.out.println("Cross BR: " + w.cross(rBR));
        //        System.out.println("Cross BL: " + w.cross(rBL));
        //        System.out.println();

        start = System.nanoTime();

        // v + w x r
        HerdVector wheelFR = v.add(w.cross(rFR));
        HerdVector wheelFL = v.add(w.cross(rFL));
        HerdVector wheelBR = v.add(w.cross(rBR));
        HerdVector wheelBL = v.add(w.cross(rBL));

        double maxMag = wheelFR.getMag();
        maxMag = (wheelFL.getMag() > maxMag) ? wheelFL.getMag(): maxMag;
        maxMag = (wheelBR.getMag() > maxMag) ? wheelBR.getMag(): maxMag;
        maxMag = (wheelBL.getMag() > maxMag) ? wheelBL.getMag(): maxMag;

        if (maxMag > 1)
        {
            wheelFR = wheelFR.scale(1 / maxMag);
            wheelFL = wheelFL.scale(1 / maxMag);
            wheelBR = wheelBR.scale(1 / maxMag);
            wheelBL = wheelBL.scale(1 / maxMag);
            Tests.maxWheelMagnitudeLast = maxMag;
        }
        else
        {
            Tests.maxWheelMagnitudeLast = 1;
        }

        end = System.nanoTime();

        System.out.println("FR: " + wheelFR);
        System.out.println("FL: " + wheelFL);
        System.out.println("BR: " + wheelBR);
        System.out.println("BL: " + wheelBL);
        System.out.println();

        double twist = -90;

        HerdVector oldFR = wheelFR.rotate(twist);
        HerdVector oldFL = wheelFL.rotate(twist);
        HerdVector oldBR = wheelBR.rotate(twist);
        HerdVector oldBL = wheelBL.rotate(twist);

        double mirrorY = -2;

        oldFR = oldFR.rotate(oldFR.getAngle() * mirrorY);
        oldFL = oldFL.rotate(oldFL.getAngle() * mirrorY);
        oldBR = oldBR.rotate(oldBR.getAngle() * mirrorY);
        oldBL = oldBL.rotate(oldBL.getAngle() * mirrorY);

        System.out.format("Translate (Twist %.0f, Mirror Y)\n", twist);
        System.out.println("FR old: " + oldFR);
        System.out.println("FL old: " + oldFL);
        System.out.println("BR old: " + oldBR);
        System.out.println("BL old: " + oldBL);
        System.out.println();

        System.out.println("Wheel Calcs: " + ((end - start)/1000) + " ns");
        System.out.format("Wheel Mag wanted: %.2f\n", Tests.maxWheelMagnitudeLast);

        Tests.wheelFR = wheelFR;
        Tests.wheelFL = wheelFL;
        Tests.wheelBR = wheelBR;
        Tests.wheelBL = wheelBL;


        HerdVector[] wheelCommands = new HerdVector[4];

        wheelCommands[0] = wheelFR;
        wheelCommands[1] = wheelFL;
        wheelCommands[2] = wheelBR;
        wheelCommands[3] = wheelBL;

        return wheelCommands;
    }

    public static void debugWheelVectorsToChassis()
    {
        double maxMag = maxWheelMagnitudeLast;
        double start = System.nanoTime();

        // Undo Mirror
        HerdVector reconstructWheelBR = new HerdVector(wheelBR.getMag(), -wheelBR.getAngle());
        HerdVector reconstructWheelFR = new HerdVector(wheelFR.getMag(), -wheelFR.getAngle());
        HerdVector reconstructWheelFL = new HerdVector(wheelFL.getMag(), -wheelFL.getAngle());
        HerdVector reconstructWheelBL = new HerdVector(wheelBL.getMag(), -wheelBL.getAngle());

        //        System.out.println("UnMirrored BR: " + reconstructWheelBR);
        //        System.out.println("UnMirrored FR: " + reconstructWheelFR);
        //        System.out.println("UnMirrored FL: " + reconstructWheelFL);
        //        System.out.println("UnMirrored BL: " + reconstructWheelBL);

        // Undo scale
        reconstructWheelBR = reconstructWheelBR.scale(maxMag);
        reconstructWheelFR = reconstructWheelFR.scale(maxMag);
        reconstructWheelFL = reconstructWheelFL.scale(maxMag);
        reconstructWheelBL = reconstructWheelBL.scale(maxMag);

        // v + w x r
        //        System.out.println("Unscaled BR: " + reconstructWheelBR);
        //        System.out.println("Unscaled FR: " + reconstructWheelFR);
        //        System.out.println("Unscaled FL: " + reconstructWheelFL);
        //        System.out.println("Unscaled BL: " + reconstructWheelBL);

        // Summing the wheel vectors, the w x r's components cancel, leaving the chassis command scaled by four
        HerdVector frankenstein = new HerdVector(reconstructWheelBR);
        frankenstein = frankenstein.add(reconstructWheelFR);
        frankenstein = frankenstein.add(reconstructWheelFL);
        frankenstein = frankenstein.add(reconstructWheelBL);
        frankenstein = frankenstein.scale(.25);

        double end = System.nanoTime();

        //        // w x r
        //        HerdVector reconstructWxBR = reconstructWheelBR.sub(v);
        //        HerdVector reconstructWxFR = reconstructWheelFR.sub(v);
        //        HerdVector reconstructWxFL = reconstructWheelFL.sub(v);
        //        HerdVector reconstructWxBL = reconstructWheelBL.sub(v);
        //
        //        System.out.println("Reconstruct BR: " + reconstructWxBR);
        //        System.out.println("Reconstruct FR: " + reconstructWxFR);
        //        System.out.println("Reconstruct FL: " + reconstructWxFL);
        //        System.out.println("Reconstruct BL: " + reconstructWxBL);

        HerdVector wXr = reconstructWheelBR.scale(maxMag);
        wXr = reconstructWheelBR.sub(frankenstein);

        System.out.println("Reconstructing Robot Command");
        System.out.format("Robot Command (%.2f, %.2f, %.2f)\n", frankenstein.getMag(), frankenstein.getAngle(), wXr.getMag());
        System.out.println("Wheel Calcs: " + ((end - start)/1000) + " ns");
    }

    public static void debugOldChassisToWheelVectors(Vector robotV, double robotS)
    {
        double start, end;
        // Cartesian Wheel Vectors
        double CHASSIS_WIDTH = width;
        double CHASSIS_DEPTH = depth;
        double CHASSIS_SCALE = Math.sqrt(CHASSIS_WIDTH * CHASSIS_WIDTH + CHASSIS_DEPTH * CHASSIS_DEPTH);

        double[][] POSITIONS = {
                { CHASSIS_WIDTH / CHASSIS_SCALE, -CHASSIS_DEPTH / CHASSIS_SCALE }, // back right
                { -CHASSIS_WIDTH / CHASSIS_SCALE, -CHASSIS_DEPTH / CHASSIS_SCALE }, // back left
                { CHASSIS_WIDTH / CHASSIS_SCALE, CHASSIS_DEPTH / CHASSIS_SCALE },  // front right
                { -CHASSIS_WIDTH / CHASSIS_SCALE, CHASSIS_DEPTH / CHASSIS_SCALE }}; // front left
        String[] names = {"FR","FL", "BR","BL"};
        Vector[] positions = new Vector[4];
        Vector velocity = Vector.NewFromMagAngle(robotV.getMag(), robotV.getAngle());  // Positive x-axis
        double spin = robotS;

        System.out.format("Width: %.2f, Depth: %.2f\n", CHASSIS_WIDTH, CHASSIS_DEPTH);
        System.out.format("Command (%.2f, %.2f, %.2f)\n\n", velocity.getMag(), velocity.getAngle(), spin);

        for (int index = 0; index < 4; index++)
        {
            positions[index] = new Vector(POSITIONS[index]);
            System.out.format("%s: (%.2f, %.2f)\n", names[index], positions[index].getMag(), positions[index].getAngle());
        }
        System.out.println();

        start = System.nanoTime();
        Vector[] scaled = oldScaleWheelVectors(velocity, spin, positions);

        end = System.nanoTime();
        for (int index = 0; index < scaled.length; index++)
        {
            System.out.format("%s: (%.2f, %.2f)\n", names[index], scaled[index].getMag(), scaled[index].getAngle());
        }
        System.out.println("Wheel Calcs: " + ((end - start)/1000) + " ns");
    }

    private static Vector[] oldScaleWheelVectors(Vector velocity, double spin, Vector[] positions)
    {
        Vector[] WheelsUnscaled = new Vector[4];
        Vector[] WheelsScaled = new Vector[4];
        double MaxWantedVeloc = 0;
        double VelocityRatio;

        for (int i = 0; i < 4; i++)
        {
            WheelsUnscaled[i] = new Vector(velocity.getX() - spin * positions[i].getY(),
                    -(velocity.getY() + spin * positions[i].getX()));

            if (WheelsUnscaled[i].getMag() >= MaxWantedVeloc)
            {
                MaxWantedVeloc = WheelsUnscaled[i].getMag();
            }
        }

        VelocityRatio = oldGetVelocityLimit(MaxWantedVeloc);

        for (int i = 0; i < 4; i++)
        {
            WheelsScaled[i] = Vector.NewFromMagAngle(WheelsUnscaled[i].getMag() * VelocityRatio, WheelsUnscaled[i].getAngle());
        }

        return WheelsScaled;
    }

    public static double oldGetVelocityLimit(double MaxWantedVeloc)
    {
        double velocityMaxAvailable = 1;
        double velocityRatio = 1;

        // Determine ratio to scale all wheel velocities by
        velocityRatio = velocityMaxAvailable / MaxWantedVeloc;

        velocityRatio = (velocityRatio > 1) ? 1:velocityRatio;

        return velocityRatio;
    }

    public static void debugFastTrig()
    {
        long start;
        long durMathSin;
        long durSin;
        long durCos;
        long durSin2;
        double sum = 0;

        start = System.nanoTime();
        for (double index = -362.49; index < 362.5; index += .5)
        {
            sum += Math.sin(index);
        }
        System.out.println(sum);
        sum = 0;
        durMathSin = System.nanoTime() - start;

        System.out.println("Fast Sin:");
        start = System.nanoTime();
        for (double index = -362.49; index < 362.5; index += .5)
        {
            sum += FastTrig.sin(index);
        }
        System.out.println(sum);
        sum = 0;
        durSin = System.nanoTime() - start;

        System.out.println("Fast Cos:");
        start = System.nanoTime();
        for (double index = -2.49; index < 362.5; index += .5)
        {
            sum += FastTrig.sin(index);
        }
        System.out.println(sum);
        sum = 0;
        durCos = System.nanoTime() - start;

        System.out.println("Fast Sin 2:");
        start = System.nanoTime();
        for (double index = -362.49; index < 362.5; index += .5)
        {
            sum += FastTrig.sin(index);
        }
        System.out.println(sum);
        sum = 0;
        durSin2 = System.nanoTime() - start;

        System.out.println("Math Sin Duration: " + durMathSin / 1000 + " us");
        System.out.println("Fast Sin Duration: " + durSin / 1000 + " us");
        System.out.println("Fast Cos Duration: " + durCos / 1000 + " us");
        System.out.println("Fast Sin Duration 2: " + durSin2 / 1000 + " us");

        System.out.println(FastTrig.cos(-30));
        System.out.println(FastTrig.cos(330));
    }

    public static void debugLogging()
    {
        double start;
        double end;
        HerdVector v = new HerdVector(1, 45);

        start = System.nanoTime();
        logS.debug("test2", 1);
        logS.info("test:", .33333);
        logS.debug("test3: ", v);
        logS.warning("test3: ", v);
        end = System.nanoTime();
        System.out.println("Test Duration: " + ((end - start)/1000) + " ns");

        System.out.println("Static Loggers:");
        start = System.nanoTime();
        HerdLogger.temp("test2", 1);
        HerdLogger.temp("test:", .33333);
        HerdLogger.temp("test3: ", v);
        HerdLogger.temp("test3: ", v);
        end = System.nanoTime();
        System.out.println("Test Duration: " + ((end - start)/1000) + " ns");

        start = System.nanoTime();
        HerdLogger.temp("test2", 1);
        HerdLogger.temp("test:", .33333);
        HerdLogger.temp("test3: ", v);
        HerdLogger.temp("test3: ", v);
        end = System.nanoTime();
        System.out.println("Test Duration: " + ((end - start)/1000) + " ns");
        System.out.println();

        HerdLogger log = new HerdLogger("WrapperTest");
        log.setLevel(Level.INFO);
        HerdLogger log2 = new HerdLogger(Tests.class);
        log2.setLevel(Level.WARNING);
        HerdLogger log3 = new HerdLogger(Tests.class);

        System.out.println("Dynamic Loggers:");
        start = System.nanoTime();
        log.debug("test2", 1);
        log.info("test:", .33333);
        log.debug("test3: ", v);
        log.warning("test3: ", v);
        end = System.nanoTime();
        System.out.println("Test Duration: " + ((end - start)/1000) + " ns");

        start = System.nanoTime();
        log2.debug("test2", 1);
        log2.info("test:", .33333);
        log2.debug("test3: ", v);
        log2.warning("test3: ", v);
        end = System.nanoTime();
        System.out.println("Test Duration: " + ((end - start)/1000) + " ns");

        start = System.nanoTime();
        log3.debug("test2", 1);
        log3.info("test:", .33333);
        log3.debug("test3: ", v);
        log3.warning("test3: ", v);
        end = System.nanoTime();
        System.out.println("Test Duration: " + ((end - start)/1000) + " ns");
        System.out.println();

        // Temp Level
        HerdLogger logTempLevel = new HerdLogger("Testing");
        logTempLevel.debug("TempLevel:", "Doesn't work");
        logTempLevel.setLevelTempDebug();
        logTempLevel.debug("TempLevel:", "Works");
    }

    public static void printDivider()
    {
        System.out.println("-------------------------------\n");
        System.out.println("-------------------------------");
    }

    private static class Vector
    {
        private double mag;
        private double ang;

        public Vector()
        {
            mag = 0;
            ang = 0;
        }

        public Vector(double[] position)
        {
            setXY(position[0], position[1]);
        }

        public Vector(double x, double y)
        {
            setXY(x, y);
        }

        public static Vector NewFromMagAngle(double mag, double ang)
        {
            Vector r = new Vector();
            r.mag = mag;
            r.ang = ang;
            return r;
        }

        public void setXY(double x, double y)
        {
            mag = Math.sqrt(x * x + y * y);
            ang = Math.toDegrees(Math.atan2(y, x));
        }

        public double getX()
        {
            double realAngle = Math.toRadians(wrapToRange(ang, 0, 360));
            return Math.cos(realAngle) * mag;
        }

        public double getY()
        {
            double realAngle = Math.toRadians(wrapToRange(ang, 0, 360));
            return Math.sin(realAngle) * mag;
        }

        public double getMag()
        {
            return mag;
        }

        public double getAngle()
        {
            return ang;
        }

        public Vector clone()
        {
            return Vector.NewFromMagAngle(mag, ang);
        }

        public static final double wrapToRange(double value, double min, double max)
        {
            return wrapToRange(value - min, max - min) + min;
        }

        public static final double wrapToRange(double value, double max)
        {
            return ((value % max) + max) % max;
        }
    }
}
