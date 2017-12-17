package connection.usb;

import java.io.IOException;

import gesture.binding.actions.Toggle;
import gesture.binding.actions.Trigger;
import layout.enums.PositioningMode;
import settings.AppSettings;

public class UsbConnectionManager
    extends UsbServer
{
    public void send(final short[] pack)
    {
        writeToStream(pack);
    }

    void send(final PositioningMode positioningMode, final short sensitivity)
    {
        short[] pack = null;
        final short version = AppSettings.getSystemSettings().getSystemInfo().getVersion();

        switch (positioningMode)
        {
            case RELATIVE:
                pack = Toggle.RELATIVE;
                pack[1] = sensitivity;
                pack[2] = version;
                break;

            case ABSOLUTE:
                pack = Toggle.ABSOLUTE;
                pack[3] = sensitivity;
                pack[4] = version;
                break;
        }
        writeToStream(pack);
    }

    private void writeToStream(final short[] pack)
    {
        try
        {
            getOutput().writeObject(pack);
            //getOutput().flush(); //do we need to clear the buffer each time?
        } catch (IOException ioException)
        {
            close();
        }
    }

    public void close()
    {
        try
        {
            send(Trigger.CLOSE);
            if (getOutput() != null)
            {
                getOutput().flush();
                getOutput().close();
            }
            if (getClient() != null)
            {
                getClient().close();
            }
            if (getServer() != null)
            {
                getServer().close();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void stopListening()
    {
        try
        {
            getServer().close();
        } catch (final IOException e)
        {
            e.printStackTrace();
        }
    }
}