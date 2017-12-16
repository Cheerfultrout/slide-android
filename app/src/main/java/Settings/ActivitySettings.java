package Settings;

import android.content.Context;
import android.content.Intent;

import com.j03.mobileinput.SettingsActivity;
import com.j03.mobileinput.WelcomeActivity;
import com.j03.mobileinput.canvas.CanvasActivity;

import Common.Enums.AppActivity;

public class ActivitySettings
{
    final private Intent settingsIntent;
    final private Intent canvasIntent;
    final private Intent welcomeIntent;

    ActivitySettings(final Context context)
    {
        this.settingsIntent = new Intent(context, SettingsActivity.class);
        this.canvasIntent = new Intent(context, CanvasActivity.class);
        this.welcomeIntent = new Intent(context, WelcomeActivity.class);
    }

    /**
     * @param context
     *     The context to start the Activity from
     * @param activity
     *     The Activity to load
     */
    public void loadActivity(final Context context, final AppActivity activity)
    {
        switch (activity)
        {
            case SETTINGS:
                context.startActivity(getSettingsIntent());
                break;

            case CANVAS:
                context.startActivity(getCanvasIntent());
                break;

            case WELCOME:
                context.startActivity(getWelcomeIntent());
                break;
        }
    }

    public void loadActivity(final Context context, final Intent activity)
    {
        context.startActivity(activity);
    }

    private Intent getSettingsIntent()
    {
        return this.settingsIntent;
    }

    public Intent getCanvasIntent()
    {
        return this.canvasIntent;
    }

    private Intent getWelcomeIntent()
    {
        return this.welcomeIntent;
    }
}