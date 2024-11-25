package ipp.estg.commands.channels;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.Channel;
import ipp.estg.database.repositories.ChannelRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.JsonConverter;

import java.util.List;

public class GetChannelsCommand implements ICommand {
    private final WorkerThread workerThread;
    private final ChannelRepository channelRepository;
    private final String[] inputArray;

    public GetChannelsCommand(WorkerThread workerThread, ChannelRepository channelRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.channelRepository = channelRepository;
        this.inputArray = inputArray;
    }

    @Override
    public void execute() {
        try {
            List<Channel> chanels = channelRepository.getAll();

            // Convert to JSON
            JsonConverter converter = new JsonConverter();
            String json = converter.toJson(chanels);

            workerThread.sendMessage(json);
        } catch (Exception e) {
            workerThread.sendMessage("ERROR: Could not get channels");  // TODO retirar porque o server não pode parar, substituir por um log
        }
    }
}
