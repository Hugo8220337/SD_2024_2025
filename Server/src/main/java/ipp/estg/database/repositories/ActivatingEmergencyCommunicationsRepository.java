package ipp.estg.database.repositories;

import ipp.estg.database.models.ActivatingEmergencyCommunications;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IActivatingEmergencyCommunicationsRepository;
import ipp.estg.utils.FileUtils;

import java.util.List;

public class ActivatingEmergencyCommunicationsRepository implements IActivatingEmergencyCommunicationsRepository {

    private final FileUtils<ActivatingEmergencyCommunications> fileUtils;

    public ActivatingEmergencyCommunicationsRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    @Override
    public synchronized List<ActivatingEmergencyCommunications> getAll() {
        return fileUtils.readObjectListFromFile();
    }

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

    @Override
    public synchronized List<ActivatingEmergencyCommunications> getPendingApprovals() {
        List<ActivatingEmergencyCommunications> emergencyCommunications = fileUtils.readObjectListFromFile();
        emergencyCommunications.removeIf(evac -> evac.getApproverId() != -1);
        return emergencyCommunications;
    }

    @Override
    public synchronized void remove(int id) throws CannotWritetoFileException {
        List<ActivatingEmergencyCommunications> emergencyCommunications = getAll();

        emergencyCommunications.removeIf(evac -> evac.getId() == id);

        fileUtils.writeObjectListToFile(emergencyCommunications);
    }

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
