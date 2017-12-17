package com.j03.mobileinput;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;

import com.samsung.spen.lib.input.SPenEventLibrary;

import java.util.Locale;

import Common.Enums.AppActivity;
import Connection.Network.UDP.Broadcast;
import Settings.AppSettings;
import Settings.Enums.ConnectionMode;
import Settings.SettingsElements;

public class SettingsActivity
    extends PreferenceActivity
{
    private static Activity currentActivity;
    private static SPenEventLibrary spenEvent;
    private AlertDialog.Builder prompt;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        currentActivity = this;

        AppSettings.getInstanceSetContext(this);

        AppSettings.getSystemSettings().getSystemInfo().setVersion();

        final boolean firstRun = AppSettings.getSystemSettings().getFirstRun(this);

        AppSettings.setSettingsElements(
            new SettingsElements(this, firstRun));

        if (firstRun)
        {
            AppSettings.getActivitySettings().loadActivity(
                this,
                AppActivity.WELCOME);
        }

        AppSettings.getPenSettings().setSpenAvailable(this);

        if (AppSettings.getSystemSettings().isWifiConnected(this))
        {
            final Thread t = new Broadcast(); // TODO: Check for network flooding
            t.start();
        }
        prompt = new AlertDialog.Builder(this);

        this.startUsbDaemon();
    }

    private static final String SAMSUNG = "SAMSUNG";
    private static final String NOTE = "GT-N";
    private static final String SPEN_FEATURE = "com.sec.feature.spen_usp";

    private boolean isSPenSupported()
    {
        final FeatureInfo[] featureInfo = getPackageManager().getSystemAvailableFeatures();
        for (FeatureInfo info : featureInfo)
        {
            if (SPEN_FEATURE.equalsIgnoreCase(info.name))
            {
                return true;
            }
        }
        if (SAMSUNG.equalsIgnoreCase(Build.MANUFACTURER))
        {
            if (Build.MODEL.toUpperCase(Locale.ENGLISH).startsWith(NOTE))
            {
                return true;
            }
        }
        return false;
    }

    /*
    public static void unlockOrientation()
    {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public static void lockOrientation()
    {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }*/

    private boolean usbDebuggingEnabled()
    {
        return android.provider.Settings.Global.getInt(
            this.getContentResolver(),
            android.provider.Settings.Global.ADB_ENABLED,
            0) == 1;
    }

    private void showUsbPrompt(AlertDialog.Builder prompt)
    {
        prompt.setTitle("USB Debugging Disabled");
        prompt.setCancelable(false);
        prompt.setMessage(
            "You must enable developer options, then USB debugging." +
                "\n" +
                "\n" +
                "Would you like to go there now?"
        );

        prompt.setPositiveButton(
            "Yes",
            new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialogInterface, int id)
                {
                    startActivityForResult(
                        new Intent(android.provider.Settings
                            .ACTION_APPLICATION_DEVELOPMENT_SETTINGS),
                        0);
                }
            }
        );
        prompt.setNegativeButton(
            "No",
            new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialogInterface, int id)
                {
                    dialogInterface.cancel();
                }
            }
        );
        prompt.create();
        prompt.show();
    }

    private boolean usbDaemonRunning = true;
    private void startUsbDaemon()
    {
        final Thread usbCheckThread = new Thread(
            new Runnable()
            {
                @Override
                public void run()
                {
                    startInitial();
                    while (usbDaemonRunning)
                    {
                        try
                        {
                            Thread.sleep(1000); // TODO: Increase polling if power is connected -
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if (interfaceChanged())
                                    {
                                        AppSettings.getConnectionManager().reinitializeServers();
                                    }
                                }
                            });
                        } catch (final InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
            usbCheckThread.start();
    }

    private boolean interfaceChanged()
    {
        if (AppSettings.getSystemSettings().isUsbConnected(this))
        {
            if (usbDebuggingEnabled()) // Check if USB debugging is enabled
            {
                if (AppSettings.getConnectionManager().getConnectionMode()
                    == ConnectionMode.WIFI)
                {
                    switchInterface(ConnectionMode.USB);
                    return true;
                } else
                {
                    return false;
                }
            } else
            {
                AppSettings.getConnectionManager().setConnectionMode(ConnectionMode.NONE);
                return false;
            }
        } else
        {
            if (AppSettings.getSystemSettings().isWifiConnected(this))
            {
                if (AppSettings.getConnectionManager().getConnectionMode()
                    == ConnectionMode.USB)
                {
                    switchInterface(ConnectionMode.WIFI);
                    return true;
                } else
                {
                    return false;
                }
            } else
            {
                AppSettings.getConnectionManager().setConnectionMode(ConnectionMode.NONE);
                return false;
            }
        }
    }

    private void startInitial()
    {
        if (AppSettings.getSystemSettings().isUsbConnected(this)
                && usbDebuggingEnabled())// Check if USB debugging is enabled
        {
            switchInterface(ConnectionMode.USB);
        } else if (AppSettings.getSystemSettings().isWifiConnected(this))
        {
            switchInterface(ConnectionMode.WIFI);
        }
        AppSettings.getConnectionManager().reinitializeServers();
    }

    private void switchInterface(final ConnectionMode mode)
    {
        AppSettings.getConnectionManager().setConnectionMode(mode);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        /*
        * in onResume() so the check is called every time the app is brought into focus in case
        * the user enabled USB debugging
        */
        if (AppSettings.getSystemSettings().isUsbConnected(this)
                && !usbDebuggingEnabled()) // Check if USB debugging is disabled
        {
            this.showUsbPrompt(prompt);
        }
    }

    @Override
    protected void onDestroy()
    {
        if (spenEvent != null)
        {
            spenEvent.unregisterSPenDetachmentListener(this);
        }
        super.onDestroy();
    }

    // Getters
    public static Activity getActivity()
    {
        return currentActivity;
    }//returns this
}