package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.MassEvacuation;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

/**
 * Interface for the MassEvacuationRepository which extends the generic IRepository interface.
 */
public interface IMassEvacuationRepository extends IRepository<MassEvacuation> {

    /**
     * Adds a new mass evacuation request to the repository.
     * @param message
     * @param creatorId
     * @param approverId
     * @return
     * @throws CannotWritetoFileException
     */
    boolean add(String message, int creatorId, int approverId) throws CannotWritetoFileException;

    /**
     * Adds a new mass evacuation request to the repository.
     * @param message
     * @param creatorId
     * @return
     * @throws CannotWritetoFileException
     */
    boolean add(String message, int creatorId) throws CannotWritetoFileException;

    /**
     * Updates the status of a mass evacuation request.
     * @param evacuation
     * @throws CannotWritetoFileException
     */
    void update(MassEvacuation evacuation) throws CannotWritetoFileException;

    /**
     * Retrieves a list of mass evacuation requests that are pending approval.
     * @return
     */
    List<MassEvacuation> getPendingApprovals();

}
