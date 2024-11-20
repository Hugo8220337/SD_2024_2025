package ipp.estg.database.repositories;

import ipp.estg.database.models.EmergencyResourceDistribution;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IEmergencyResourceDistributionRepository;
import ipp.estg.utils.FileUtils;

import java.util.List;

public class EmergencyResourceDistributionRepository implements IEmergencyResourceDistributionRepository {

    private final FileUtils<EmergencyResourceDistribution> fileUtils;

    public EmergencyResourceDistributionRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    public synchronized List<EmergencyResourceDistribution> getAll() {
        return fileUtils.readObjectListFromFile();
    }

    @Override
    public boolean add(String message, String approverId) throws CannotWritetoFileException {
        List<EmergencyResourceDistribution> emergency = fileUtils.readObjectListFromFile();
        int approverIdInt = Integer.parseInt(approverId);

        EmergencyResourceDistribution newRegister = new EmergencyResourceDistribution(emergency.size() + 1, message, approverIdInt);
        emergency.add(newRegister);

        return fileUtils.writeObjectListToFile(emergency);
    }

    @Override
    public boolean add(String message) throws CannotWritetoFileException {
        List<EmergencyResourceDistribution> emergency = fileUtils.readObjectListFromFile();

        EmergencyResourceDistribution newRegister = new EmergencyResourceDistribution(emergency.size() + 1, message);
        emergency.add(newRegister);

        return fileUtils.writeObjectListToFile(emergency);
    }

    @Override
    public void update(EmergencyResourceDistribution evacuation) throws CannotWritetoFileException {

        List<EmergencyResourceDistribution> emergency = fileUtils.readObjectListFromFile();

        for (int i = 0; i < emergency.size(); i++) {
            if (emergency.get(i).getId() == evacuation.getId()) {
                emergency.set(i, evacuation);
                break;
            }
        }

        fileUtils.writeObjectListToFile(emergency);
    }

    @Override
    public List<EmergencyResourceDistribution> getPendingApprovals() {

        List<EmergencyResourceDistribution> emergency = fileUtils.readObjectListFromFile();
        emergency.removeIf(evacuation -> evacuation.getApproverId() != -1);

        return emergency;
    }

    @Override
    public void remove(int id) throws CannotWritetoFileException {

        List<EmergencyResourceDistribution> emergency = getAll();
        emergency.removeIf(evacuation -> evacuation.getId() == id);
        fileUtils.writeObjectListToFile(emergency);
    }

    @Override
    public EmergencyResourceDistribution getById(int id) {
        List<EmergencyResourceDistribution> emergency = getAll();
        for (EmergencyResourceDistribution evacuation : emergency) {
            if (evacuation.getId() == id) {
                return evacuation;
            }
        }
        return null;
    }
}
