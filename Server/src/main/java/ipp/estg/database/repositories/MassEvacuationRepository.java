package ipp.estg.database.repositories;

import ipp.estg.database.models.MassEvacuation;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IMassEvacuationRepository;
import ipp.estg.utils.FileUtils;

import java.util.List;


public class MassEvacuationRepository implements IMassEvacuationRepository {
    private final FileUtils<MassEvacuation> fileUtils;

    public MassEvacuationRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    @Override
    public synchronized List<MassEvacuation> getAll() {
        return fileUtils.readObjectListFromFile();
    }

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

    @Override
    public synchronized List<MassEvacuation> getPendingApprovals() {
        List<MassEvacuation> evacuations = fileUtils.readObjectListFromFile();
        evacuations.removeIf(evac -> evac.getApproverId() != -1);
        return evacuations;
    }

    @Override
    public synchronized void remove(int id) throws CannotWritetoFileException {
        List<MassEvacuation> evacuations = getAll();

        evacuations.removeIf(evac -> evac.getId() == id);

        fileUtils.writeObjectListToFile(evacuations);
    }

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