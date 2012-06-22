package org.wintrisstech.erik.iaroc;

import android.os.SystemClock;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.irobot.ioio.IRobotCreateScript;
import org.wintrisstech.sensors.UltraSonicSensors;

public class Ferrari extends IRobotCreateAdapter implements Runnable {

    private static final String TAG = "Ferrari";
    private int irCode = 255;
    int none = 255;
    int red = 248;
    int green = 244;
    int reserved = 240;
    int forceField = 242;
    int redAndGreen = 252;
    int redAndForceField = 250;
    int greenAndForceField = 246;
    int RedAndGreenAndForceField = 254;
    int currentDistance = 0;
    int someDistance = 0;
    int someDistance2 = 0;
    int someDistance3 = 0;
    int hugFactor = 4;
    double version = 3.2;
    boolean rylexIsAwesome = true;
    boolean north = true;
    boolean south = true;
    boolean east = true;
    boolean west = true;
    private final UltraSonicSensors sonar;
    private final Dashboard dashboard;
    /*
     * The maze can be thought of as a grid of quadratic cells, separated by
     * zero-width walls. The cell width includes half a pipe diameter on each
     * side, i.e the cell edges pass through the center of surrounding pipes.
     * <p> Row numbers increase northward, and column numbers increase eastward.
     * <p> Positions and direction use a reference system that has its origin at
     * the west-most, south-most corner of the maze. The x-axis is oriented
     * eastward; the y-axis is oriented northward. The unit is 1 mm. <p> What
     * the Ferrari knows about the maze is:
     */
    private final static int NUM_ROWS = 12;
    private final static int NUM_COLUMNS = 4;
    private final static int CELL_WIDTH = 712;
    /*
     * State variables:
     */
    private int speed = 200; // The normal speed of the Ferrari when going straight
    private int backwardsSpeed = 250;
    private int spinSpeed = 200;// The normal speed of the Ferrari when going straight
    // The row and column number of the current cell. 
    private int row;
    private int column;
    private boolean running = true;
    private final static int SECOND = 1000; // number of millis in a second
    private int mode = 0;
    private int forwardDistance = 0;
    private int defaultSpeed = 200;
    private int defaultBackwardDistance = 100;
    private int backwardDistance = defaultBackwardDistance;
    private boolean shouldTurn = false;
    private int currentAngle = 0;
    private boolean adjustingRight = false;
    private boolean adjustingLeft = false;
    private int totalAngle;

    /**
     * Constructs a Ferrari, an amazing machine!
     *
     * @param ioio the IOIO instance that the Ferrari can use to communicate
     * with other peripherals such as sensors
     * @param create an implementation of an iRobot
     * @param dashboard the Dashboard instance that is connected to the Ferrari
     * @throws ConnectionLostException
     */
    public Ferrari(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard) throws ConnectionLostException {
        super(create);
        sonar = new UltraSonicSensors(ioio);
        if (sonar == null) {
            dashboard.log("sonar not instantiated");
        } else {
            dashboard.log("sonar OK");
        }
        this.dashboard = dashboard;
        sonar.setDashboard(dashboard);
    }

    /**
     * Main method that gets the Ferrari running.
     *
     */
    public void run() {
        dashboard.speak("Startup " + version + ". Hug factor " + hugFactor + "Climate Status Normal. Speed Factor is " + speed + ". Take that! Oh! By the way, that's times two. " + speed + " times two. Reality Distortion Field: Activated. wawawawawawawawawawawawawawawa. Ultrasonic sensors: Activated.");
        dashboard.log("Startup " + version + ". Hug factor " + hugFactor + "Climate Status Normal. Speed Factor is " + speed + ". Take that! Oh! By the way, that's times two. " + speed + " times two. Reality Distortion Field: Activated. wawawawawawawawawawawawawawawa. Ultrasonic sensors: Activated.");
        try {
            int leftWheelSpeed = speed;
            int rightWheelSpeed = speed;
            readSensors(SENSORS_GROUP_ID6);
            sonar.readUltrasonicSensors();
            readSensors(SENSORS_GROUP_ID6);
            sonar.readUltrasonicSensors();
//            SystemClock.sleep(18000);
//            spinLeft(180, speed);
//            SystemClock.sleep(5000);
//            spinLeft(180, speed);
            float leftDistance = 0;
            float rightDistance = 0;
            float frontDistance = 0;
            float oldLeftDistance = 0;
            float oldRightDistance = 0;
            float oldFrontDistance = 0;
            int adjustTurn = 25;

            float convertToDistanceFactor = 1.6667f;

            
            while (rylexIsAwesome == true) {
                //SystemClock.sleep(1000);
//                dashboard.log("loop");
                //5cm = 41
                driveDirect(rightWheelSpeed, leftWheelSpeed);
                //goForward(speed);
                readSensors(SENSORS_GROUP_ID6);
                someDistance += getDistance();
                someDistance2 += getDistance();
                someDistance3 += getDistance();
                totalAngle += getAngle();

                if (isBumpLeft() == true && isBumpRight() == false) {
                    spinRight(3, speed);
                    dashboard.log("Using bump backups.");
                    dashboard.speak("Using bump backups.");
                    continue;
                } else if (isBumpRight() == true && isBumpLeft() == false) {
                    spinLeft(3, speed);
                    dashboard.log("Using bump backups.");
                    dashboard.speak("Using bump backups.");
                    continue;
                }

                dashboard.log("LOOP********************************");
                //if (someDistance >= 1) {
                someDistance = 0;
                sonar.readUltrasonicSensors();
                //dashboard.log("IT WAS ABLE TO READ THE SONAR********************************");
                //float convertToDistanceFactor = 3.636f;
                
                oldLeftDistance = leftDistance;
                oldRightDistance = rightDistance;
                oldFrontDistance = frontDistance;

                leftDistance = sonar.getLeftDistance() * convertToDistanceFactor;
                rightDistance = sonar.getRightDistance() * convertToDistanceFactor;
                frontDistance = sonar.getFrontDistance() * convertToDistanceFactor;
                dashboard.log(leftDistance + " / " + rightDistance + " / " + frontDistance);
                
                if (leftDistance > 1000) {
                    leftDistance = oldLeftDistance;
                }
                
                if (rightDistance > 1000) {
                    rightDistance = oldRightDistance;
                }
                
                //}
                //dashboard.log("It was able to read the UltrasonicSensors********************");
                //dashboard.log("IT GOT ALMOST TO THE END OF THE LOOP - A even less Sorta********************");
//                if (someDistance2 >= 20 && (leftDistance - rightDistance >= 10 || leftDistance - rightDistance <= -10)) {
//                    someDistance2 = 0;
//                    dashboard.log("Total angle = " + totalAngle);
//                    if (leftDistance > rightDistance && totalAngle < (leftDistance - rightDistance) / 5) {
//                        int spinAngle = (int)(((leftDistance - rightDistance) / 5) - totalAngle);
//                        spinLeft(spinAngle, speed);
//                        dashboard.log("Adjusting left, angle = " + spinAngle);
//                        continue;
//                    } else if (leftDistance > rightDistance && totalAngle > (leftDistance - rightDistance) / 5) {
//                        int spinAngle = (int)(totalAngle - ((leftDistance- rightDistance) / 5));
//                        spinRight(spinAngle, speed);
//                        dashboard.log("Leveling out from left, angle = " + spinAngle);
//                        continue;
//                    } else if (leftDistance < rightDistance && totalAngle > (leftDistance - rightDistance) / 5) {
//                        int spinAngle = (int)(totalAngle - ((leftDistance - rightDistance) / 5));
//                        spinRight(spinAngle, speed);
//                        dashboard.log("Adjusting right, angle = " + spinAngle);
//                        continue;
//                    } else if (leftDistance < rightDistance && totalAngle < (leftDistance - rightDistance) / 5) {
//                        int spinAngle = (int)(((leftDistance - rightDistance) / 5) - totalAngle);
//                        spinLeft(spinAngle, speed);
//                        dashboard.log("Leveling out from right, " + spinAngle);
//                        continue;
//                    }
//                    someDistance = 0;
//                }
               
                if (leftDistance < 120) {
                    //spinRight(1, spinSpeed);
                    if (adjustingRight == true) {
                        if (someDistance3 > 10 && adjustTurn > 1) {
                            adjustTurn -= 1;
                            someDistance3 = 0;
                        }
                    } else {
                        adjustTurn = 25;
                    }
                    adjustingRight = true;
                    adjustingLeft = false;
                    leftWheelSpeed = speed;
                    rightWheelSpeed = speed - adjustTurn;
                    //turnRight(10, speed);
                    //spinLeft(10, speed);
                    dashboard.log("Adjusting right.");
                    //dashboard.log("IT GOT ALMOST TO THE END OF THE LOOP - A little less Sorta********************");
                    continue;
                } else if (leftDistance > 140) {
                    //spinLeft(1, spinSpeed);
                    if (adjustingLeft == true) {
                        if (someDistance3 > 10 && adjustTurn > 1) {
                            adjustTurn -= 1;
                            someDistance3 = 0;
                        }
                    } else {
                        adjustTurn = 25;
                    }
                    leftWheelSpeed = speed - adjustTurn;
                    rightWheelSpeed = speed;
                    adjustingRight = false;
                    adjustingLeft = true;
                    //turnLeft(10, speed);
                    //spinRight(10, speed);
                    dashboard.log("Done adjusting right.");
                    //dashboard.log("IT GOT ALMOST TO THE END OF THE LOOP - Sorta********************");
                    continue;
                } else {
                    leftWheelSpeed = speed;
                    //dashboard.log("IT GOT ALMOST TO THE END OF THE LOOP********************");
                    rightWheelSpeed = speed;
                }
                /*
                 * else if (sonar.getLeftDistance() >= 140 && adjustingLeft ==
                 * false) { spinLeft(1, spinSpeed); adjustingLeft = true;
                 * dashboard.log("Adjusting left."); continue; } else if
                 * (sonar.getLeftDistance() <= 130 && adjustingLeft == true) {
                 * spinRight(1, spinSpeed); adjustingLeft = false;
                 * dashboard.log("Done adjusting left."); continue; }
                 */
                //dashboard.log("IT GOT TO THE END OF THE LOOP********************");
            }
            dashboard.speak("I'm an idiot.");

//            readSensors(SENSORS_GROUP_ID6);
//            while (true)
//            {
//                readSensors(SENSORS_GROUP_ID6);
//                someDistance += getDistance();
//                boolean bumpLeft = isBumpLeft();
//                boolean bumpRight = isBumpRight();
//                if (someDistance >= 50)
//                {
//                    someDistance = 0;
//                    shouldTurn = true;
//                }
//                if (currentAngle >= 17) 
//                {
//                    shouldTurn = false;
//                }
//                if (shouldTurn == true) 
//                {
//                    readSensors(SENSORS_GROUP_ID6);
//                    currentAngle += getAngle();
//                    driveDirect(speed, -speed / 16);
//                }
//                readSensors(SENSORS_GROUP_ID6);
//                someDistance += getDistance();
//                if ((bumpLeft == true && bumpRight == false) || (isBumpLeft() == true && isBumpRight() == false))
//                {
//                    goBackwards(1, backwardsSpeed);
//                    spinRight(10, spinSpeed);
//                } else if (bumpLeft == true && bumpRight == true || isBumpLeft() == true && isBumpRight() == true)
//                {
//                    goBackwards(1, backwardsSpeed);
//                    spinRight(60, spinSpeed);
//                } else if (bumpLeft == false && bumpRight == true || isBumpLeft() == false && isBumpRight() == true)
//                {
//                    goBackwards(1, backwardsSpeed);
//                    spinLeft(10, spinSpeed);
//                } else
//                {
//                    goForward(speed);
//                }
//            }
        } catch (Exception ex) {
        }
    }
    
    
//    private readUltraSonicSensors()
//    {
//        sonar.readUltrasonicSensors();
//        float leftDistance1 = sonar.getLeftDistance() * convertToDistanceFactor;
//        float rightDistance1 = sonar.getRightDistance() * convertToDistanceFactor;
//        float frontDistance1 = sonar.getFrontDistance() * convertToDistanceFactor;
//        sonar.readUltrasonicSensors();
//        float leftDistance2 = sonar.getLeftDistance() * convertToDistanceFactor;
//        float rightDistance2 = sonar.getRightDistance() * convertToDistanceFactor;
//        float frontDistance2 = sonar.getFrontDistance() * convertToDistanceFactor;
//        sonar.readUltrasonicSensors();
//        float leftDistance3 = sonar.getLeftDistance() * convertToDistanceFactor;
//        float rightDistance3 = sonar.getRightDistance() * convertToDistanceFactor;
//        float frontDistance3 = sonar.getFrontDistance() * convertToDistanceFactor;
//
//    }

    /**
     * To run this test, place the Ferrari in a cell surrounded by 4 walls. <p>
     * Note: The sensors draw power from the Create's battery. Make sure it is
     * charged.
     */
    private void testUltraSonicSensors() {
        dashboard.log("Starting ultrasonic test.");
        long endTime = System.currentTimeMillis() + 20 * SECOND;
        while (System.currentTimeMillis() < endTime) {
            try {
                sonar.readUltrasonicSensors();
            } catch (ConnectionLostException ex) {
                //TODO
            } catch (InterruptedException ex) {
                //TODO
            }
            SystemClock.sleep(500);
        }
        dashboard.log("Ultrasonic test ended.");
    }

    /**
     * Tests the rotation of the Ferrari.
     */
    private void testRotation() {
        dashboard.log("Testing rotation");
        try {
            turnAndGo(10, 0);
            SystemClock.sleep(500);
            turnAndGo(80, 0);
            SystemClock.sleep(80);
            turnAndGo(-90, 0);
            SystemClock.sleep(80);
            turnAndGo(180, 0);
            SystemClock.sleep(80);
            turnAndGo(-90, 0);
            SystemClock.sleep(80);
            turnAndGo(-180, 0);
            SystemClock.sleep(80);
            turnAndGo(180, 0);
            SystemClock.sleep(80);
        } catch (ConnectionLostException ex) {
        } catch (InterruptedException ex) {
        }

    }

    /**
     * Turns in place and then goes forward.
     *
     * @param angle the angle in degrees that the Ferrari shall turn. Negative
     * values makes clockwise turns.
     * @param distance the distance in mm that the Ferrari shall run forward.
     * Must be positive.
     */
    private void turnAndGo(int angle, int distance)
            throws ConnectionLostException, InterruptedException {
        IRobotCreateScript script = new IRobotCreateScript();
        /*
         * The Create overshoots by approx. 3 degrees depending on the floor
         * surface. Note: This is speed sensitive.
         */
        // TODO: Further tweaks to make the Ferrari make more precise turns.  
        if (angle < 0) {
            angle = Math.min(0, angle + 3);
        }
        if (angle > 0) {
            angle = Math.max(0, angle - 3);
        }
        if (angle != 0) {
            script.turnInPlace(100, angle < 0); // Do not change speed!
            script.waitAngle(angle);
        }
        if (distance > 0) {
            script.driveStraight(speed);
            script.waitDistance(distance);
        }
        if (angle != 0 || distance > 0) {
            script.stop();
            playScript(script.getBytes(), false);
            // delay return from this method until script has finished executing
        }
    }

    /**
     * Closes down all the connections of the Ferrari, including the connection
     * to the iRobot Create and the connections to all the sensors.
     */
    public void shutDown() {
        closeConnection(); // close the connection to the Create
        sonar.closeConnection();
    }

    //// Methods made public for the purpose of the Dashboard ////
    /**
     * Gets the left distance to the wall using the left ultrasonic sensor
     *
     * @return the left distance
     */
    public int getLeftDistance() {
        return sonar.getLeftDistance();
    }

    /**
     * Gets the front distance to the wall using the front ultrasonic sensor
     *
     * @return the front distance
     */
    public int getFrontDistance() {
        return sonar.getFrontDistance();
    }

    /**
     * Gets the right distance to the wall using the right ultrasonic sensor
     *
     * @return the right distance
     */
    public int getRightDistance() {
        return sonar.getRightDistance();
    }

    /**
     * Checks if the Ferrari is running
     *
     * @return true if the Ferrari is running
     */
    public synchronized boolean isRunning() {
        return running;
    }

    private synchronized void setRunning(boolean b) {
        running = false;
    }

    public boolean closeToBeacon() {
        if (getInfraredByte() == 244 || getInfraredByte() == 248 || getInfraredByte() == 252)//Red, green
        {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ***********************************************************************
     * Rylex AwesomeApi
     * ***********************************************************************
     */
    public void spinScan() throws Exception {
        dashboard.log("SPIN SCAN************************");
        int currentAngle = 0;
        readSensors(SENSORS_GROUP_ID6);
        getInfraredByte();
        irCode = getInfraredByte();
        while (currentAngle <= 360 && irCode == none) {
            readSensors(SENSORS_GROUP_ID6);
            currentAngle += getAngle();
            irCode = getInfraredByte();
            driveDirect(speed, -speed);
            if (irCode == red) {
                dashboard.speak("Red");
                mode = 4;
            } else if (irCode == green) {
                dashboard.speak("Green");
                mode = 5;
            } else if (irCode == redAndGreen) {
                dashboard.speak("Red and Green");
                mode = 0;
            } else if (irCode == redAndForceField) {
                dashboard.speak("Red and Force Field");
                mode = 6;
            } else if (irCode == greenAndForceField) {
                dashboard.speak("Green and Force Field");
                mode = 7;
            } else if (irCode == RedAndGreenAndForceField) {
                dashboard.speak("Red, Green, and Force Field");
                mode = 0;
            } else {
                mode = 0;
            }
        }
    }

    public void goForward(int speed) throws Exception {
//        checkingBumps(500);
        driveDirect(speed, speed);
//        checkingBumps(500);
    }

    public void goBackwards(int distance, int speed) throws Exception {
        int currentDistance = 0;
        readSensors(SENSORS_GROUP_ID6);
        while (currentDistance > (-distance)) {
            driveDirect(-speed, -speed);
            readSensors(SENSORS_GROUP_ID6);
            currentDistance += getDistance();
        }
    }

//    public void checkingBumps(int speed) throws Exception
//    {
//        bumpRight(speed);
//        bumpLeft(speed);
//        bumpFront(speed);
//    }
    public void backUp(int speed) throws Exception//Bumped right
    {
        driveDirect(-speed, -speed);
    }
//    public void backUp(int speed) throws Exception//Bumped front
//    {
//        int r = new Random().nextInt(2);
//        if (r == 0)
//        {
//            backLeft(speed);
//        } else
//        {
//            backRight(speed);
//        }
//    }

    public void turnLeft(int angle, int speed) throws Exception {
        int currentAngle = 0;
        readSensors(SENSORS_GROUP_ID6);
        while (currentAngle < angle) {
            readSensors(SENSORS_GROUP_ID6);
            currentAngle += getAngle();
            driveDirect(speed, speed - (speed / 10));
        }
    }

//    public void sensors(int angle, int speed) throws Exception {
//        readSensors(SENSORS_GROUP_ID6);
//        readSensors(SENSORS_GROUP_ID6);
//    }

    public void spinLeft(int angle, int speed) throws Exception {
        int currentAngle = 0;
        readSensors(SENSORS_GROUP_ID6);
        while (currentAngle < angle) {
            readSensors(SENSORS_GROUP_ID6);
            currentAngle += getAngle();
            driveDirect(speed, -speed);
        }
    }

    public void turnRight(int angle, int speed) throws Exception {
        int currentAngle = 0;
        readSensors(SENSORS_GROUP_ID6);
        while (currentAngle > (-angle)) {
            readSensors(SENSORS_GROUP_ID6);
            currentAngle += getAngle();
            driveDirect(speed - (speed / 10), speed);
        }
    }

    public void spinRight(int angle, int speed) throws Exception {
        int currentAngle = 0;
        readSensors(SENSORS_GROUP_ID6);
        while (currentAngle > (-angle)) {
            readSensors(SENSORS_GROUP_ID6);
            currentAngle += getAngle();
            driveDirect(-speed, speed);
        }
    }

//    public void bumpFront(int speed) throws Exception
//    {
//        readSensors(SENSORS_GROUP_ID6);
//        if (isBumpRight() && isBumpLeft())
//        {
//            System.out.println("BF");
//            //backUp(speed);
//            //goNorth();
//        }
//    }
//    public void bumpLeft(int speed) throws Exception
//    {
//        readSensors(SENSORS_GROUP_ID6);
//        if (isBumpLeft())
//        {
//            System.out.println("BL");
//            backRight(speed);
//            //goNorth();
//        }
//    }
//    public void bumpRight(int speed) throws Exception
//    {
//        readSensors(SENSORS_GROUP_ID6);
//        if (isBumpRight())
//        {
//            System.out.println("BR");
//            backUp(speed);
//            //goNorth();
//        }
//    }
    void stop() {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    void initialize() throws ConnectionLostException {
        dashboard.log("===========Start===========");
        //readSensors(SENSORS_GROUP_ID6);//Resets all counters in the Create to 0.
        //driveDirect(speed, speed);
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    void loop() {
        // throw new UnsupportedOperationException("Not yet implemented");
    }
}
