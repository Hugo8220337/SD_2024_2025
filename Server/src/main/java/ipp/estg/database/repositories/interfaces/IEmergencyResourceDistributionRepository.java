package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.EmergencyResourceDistribution;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

/**
 * Interface for the EmergencyResourceDistributionRepository which extends the generic IRepository interface.
 */
public interface IEmergencyResourceDistributionRepository extends IRepository<EmergencyResourceDistribution> {

    /**
     * Adds a new emergency resource distribution request to the repository.
     * @param message
     * @param creatorId
     * @param approverId
     * @return
     * @throws CannotWritetoFileException
     */
    boolean add(String message, int creatorId, int approverId) throws CannotWritetoFileException;

    /**
     * Adds a new emergency resource distribution request to the repository.
     * @param message
     * @param creatorId
     * @return
     * @throws CannotWritetoFileException
     */
    boolean add(String message, int creatorId) throws CannotWritetoFileException;

    /**
     * Updates the status of an emergency resource distribution request.
     * @param evacuation
     * @throws CannotWritetoFileException
     */
    void update(EmergencyResourceDistribution evacuation) throws CannotWritetoFileException;

    /**
     * Retrieves a list of emergency resource distribution requests that are pending approval.
     * @return
     */
    List<EmergencyResourceDistribution> getPendingApprovals();
}
