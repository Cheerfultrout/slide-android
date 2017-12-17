package settings;

import android.preference.Preference;

import common.SystemInfo;
import connection.network.NetworkConnectionManager;
import connection.usb.UsbConnectionManager;
import settings.enums.ConnectionMode;

public class ConnectionSettings
{
    private ConnectionMode connectionMode;

    private NetworkConnectionManager networkConnectionManager;
    private UsbConnectionManager usbConnectionManager;

    protected ConnectionSettings()
    {
        loadFactoryDefaults();
    }

    private void loadFactoryDefaults()
    {
        setConnectionMode(ConnectionMode.NONE);
        setNetworkConnectionManager(new NetworkConnectionManager());
        setUsbConnectionManager(new UsbConnectionManager());
    }

    public void reinitializeServers()
    {
        if (getConnectionMode() == ConnectionMode.WIFI)
        {
            if (getNetworkConnectionManager().isRunning())
            {
                getNetworkConnectionManager().stopListening();
            }
            setNetworkConnectionManager(new NetworkConnectionManager());
            getNetworkConnectionManager().start();
        } else if (getConnectionMode() == ConnectionMode.USB)
        {
            if (getUsbConnectionManager().isRunning())
            {
                getUsbConnectionManager().stopListening();
            }
            setUsbConnectionManager(new UsbConnectionManager());
            getUsbConnectionManager().start();
        } else
        {
            return;
        }

        updateUi(getConnectionMode());
    }

    // Setters
    public void setConnectionMode(final ConnectionMode mode)
    {
        this.connectionMode = mode;
    }

    private void setNetworkConnectionManager(final NetworkConnectionManager netMan)
    {
        this.networkConnectionManager = netMan;
    }

    private void setUsbConnectionManager(final UsbConnectionManager usbMan)
    {
        this.usbConnectionManager = usbMan;
    }

    // Getters
    public ConnectionMode getConnectionMode()
    {
        return this.connectionMode;
    }

    public NetworkConnectionManager getNetworkConnectionManager()
    {
        return this.networkConnectionManager;
    }

    public UsbConnectionManager getUsbConnectionManager()
    {
        return this.usbConnectionManager;
    }

    //updates the connection mode text
    private static void updateUi(final ConnectionMode mode)
    {
        Preference inContext = AppSettings.getSettingsElements().getPrefConnectionStatus();
        inContext.setEnabled(false);
        String summary = "Listening on ";
        if (mode == ConnectionMode.USB)
        {
            summary+="USB";
        } else if (mode == ConnectionMode.WIFI)
        {
            final SystemInfo ipv4 = AppSettings.getSystemSettings().getSystemInfo();
            summary += "WiFi (" + ipv4.ipv4Address().get(0).getHostAddress() + ")";
        }
        inContext.setSummary(summary);
    }
}