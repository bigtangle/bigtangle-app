package net.bigtangle.wallet;

import android.app.Instrumentation;
import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import android.view.KeyEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    public Instrumentation instrumentation;
    public UiDevice uiDevice;

    @Before
    public void setUp() {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        uiDevice = UiDevice.getInstance();
    }

    @Test
    public void test() throws InterruptedException {
        uiDevice.pressHome();

        uiDevice.click(70, 140);
        Thread.sleep(1000);

        int h = uiDevice.getDisplayHeight();
        int w = uiDevice.getDisplayWidth();

        System.out.println("w : " + w);
        System.out.println("h : " + h);

        uiDevice.pressKeyCode(KeyEvent.KEYCODE_T);
        uiDevice.pressKeyCode(KeyEvent.KEYCODE_E);
        uiDevice.pressKeyCode(KeyEvent.KEYCODE_S);
        uiDevice.pressKeyCode(KeyEvent.KEYCODE_T);

        uiDevice.pressKeyCode(KeyEvent.KEYCODE_1);
        uiDevice.pressKeyCode(KeyEvent.KEYCODE_2);
        uiDevice.pressKeyCode(KeyEvent.KEYCODE_3);
        uiDevice.pressKeyCode(KeyEvent.KEYCODE_4);

        System.out.println("test end");
    }

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("net.bigtangle.wallet", appContext.getPackageName());
    }
}
