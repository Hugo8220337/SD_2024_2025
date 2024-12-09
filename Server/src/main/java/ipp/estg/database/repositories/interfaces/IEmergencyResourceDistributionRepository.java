package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.EmergencyResourceDistribution;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

public interface IEmergencyResourceDistributionRepository extends IRepository<EmergencyResourceDistribution> {
    boolean add(String message, int creatorId, int approverId) throws CannotWritetoFileException;

    boolean add(String message, int creatorId) throws CannotWritetoFileException;

    void update(EmergencyResourceDistribution evacuation) throws CannotWritetoFileException;

    List<EmergencyResourceDistribution> getPendingApprovals();
}
