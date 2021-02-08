package com.github.uiautomator.stub;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutomatorServiceExtImpl extends AutomatorServiceImpl implements AutomatorServiceExt {

    @Override
    public boolean fastClickEnabled(Selector target, long timeout, boolean waitExist, Selector skipIfExist)
            throws UiObjectNotFoundException {
        // 检查如何已经存在，就跳过点击，返回false
        if (skipIfExist != null) {
            UiObject2 skipIfExistObj = device.findObject(skipIfExist.toBySelector());
            if (skipIfExistObj != null) {
                return false;
            }
        }

        long start = SystemClock.uptimeMillis();
        BySelector bySelector = target.toBySelector();
        UiObject2 targetObj = device.findObject(bySelector);
        if (waitExist && targetObj == null) {
            BySelector skipSelector = null;
            if (skipIfExist != null) {
                // 检查是否要skip的selector
                skipSelector = skipIfExist.toBySelector();
            }
            // 等待目标exist
            while (targetObj == null) {
                if (SystemClock.uptimeMillis() > start + timeout) {
                    break;
                }
                if (skipSelector != null && device.findObject(skipSelector) != null) {
                    // 检查是否要skip
                    return false;
                }
                SystemClock.sleep(1); // normally 100ms for click
                targetObj = device.findObject(bySelector);
            }
        }
        if (targetObj == null) {
            throw new UiObjectNotFoundException("UiObject " + target.toUiSelector().toString() + " not found!");
        }

        // 等待目标enabled状态
        while (!targetObj.isEnabled()) {
            if (SystemClock.uptimeMillis() > start + timeout) {
                throw new UiObjectNotFoundException("UiObject " + target.toUiSelector().toString() + " not found!");
            }
            SystemClock.sleep(1); // normally 100ms for click
        }

        android.graphics.Rect rect = targetObj.getVisibleBounds();
        int x = (rect.left + rect.right) / 2;
        int y = (rect.top + rect.bottom) / 2;
        return injectClickEvent(x, y);
//        touchController.touchDown(x, y);
//        return touchController.touchUp(x, y);
    }

    private boolean injectClickEvent(float x, float y) {
        //A MotionEvent is a type of InputEvent.
        //The event time must be the current uptime.
        final long eventTime = SystemClock.uptimeMillis();

        //A typical click event triggered by a user click on the touchscreen creates two MotionEvents,
        //first one with the action KeyEvent.ACTION_DOWN and the 2nd with the action KeyEvent.ACTION_UP
        MotionEvent motionDown = MotionEvent.obtain(eventTime, eventTime, KeyEvent.ACTION_DOWN,
                x, y, 0);
        //We must set the source of the MotionEvent or the click doesn't work.
        motionDown.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        uiAutomation.injectInputEvent(motionDown, true);
        MotionEvent motionUp = MotionEvent.obtain(eventTime, eventTime, KeyEvent.ACTION_UP,
                x, y, 0);
        motionUp.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        boolean result = uiAutomation.injectInputEvent(motionUp, true);
        //Recycle our events back to the system pool.
        motionUp.recycle();
        motionDown.recycle();
        return result;
    }


    private boolean fastClickExistsOrGone(boolean exist,
                                          BySelector clickTarget, BySelector waitTarget,
                                          long timeout, long preWait) throws UiObjectNotFoundException {
        if (preWait > 0) {
            SystemClock.sleep(preWait);
        }

        long start = SystemClock.uptimeMillis();
        UiObject2 waitObj = device.findObject(waitTarget);
        while ((exist && waitObj == null) || (!exist && waitObj != null)) {
            if (SystemClock.uptimeMillis() > start + timeout) {
                throw new UiObjectNotFoundException("UiObject " + waitTarget.toString() + " not found!");
            }
            SystemClock.sleep(1); // normally 100ms for click
            waitObj = device.findObject(waitTarget);
        }

        android.graphics.Rect rect = device.findObject(clickTarget).getVisibleBounds();
        int x = (rect.left + rect.right) / 2;
        int y = (rect.top + rect.bottom) / 2;
        return injectClickEvent(x, y);
    }

    @Override
    public boolean fastClickExists(Selector target, long timeout, long preWait) throws UiObjectNotFoundException {
        // TODO merge below with above method later
        if (preWait > 0) {
            SystemClock.sleep(preWait);
        }

        long start = SystemClock.uptimeMillis();
        BySelector bySelector = target.toBySelector();
        UiObject2 targetObj = device.findObject(bySelector);
        //UiObject targetObj = device.findObject(obj.toUiSelector());
        while (targetObj == null) {
            if (SystemClock.uptimeMillis() > start + timeout) {
                throw new UiObjectNotFoundException("UiObject " + target.toUiSelector().toString() + " not found!");
            }
            SystemClock.sleep(1); // normally 100ms for click
            targetObj = device.findObject(bySelector);
        }

        android.graphics.Rect rect = targetObj.getVisibleBounds();
        int x = (rect.left + rect.right) / 2;
        int y = (rect.top + rect.bottom) / 2;
        return injectClickEvent(x, y);
    }

    @Override
    public boolean fastClickGone(Selector clickTarget, Selector waitTarget, long timeout, long preWait)
            throws UiObjectNotFoundException {
        BySelector clickSelector = clickTarget.toBySelector();
        BySelector waitSelector = waitTarget.toBySelector();
        return fastClickExistsOrGone(false, clickSelector, waitSelector, timeout, preWait);
    }

    @Override
    public boolean fastClickPos(List<Integer> posList) {
        int len = posList.size(), idx = 0;
        boolean result = false;

        while (len - idx >= 2) {
            int x = posList.get(idx);
            int y = posList.get(idx + 1);
            result = injectClickEvent(x, y);
            idx += 2;
        }
        if (len - idx != 0) {
            return false;
        }
        return result;
    }

}
