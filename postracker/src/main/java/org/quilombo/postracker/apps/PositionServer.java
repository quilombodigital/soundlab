package org.quilombo.postracker.apps;

import com.fazecast.jSerialComm.SerialPort;
import org.quilombo.postracker.core.ProjectConfig;
import org.quilombo.postracker.gui.GuiUtil;
import org.quilombo.postracker.model.TagsHolder;
import org.quilombo.postracker.serial.SerialReaderWebsocketSender;
import org.quilombo.postracker.websocket.server.EventServer;
import org.quilombo.postracker.websocket.server.TagListProvider;

import java.io.IOException;

public class PositionServer {

    public static PositionServer instance = new PositionServer();

    public TagsHolder tagsHolder;
    public EventServer eventServer;

    private PositionServer() {
    }

    public void startServer(ProjectConfig config) throws InterruptedException, IOException {

        tagsHolder = new TagsHolder();

        SerialPort serial = SerialPort.getCommPort(config.serialPort);
        serial.setBaudRate(115200);
        SerialReaderWebsocketSender reader = new SerialReaderWebsocketSender(serial, tagsHolder);
        reader.setDaemon(true);
        reader.start();
        serial.openPort();

        //TODO detect if connected...

        serial.getOutputStream().write('\r');
        Thread.sleep(500);
        serial.getOutputStream().write('\r');
        //serial.getOutputStream().write("?\r".getBytes());
        Thread.sleep(3000);
        serial.getOutputStream().write("lep\r".getBytes());

        TagListProvider tagListProvider = () -> tagsHolder.getTags();
        eventServer = new EventServer(config.websocketPort, tagListProvider);
    }


    public static void main(String[] args) throws Exception {
        ProjectConfig config = ProjectConfig.load(GuiUtil.chooseProject());
        PositionServer.instance.startServer(config);
    }


}
