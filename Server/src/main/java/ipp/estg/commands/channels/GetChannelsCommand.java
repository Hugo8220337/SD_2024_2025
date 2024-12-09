package ipp.estg.commands.channels;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.Channel;
import ipp.estg.database.repositories.ChannelRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.JsonConverter;

import java.util.List;

public class GetChannelsCommand implements ICommand {
    private final WorkerThread workerThread;
    private final ChannelRepository channelRepository;
    private static final AppLogger LOGGER = AppLogger.getLogger(GetChannelsCommand.class);

    public GetChannelsCommand(WorkerThread workerThread, ChannelRepository channelRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.channelRepository = channelRepository;
    }

    @Override
    public void execute() {
        try {
            List<Channel> chanels = channelRepository.getAll();

            // Convert to JSON
            JsonConverter converter = new JsonConverter();
            String json = converter.toJson(chanels);

            workerThread.sendMessage(json);
            LOGGER.info("Channels sent to client");
        } catch (Exception e) {
            workerThread.sendMessage("ERROR: Could not get channels");
            LOGGER.error("Could not get channels: " + e.getMessage());
        }
    }
}
