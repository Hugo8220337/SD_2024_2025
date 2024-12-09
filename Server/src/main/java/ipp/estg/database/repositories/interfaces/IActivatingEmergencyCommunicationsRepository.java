package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.ActivatingEmergencyCommunications;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

public interface IActivatingEmergencyCommunicationsRepository extends IRepository<ActivatingEmergencyCommunications>{
    boolean add(String message, int creatorId, int approverId) throws CannotWritetoFileException;

    boolean add(String message, int creatorId) throws CannotWritetoFileException;

    void update(ActivatingEmergencyCommunications emergencyCommunications) throws CannotWritetoFileException;

    List<ActivatingEmergencyCommunications> getPendingApprovals();
}
