package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.EmergencyResourceDistribution;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

public interface IEmergencyResourceDistributionRepository extends IRepository<EmergencyResourceDistribution> {
    boolean add(String message, String approverId) throws CannotWritetoFileException;

    boolean add(String message) throws CannotWritetoFileException;

    void update(EmergencyResourceDistribution evacuation) throws CannotWritetoFileException;

    List<EmergencyResourceDistribution> getPendingApprovals();
}
