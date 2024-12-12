package ipp.estg.commands.channels;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.Channel;
import ipp.estg.database.repositories.ChannelRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.JsonConverter;

import java.util.List;

/**
 * Command to retrieve and send a list of all available channels to the client.
 * This command queries the channel repository and sends the result as a JSON response.
 */
public class GetChannelsCommand implements ICommand {

    /**
     * Worker thread that will send the response to the client.
     */
    private final WorkerThread workerThread;

    /**
     * Repository to query the channels.
     */
    private final ChannelRepository channelRepository;

    /**
     * Logger for the class.
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(GetChannelsCommand.class);

    /**
     * Constructs a new GetChannelsCommand.
     *
     * @param workerThread the worker thread associated with the current command
     * @param channelRepository the repository for channel-related operations
     */
    public GetChannelsCommand(WorkerThread workerThread, ChannelRepository channelRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.channelRepository = channelRepository;
    }

    /**
     * Executes the command to retrieve all channels from the repository.
     * Converts the list of channels to a JSON string and sends it to the client.
     *
     * If an error occurs while retrieving the channels, an error message is sent to the client.
     */
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
