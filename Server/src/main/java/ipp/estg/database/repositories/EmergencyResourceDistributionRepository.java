package ipp.estg.database.repositories;

import ipp.estg.database.models.EmergencyResourceDistribution;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IEmergencyResourceDistributionRepository;
import ipp.estg.database.repositories.interfaces.INotificationRepository;
import ipp.estg.utils.FileUtils;

import java.util.List;

/**
 * Repository for managing emergency resource distribution records. This repository
 * allows adding, updating, retrieving, and removing emergency resource distribution entries.
 * It uses the {@link FileUtils} class for reading and writing data from a file.
 */
public class EmergencyResourceDistributionRepository implements IEmergencyResourceDistributionRepository {

    /**
     * File utility for handling emergency resource distribution data.
     */
    private final FileUtils<EmergencyResourceDistribution> fileUtils;

    /**
     * Constructor that initializes the repository with a specified file path.
     *
     * @param filePath The file path to read/write the emergency resource distribution data.
     */
    public EmergencyResourceDistributionRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    /**
     * Retrieves all emergency resource distribution records from the repository.
     *
     * @return A list of all emergency resource distribution records.
     */
    public synchronized List<EmergencyResourceDistribution> getAll() {
        return fileUtils.readObjectListFromFile();
    }

    /**
     * Adds a new emergency resource distribution record with a message, creator ID, and approver ID.
     *
     * @param message    The message associated with the distribution.
     * @param creatorId  The ID of the creator of the distribution.
     * @param approverId The ID of the approver for the distribution.
     * @return True if the record was successfully added, otherwise false.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized boolean add(String message, int creatorId,  int approverId) throws CannotWritetoFileException {
        List<EmergencyResourceDistribution> emergency = fileUtils.readObjectListFromFile();

        EmergencyResourceDistribution newRegister =
                new EmergencyResourceDistribution(emergency.size() + 1, message, creatorId, approverId);
        emergency.add(newRegister);

        return fileUtils.writeObjectListToFile(emergency);
    }

    /**
     * Adds a new emergency resource distribution record with a message and creator ID.
     * This version does not require an approver ID.
     *
     * @param message   The message associated with the distribution.
     * @param creatorId The ID of the creator of the distribution.
     * @return True if the record was successfully added, otherwise false.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized boolean add(String message, int creatorId) throws CannotWritetoFileException {
        List<EmergencyResourceDistribution> emergency = fileUtils.readObjectListFromFile();

        EmergencyResourceDistribution newRegister =
                new EmergencyResourceDistribution(emergency.size() + 1, message, creatorId);
        emergency.add(newRegister);

        return fileUtils.writeObjectListToFile(emergency);
    }

    /**
     * Updates an existing emergency resource distribution record.
     *
     * @param evacuation The updated emergency resource distribution record.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized void update(EmergencyResourceDistribution evacuation) throws CannotWritetoFileException {
        List<EmergencyResourceDistribution> emergency = fileUtils.readObjectListFromFile();

        for (int i = 0; i < emergency.size(); i++) {
            if (emergency.get(i).getId() == evacuation.getId()) {
                emergency.set(i, evacuation);
                break;
            }
        }

        fileUtils.writeObjectListToFile(emergency);
    }

    /**
     * Retrieves all emergency resource distribution records that are pending approval (approver ID is -1).
     *
     * @return A list of emergency resource distribution records pending approval.
     */
    @Override
    public synchronized List<EmergencyResourceDistribution> getPendingApprovals() {
        List<EmergencyResourceDistribution> emergency = fileUtils.readObjectListFromFile();
        emergency.removeIf(evacuation -> evacuation.getApproverId() != -1);
        return emergency;
    }

    /**
     * Removes an emergency resource distribution record by its ID.
     *
     * @param id The ID of the emergency resource distribution record to remove.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized void remove(int id) throws CannotWritetoFileException {

        List<EmergencyResourceDistribution> emergency = getAll();
        emergency.removeIf(evacuation -> evacuation.getId() == id);
        fileUtils.writeObjectListToFile(emergency);
    }

    /**
     * Retrieves an emergency resource distribution record by its ID.
     *
     * @param id The ID of the emergency resource distribution record to retrieve.
     * @return The emergency resource distribution record with the specified ID, or null if not found.
     */
    @Override
    public synchronized EmergencyResourceDistribution getById(int id) {
        List<EmergencyResourceDistribution> emergency = getAll();
        for (EmergencyResourceDistribution evacuation : emergency) {
            if (evacuation.getId() == id) {
                return evacuation;
            }
        }
        return null;
    }
}
