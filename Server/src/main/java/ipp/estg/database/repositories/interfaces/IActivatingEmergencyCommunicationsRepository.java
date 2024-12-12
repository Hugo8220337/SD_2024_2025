package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.ActivatingEmergencyCommunications;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

/**
 * Interface for the ActivatingEmergencyCommunicationsRepository, which extends the generic IRepository interface.
 * This interface defines methods for managing emergency communications requests, including adding new requests,
 * updating them, and retrieving pending approvals.
 */
public interface IActivatingEmergencyCommunicationsRepository extends IRepository<ActivatingEmergencyCommunications>{

    /**
     * Adds a new emergency communications request to the repository.
     *
     * @param message     The message describing the emergency communication request.
     * @param creatorId   The ID of the user who created the emergency communication request.
     * @param approverId  The ID of the user who approves the request.
     * @return True if the request was successfully added, otherwise false.
     * @throws CannotWritetoFileException If there is an error while writing to the file or database.
     */
    boolean add(String message, int creatorId, int approverId) throws CannotWritetoFileException;

    /**
     * Adds a new emergency communications request with only the creator's ID.
     *
     * @param message     The message describing the emergency communication request.
     * @param creatorId   The ID of the user who created the emergency communication request.
     * @return True if the request was successfully added, otherwise false.
     * @throws CannotWritetoFileException If there is an error while writing to the file or database.
     */
    boolean add(String message, int creatorId) throws CannotWritetoFileException;

    /**
     * Updates an existing emergency communications request in the repository.
     *
     * @param emergencyCommunications The emergency communications object containing updated information.
     * @throws CannotWritetoFileException If there is an error while writing to the file or database.
     */
    void update(ActivatingEmergencyCommunications emergencyCommunications) throws CannotWritetoFileException;

    /**
     * Retrieves a list of all emergency communications requests that are pending approval.
     *
     * @return A list of emergency communications requests that need approval.
     */
    List<ActivatingEmergencyCommunications> getPendingApprovals();
}
