package ipp.estg.database.repositories;

import ipp.estg.database.models.ActivatingEmergencyCommunications;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IActivatingEmergencyCommunicationsRepository;
import ipp.estg.database.repositories.interfaces.INotificationRepository;
import ipp.estg.utils.FileUtils;

import java.util.List;

/**
 * Repository for managing Activating Emergency Communications, including adding, updating,
 * retrieving, and removing emergency communications requests.
 * This class utilizes the {@link FileUtils} class for file operations.
 */
public class ActivatingEmergencyCommunicationsRepository implements IActivatingEmergencyCommunicationsRepository {

    /**
     * File utility for handling Activating Emergency Communications data.
     */
    private final FileUtils<ActivatingEmergencyCommunications> fileUtils;

    /**
     * Constructor that initializes the repository with a specified file path.
     *
     * @param filePath The file path to read/write the emergency communications data.
     */
    public ActivatingEmergencyCommunicationsRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    /**
     * Retrieves all activating emergency communications from the repository.
     *
     * @return A list of all activating emergency communications.
     */
    @Override
    public synchronized List<ActivatingEmergencyCommunications> getAll() {
        return fileUtils.readObjectListFromFile();
    }

    /**
     * Adds a new emergency communication with both creator and approver IDs.
     *
     * @param message     The message describing the emergency communication.
     * @param creatorId   The ID of the creator of the communication.
     * @param approverId  The ID of the approver of the communication.
     * @return True if the communication was successfully added, otherwise false.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized boolean add(
            String message,
            int creatorId,
            int approverId
    ) throws CannotWritetoFileException {
        List<ActivatingEmergencyCommunications> emergencyCommunications = fileUtils.readObjectListFromFile();

        ActivatingEmergencyCommunications newRegister = new
                ActivatingEmergencyCommunications(emergencyCommunications.size() + 1, message, creatorId, approverId);
        emergencyCommunications.add(newRegister);

        return fileUtils.writeObjectListToFile(emergencyCommunications);
    }

    /**
     * Adds a new emergency communication with only the creator ID.
     *
     * @param message     The message describing the emergency communication.
     * @param creatorId   The ID of the creator of the communication.
     * @return True if the communication was successfully added, otherwise false.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized boolean add(
            String message,
            int creatorId
    ) throws CannotWritetoFileException {
        List<ActivatingEmergencyCommunications> emergencyCommunications = fileUtils.readObjectListFromFile();

        ActivatingEmergencyCommunications newRegister = new
                ActivatingEmergencyCommunications(emergencyCommunications.size() + 1, message, creatorId);
        emergencyCommunications.add(newRegister);

        return fileUtils.writeObjectListToFile(emergencyCommunications);
    }

    /**
     * Updates an existing emergency communication in the repository.
     *
     * @param emergencyCommunication The updated emergency communication object.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized void update(ActivatingEmergencyCommunications emergencyCommunication) throws CannotWritetoFileException {
        List<ActivatingEmergencyCommunications> emergencyCommunications = fileUtils.readObjectListFromFile();

        for (int i = 0; i < emergencyCommunications.size(); i++) {
            if (emergencyCommunications.get(i).getId() == emergencyCommunication.getId()) {
                emergencyCommunications.set(i, emergencyCommunication);
                break;
            }
        }

        fileUtils.writeObjectListToFile(emergencyCommunications);
    }

    /**
     * Retrieves a list of all emergency communications that are pending approval.
     * Pending approval communications are those with an approver ID of -1.
     *
     * @return A list of emergency communications that are pending approval.
     */
    @Override
    public synchronized List<ActivatingEmergencyCommunications> getPendingApprovals() {
        List<ActivatingEmergencyCommunications> emergencyCommunications = fileUtils.readObjectListFromFile();
        emergencyCommunications.removeIf(evac -> evac.getApproverId() != -1);
        return emergencyCommunications;
    }

    /**
     * Removes an emergency communication by its ID.
     *
     * @param id The ID of the emergency communication to remove.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized void remove(int id) throws CannotWritetoFileException {
        List<ActivatingEmergencyCommunications> emergencyCommunications = getAll();

        emergencyCommunications.removeIf(evac -> evac.getId() == id);

        fileUtils.writeObjectListToFile(emergencyCommunications);
    }

    /**
     * Retrieves an emergency communication by its ID.
     *
     * @param id The ID of the emergency communication to retrieve.
     * @return The emergency communication with the specified ID, or null if not found.
     */
    @Override
    public synchronized ActivatingEmergencyCommunications getById(int id) {
        List<ActivatingEmergencyCommunications> emergencyCommunications = getAll();
        for (ActivatingEmergencyCommunications emergency : emergencyCommunications) {
            if (emergency.getId() == id) {
                return emergency;
            }
        }
        return null;
    }

}
