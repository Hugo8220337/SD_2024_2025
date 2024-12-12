package ipp.estg.database.repositories;

import ipp.estg.database.models.MassEvacuation;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IMassEvacuationRepository;
import ipp.estg.utils.FileUtils;

import java.util.List;

/**
 * Repository for managing mass evacuation records. This repository allows adding, updating, retrieving,
 * and removing mass evacuation entries. It uses the {@link FileUtils} class for reading and writing data from a file.
 */

public class MassEvacuationRepository implements IMassEvacuationRepository {

    /**
     * File utility for handling mass evacuation data.
     */
    private final FileUtils<MassEvacuation> fileUtils;

    /**
     * Constructor that initializes the repository with a specified file path.
     *
     * @param filePath The file path to read/write the mass evacuation data.
     */
    public MassEvacuationRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    /**
     * Retrieves all mass evacuation records from the repository.
     *
     * @return A list of all mass evacuation records.
     */
    @Override
    public synchronized List<MassEvacuation> getAll() {
        return fileUtils.readObjectListFromFile();
    }

    /**
     * Adds a new mass evacuation record with a message, creator ID, and approver ID.
     *
     * @param message    The message associated with the evacuation.
     * @param creatorId  The ID of the creator of the evacuation.
     * @param approverId The ID of the approver for the evacuation.
     * @return True if the record was successfully added, otherwise false.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized boolean add(
            String message,
            int creatorId,
            int approverId
    ) throws CannotWritetoFileException {
        List<MassEvacuation> evacuations = fileUtils.readObjectListFromFile();

        MassEvacuation newRegister = new MassEvacuation(evacuations.size() + 1, message, creatorId, approverId);
        evacuations.add(newRegister);

        return fileUtils.writeObjectListToFile(evacuations);
    }

    /**
     * Adds a new mass evacuation record with a message and creator ID. This version does not require an approver ID.
     *
     * @param message   The message associated with the evacuation.
     * @param creatorId The ID of the creator of the evacuation.
     * @return True if the record was successfully added, otherwise false.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized boolean add(
            String message,
            int creatorId
    ) throws CannotWritetoFileException {
        List<MassEvacuation> evacuations = fileUtils.readObjectListFromFile();

        MassEvacuation newRegister = new MassEvacuation(evacuations.size() + 1, message, creatorId);
        evacuations.add(newRegister);

        return fileUtils.writeObjectListToFile(evacuations);
    }

    /**
     * Updates an existing mass evacuation record.
     *
     * @param evacuation The updated mass evacuation record.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public void update(MassEvacuation evacuation) throws CannotWritetoFileException {
        List<MassEvacuation> evacuations = fileUtils.readObjectListFromFile();

        for (int i = 0; i < evacuations.size(); i++) {
            if (evacuations.get(i).getId() == evacuation.getId()) {
                evacuations.set(i, evacuation);
                break;
            }
        }

        fileUtils.writeObjectListToFile(evacuations);
    }

    /**
     * Retrieves all mass evacuation records that are pending approval (approver ID is -1).
     *
     * @return A list of mass evacuation records pending approval.
     */
    @Override
    public synchronized List<MassEvacuation> getPendingApprovals() {
        List<MassEvacuation> evacuations = fileUtils.readObjectListFromFile();
        evacuations.removeIf(evac -> evac.getApproverId() != -1);
        return evacuations;
    }

    /**
     * Removes a mass evacuation record by its ID.
     *
     * @param id The ID of the mass evacuation record to remove.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized void remove(int id) throws CannotWritetoFileException {
        List<MassEvacuation> evacuations = getAll();
        evacuations.removeIf(evac -> evac.getId() == id);
        fileUtils.writeObjectListToFile(evacuations);
    }

    /**
     * Retrieves a mass evacuation record by its ID.
     *
     * @param id The ID of the mass evacuation record to retrieve.
     * @return The mass evacuation record with the specified ID, or null if not found.
     */
    @Override
    public synchronized MassEvacuation getById(int id) {
        List<MassEvacuation> evacuations = getAll();
        for (MassEvacuation evac : evacuations) {
            if (evac.getId() == id) {
                return evac;
            }
        }
        return null;
    }

}