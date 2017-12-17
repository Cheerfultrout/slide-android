package connection.usb;

import com.j03.mobileinput.SettingsActivity;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import common.enums.AppActivity;
import layout.enums.PositioningMode;
import settings.AppSettings;

public class UsbServer
    extends Thread
{
    private ServerSocket server;
    private Socket client;
    private final short port = 8072;
    private ObjectOutputStream output;
    private boolean running = false;

    @Override
    public void run()
    {
        this.running = true;
        try
        {
            server = new ServerSocket(getPort());
            server.setReuseAddress(true);
            if (!server.isBound())
            {
                server.bind(new InetSocketAddress(getPort()));
            }

            if (!server.isClosed())
            {
                client = server.accept();
                client.setTcpNoDelay(true);

                output = new ObjectOutputStream(client.getOutputStream());
                output.flush();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        //connected
        if (client != null)
        {
            final short sensitivity = AppSettings.getSystemSettings().getMouseSensitivity();

            if (AppSettings.getSettingsElements().getPositioningMode() == PositioningMode.ABSOLUTE)
            {
                AppSettings.getConnectionManager()
                    .getUsbConnectionManager()
                    .send(PositioningMode.ABSOLUTE, sensitivity);
            } else
            {
                AppSettings.getConnectionManager().getUsbConnectionManager().send(
                    PositioningMode.RELATIVE, sensitivity);
            }

            // Load the Canvas
            AppSettings.getActivitySettings()
                    .loadActivity(SettingsActivity.getActivity(), AppActivity.CANVAS);
        }
    }

    // Getters
    private short getPort()
    {
        return this.port;
    }

    ObjectOutputStream getOutput()
    {
        return this.output;
    }

    ServerSocket getServer()
    {
        return this.server;
    }

    Socket getClient()
    {
        return this.client;
    }

    public boolean isRunning()
    {
        return this.running;
    }
}