package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.MassEvacuation;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

public interface IMassEvacuationRepository extends IRepository<MassEvacuation> {
    boolean add(String message, int creatorId, int approverId) throws CannotWritetoFileException;

    boolean add(String message, int creatorId) throws CannotWritetoFileException;

    void update(MassEvacuation evacuation) throws CannotWritetoFileException;

    List<MassEvacuation> getPendingApprovals();

}
