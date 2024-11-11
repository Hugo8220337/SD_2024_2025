package ipp.estg.commands;

import java.util.Map;

/**
 *
 *
 * Fui buscar esta classe aqui: https://stackoverflow.com/questions/126409/eliminating-switch-statements
 */
public class CommandHandler {
    private Map<Integer, Command> commandMap; // injected in, or obtained from a factory

    public void handleCommand(int action) {
        Command command = commandMap.get(action);
        command.execute();
    }
}