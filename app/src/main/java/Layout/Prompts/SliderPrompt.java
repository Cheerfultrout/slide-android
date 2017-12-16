package Layout.Prompts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.j03.mobileinput.R;

public abstract class SliderPrompt
{
    private AlertDialog sensitivityDialog;
    private double sensitivity;
    private SeekBar seekbarSensitivity;
    private TextView txtProgress;

    SliderPrompt(final PreferenceActivity activity, final String title, final String message)
    {
        final AlertDialog.Builder slidebarBuilder =
            new AlertDialog.Builder(activity);
        slidebarBuilder.setTitle(title);
        slidebarBuilder.setMessage(message);
        //"Adjust the amout of pressure you must apply before a click is registered");
        final LayoutInflater inflater =
            (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.pref_sensitivity_dialog, null);

        if (v != null)
        {
            this.txtProgress = (TextView) v.findViewById(R.id.text_progress);
            seekbarSensitivity =
                (SeekBar) v.findViewById(R.id.pressure_seek_bar);
            slidebarBuilder.setView(v);
            slidebarBuilder.setNegativeButton(
                "Cancel", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        sensitivityDialog.dismiss();
                    }
                });

            slidebarBuilder.setPositiveButton("Ok", this.onOk());

            sensitivityDialog = slidebarBuilder.create();
            sensitivityDialog.setOnShowListener(this.onShow());

            seekbarSensitivity.setOnSeekBarChangeListener(onSeekBarChange());
        }
    }

    public double getValue()
    {
        return seekbarSensitivity.getProgress();
    }

    public void show()
    {
        this.sensitivityDialog.show();
    }

    void dismissDialog()
    {
        this.sensitivityDialog.dismiss();
    }

    double getSensitivity()
    {
        return this.sensitivity;
    }

    SeekBar getSeekbarSensitivity()
    {
        return this.seekbarSensitivity;
    }

    void setSensitivity(final double sensitivity)
    {
        this.sensitivity = sensitivity;
    }

    void setText(final String text)
    {
        this.txtProgress.setText(text);
    }

    protected abstract DialogInterface.OnClickListener onOk();

    protected abstract DialogInterface.OnShowListener onShow();

    protected abstract SeekBar.OnSeekBarChangeListener onSeekBarChange();
}