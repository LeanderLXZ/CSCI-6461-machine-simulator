package main.java.device;

import main.java.Main;
import main.java.panel.Controller;

import java.util.concurrent.CountDownLatch;

/**
 * @author jalal
 * @since 10/9/19
 * <p>
 * Card Reader device.
 * For taking input from external device.
 * <p>
 */
public class CardReader extends Device {

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.CARD_READER;
    }

    @Override
    public void run() {
        try {
            Main.IO = new CountDownLatch(1);
            Controller.pullFromCard();
            Main.IO.await();
        } catch (InterruptedException e) {

        }
        setBinaryValue(Main.dp.getMemoryAt(7));
    }
}
