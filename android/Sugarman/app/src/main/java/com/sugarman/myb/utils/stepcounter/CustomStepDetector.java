package com.sugarman.myb.utils.stepcounter;

import android.util.Log;
import com.sugarman.myb.listeners.StepListener;

public class CustomStepDetector {

  private static final int ACCEL_RING_SIZE = 50;
  private static final int VEL_RING_SIZE = 10;

  private final int SECONDS_TO_START_WALKING = 2;
  private final int SECONDS_TO_STOP_WALING = 10;
  private final int STEPS_NEEDED_TO_START_WALKING = 5;

  // change this threshold according to your sensitivity preferences
  private static final float STEP_THRESHOLD = 20f;

  private static final int STEP_DELAY_NS = 350000000;

  int currentSteps = 0;

  private int accelRingCounter = 0;
  private float[] accelRingX = new float[ACCEL_RING_SIZE];
  private float[] accelRingY = new float[ACCEL_RING_SIZE];
  private float[] accelRingZ = new float[ACCEL_RING_SIZE];
  private int velRingCounter = 0;
  private float[] velRing = new float[VEL_RING_SIZE];
  private long lastStepTimeNs = 0;
  private float oldVelocityEstimate = 0;

  Thread stepChecker;

  private StepListener listener;

  public void registerListener(StepListener listener) {
    this.listener = listener;
  }

  public void unregisterListener() {
    listener = null;
  }

  public void updateAccel(final long timeNs, float x, float y, float z) {
    float[] currentAccel = new float[3];
    currentAccel[0] = x;
    currentAccel[1] = y;
    currentAccel[2] = z;

    // First step is to update our guess of where the global z vector is.
    accelRingCounter++;
    accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0];
    accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1];
    accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2];

    float[] worldZ = new float[3];
    worldZ[0] = SensorFilter.sum(accelRingX) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
    worldZ[1] = SensorFilter.sum(accelRingY) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
    worldZ[2] = SensorFilter.sum(accelRingZ) / Math.min(accelRingCounter, ACCEL_RING_SIZE);

    float normalization_factor = SensorFilter.norm(worldZ);

    worldZ[0] = worldZ[0] / normalization_factor;
    worldZ[1] = worldZ[1] / normalization_factor;
    worldZ[2] = worldZ[2] / normalization_factor;

    float currentZ = SensorFilter.dot(worldZ, currentAccel) - normalization_factor;
    velRingCounter++;
    velRing[velRingCounter % VEL_RING_SIZE] = currentZ;

    float velocityEstimate = SensorFilter.sum(velRing);

    if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD && (timeNs
        - lastStepTimeNs > STEP_DELAY_NS)) {
      Log.d("CUSTOM STEP DETECTOR", " Started if");
      if (stepChecker != null && stepChecker.isAlive()) stepChecker.interrupt();
      stepChecker = new Thread(new Runnable() {
        @Override public void run() {
          addStepToCounter();
          if (currentSteps < STEPS_NEEDED_TO_START_WALKING) {
            try {
              System.out.println("Started sleeping at " + getSteps() + " steps");
              Thread.sleep(SECONDS_TO_START_WALKING * 1000);
              setStepsToZero();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          } else if (getSteps() == STEPS_NEEDED_TO_START_WALKING) {
            try {
              for (int i = 0; i < STEPS_NEEDED_TO_START_WALKING; i++)
                listener.step(timeNs);
              Thread.sleep(SECONDS_TO_STOP_WALING * 1000);
              setStepsToZero();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          } else {
            System.out.println(getSteps() + " IS HOW MANY STEPS I HAVE");
            try {
              listener.step(timeNs);
              Thread.sleep(10000);
              setStepsToZero();
              System.out.println("I'm not walking anymore");
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      });

      stepChecker.start();

      lastStepTimeNs = timeNs;
    }
    oldVelocityEstimate = velocityEstimate;
  }

  void addStepToCounter() {
    synchronized (this) {
      currentSteps++;
    }
  }

  synchronized void setStepsToZero() {
    currentSteps = 0;
  }

  synchronized int getSteps() {
    return currentSteps;
  }
}
