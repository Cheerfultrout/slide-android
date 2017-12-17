package com.j03.mobileinput.canvas;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.j03.mobileinput.R;

import java.util.Timer;
import java.util.TimerTask;

import common.SystemInfo;
import common.toast.Toaster;
import connection.ConnectionManager;
import gesture.binding.actions.Trigger;
import layout.enums.PositioningMode;
import settings.AppSettings;
import settings.enums.ConnectionMode;

@TargetApi(19)
@SuppressLint("InlinedApi")
public class CanvasActivity
    extends ActionBarActivity
{
    //--> move this to a field of appsettings, and access via appsettings.getInstance().getCanvas()
    public static Activity mainActivityContext;
    private static Runnable mHideRunnable;

    private boolean waiting = false;
    private Timer countdown;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitNetwork()
                .build());
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        AppSettings.getInstanceSetContext(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_keyboard:
                InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(
                    InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY);
                return true;

            case R.id.action_cut:
                ConnectionManager.send(Trigger.CUT);
                Toaster.getInstance().showToast("Cut", getApplicationContext());
                return true;

            case R.id.action_copy:
                ConnectionManager.send(Trigger.COPY);
                Toaster.getInstance().showToast("Copy", getApplicationContext());
                return true;

            case R.id.action_paste:
                ConnectionManager.send(Trigger.PASTE);
                Toaster.getInstance().showToast("Paste", getApplicationContext());
                return true;

            case R.id.action_hide:
                // Hide actionbar when not starting from the top
                if (AppSettings.getSystemSettings().getKitKat())
                {
                    hideUi(CanvasActivity.mainActivityContext);
                }
                return true;

            case R.id.show_settings:
                AppSettings.getSettingsElements().getPrefConnectionStatus().setEnabled(true);
                AppSettings.getSettingsElements().getPrefConnectionStatus().setTitle("Tap to resume");
                if (AppSettings.getConnectionManager().getConnectionMode() == ConnectionMode.WIFI)
                {
                    final SystemInfo ipv4 = AppSettings.getSystemSettings().getSystemInfo();
                    AppSettings.getSettingsElements().getPrefConnectionStatus().setSummary(
                        "Connected via WiFi (" + ipv4.ipv4Address().get(0).getHostAddress() + ")");
                } else if (AppSettings.getConnectionManager().getConnectionMode() == ConnectionMode.USB)
                {
                    AppSettings.getSettingsElements().getPrefConnectionStatus().setSummary(
                        "Connected via USB");
                }
                finish();
                return true;

            case R.id.action_close:
                closeConnection();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitNetwork()
                .build());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        CanvasActivity.mainActivityContext = this;
        /*
        Controller c = new Controller(this);*/

        if (AppSettings.getSettingsElements().getPositioningMode() == PositioningMode.ABSOLUTE)
        {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        if (getSupportActionBar() != null)
        {
            if (AppSettings.getSettingsElements().getActionBarEnabled())
            {
                getSupportActionBar().show();
            } else
            {
                getSupportActionBar().hide();
            }
        }

        countdown = new Timer();

        final CanvasView dv = new CanvasView(this);
        setContentView(dv); //show the drawing view
    }

    @SuppressLint("InlinedApi")
    public static void hideUi(Activity a)
    {
        a.getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
        mHideRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                hideUi(CanvasActivity.mainActivityContext);
            }
        };
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) &&
            (event.getRepeatCount() == 0))
        {
            if (!waiting)
            {
                Toaster.getInstance().showToast("Press back again to close the connection",
                    getApplicationContext());

                waiting = true;
                countdown.schedule(
                    new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            waiting = false;
                        }
                    }, 2000);
            } else
            {
                closeConnection();
            }
            return true;
        } else
        {
            // TODO: debug keyboard only sending '/'
            if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT
                || keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT)
            {
                //changed from hardcoded "100" to "(short) keyCode"
                final short[] KEYBOARD = {10000 + 9, (short) keyCode};
                ConnectionManager.send(KEYBOARD);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(final int keyCode, final KeyEvent event)
    {
        if (keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_MENU)
        {
            final short[] KEYBOARD = {10000 + 9, (short) keyCode};
            ConnectionManager.send(KEYBOARD);
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    private void closeConnection()
    {
        if (AppSettings.getConnectionManager().getConnectionMode() == ConnectionMode.WIFI)
        {
            //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            AppSettings.getConnectionManager().getNetworkConnectionManager().close();
        } else if (AppSettings.getConnectionManager().getConnectionMode() == ConnectionMode.USB)
        {
            //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            AppSettings.getConnectionManager().getUsbConnectionManager().close();
        }

        AppSettings.getConnectionManager().reinitializeServers();

        finish();
    }

    public Context getContext()
    {
        return this;
    }
}