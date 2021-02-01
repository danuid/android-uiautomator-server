package com.github.uiautomator.stub;

import androidx.test.uiautomator.UiObjectNotFoundException;

public interface AutomatorServiceExt extends AutomatorService {
    /**
     * 在目标为enabled状态的时候马上点击
     *
     * @param target      点击目标
     * @param timeout     等待超时
     * @param waitExist   是否等待exist
     * @param skipIfExist 如果这个元素已经存在，或者出现时间比目标早，则不进行点击，并返回false
     * @return 是否有进行点击
     * @throws UiObjectNotFoundException 没有找到目标异常
     */
    boolean fastClickEnabled(Selector target, long timeout, boolean waitExist, Selector skipIfExist)
            throws UiObjectNotFoundException;

    /**
     * 快速查找元素并点击
     *
     * @param obj     点击目标
     * @param timeout 查找元素超时时长,in milliseconds
     * @param preWait 查找元素前等待milliseconds时长
     * @return
     * @throws UiObjectNotFoundException
     */
    boolean fastClickExists(Selector obj, long timeout, long preWait) throws UiObjectNotFoundException;
}
