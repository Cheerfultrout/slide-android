package Connection;

import Settings.AppSettings;
import Settings.ConnectionSettings;
import Settings.Enums.ConnectionMode;

public class ConnectionManager
    extends ConnectionSettings
{
    public void send(final short[] pack)
    {
        ConnectionManager inContext = AppSettings.getInstance().getConnectionManager();
        if (inContext.getConnectionMode() == ConnectionMode.WIFI)
        {
            inContext.getNetworkConnectionManager().send(pack);
        } else if (inContext.getConnectionMode() == ConnectionMode.USB)
        {
            inContext.getUsbConnectionManager().send(pack);
        }
    }
}