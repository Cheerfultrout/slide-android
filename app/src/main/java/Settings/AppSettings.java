package Settings;

import android.content.Context;

import Connection.ConnectionManager;

public class AppSettings
{
    private static Context currentContext;

    private static ActivitySettings activitySettings;
    private static CanvasSettings canvasSettings;
    private static ConnectionManager connectionManager;
    private static PenSettings penSettings;
    private static SettingsElements settingsElements;

    private static SystemSettings systemSettings;

    private static AppSettings instance = null;

    static{
        canvasSettings = new CanvasSettings();
        connectionManager = new ConnectionManager();
        penSettings = new PenSettings();
        systemSettings = new SystemSettings();
    }

    // Getter for instance
    public static synchronized AppSettings getInstance()
    {
        /*previously had an if(instance)==null statement (probably to prevent null pointer
        exceptions) however making instance not null by calling new AppSettings(); would throw
        a null pointer exception upon trying to access currentContext, therefore this null value
        check does not actually prevent *any* null pointer exceptions and is thus unnecessary*/
        return instance;
    }

    public static synchronized AppSettings getInstanceSetContext(final Context c)
    {
        if (instance == null)
        {
            instance = new AppSettings(c);
        }
        return instance;
    }

    /*removed no argument constructor. Reason: the only code executed inside it that *wasn't*
    moved to the static block at the top of the file would throw an error unless
    AppSettings(Context c) had already been called. (and if it *had* been called, that line would
    be entirely redundant)*/

    private AppSettings(final Context c)
    {
        currentContext = c;
        activitySettings = new ActivitySettings(c);
    }

    // Getters
    public static ActivitySettings getActivitySettings()
    {
        return activitySettings;
    }

    public static CanvasSettings getCanvasSettings()
    {
        return canvasSettings;
    }

    public static ConnectionManager getConnectionManager()
    {
        return connectionManager;
    }

    public static PenSettings getPenSettings()
    {
        return penSettings;
    }

    public static SettingsElements getSettingsElements()
    {
        return settingsElements;
    }

    public static SystemSettings getSystemSettings()
    {
        return systemSettings;
    }

    public static Context getCurrentContext()
    {
        return currentContext;
    }

    // Setters
    public static void setSettingsElements(final SettingsElements el)
    {
        settingsElements = el;
    }

    public static void setCurrentContext(final Context c)
    {
        currentContext = c;
    }
}