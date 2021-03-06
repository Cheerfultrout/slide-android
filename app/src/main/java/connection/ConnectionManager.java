package connection;

import settings.AppSettings;
import settings.ConnectionSettings;
import settings.enums.ConnectionMode;

public class ConnectionManager
    extends ConnectionSettings
{
    public static void send(final short[] pack)
    {
        /*because we are getting the instantiated ConnectionManager from AppSettings,
        we can make the method static, and call ConnectionManager.send rather than
        first having to acquire an instance of it from calling classes*/
        ConnectionManager inContext = AppSettings.getConnectionManager();
        if (inContext.getConnectionMode() == ConnectionMode.WIFI)
        {
            inContext.getNetworkConnectionManager().send(pack);
        } else if (inContext.getConnectionMode() == ConnectionMode.USB)
        {
            inContext.getUsbConnectionManager().send(pack);
        }
    }
}