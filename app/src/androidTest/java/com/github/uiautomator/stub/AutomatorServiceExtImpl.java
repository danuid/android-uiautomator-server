package com.github.uiautomator.stub;

import android.os.SystemClock;

import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

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
        if (waitExist) {
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
        touchController.touchDown(x, y);
        return touchController.touchUp(x, y);
    }

    @Override
    public boolean fastClickExists(Selector target, long timeout, long preWait) throws UiObjectNotFoundException {
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
        touchController.touchDown(x, y);
        return touchController.touchUp(x, y);
    }

}
