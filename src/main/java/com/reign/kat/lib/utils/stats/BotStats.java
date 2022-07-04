package com.reign.kat.lib.utils.stats;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import de.vandermeer.asciitable.AsciiTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BotStats {
    private static final Logger log = LoggerFactory.getLogger(BotStats.class);

    public static final int HISTORY_SIZE = 10;
    private static final HashMap<Command, ArrayList<CommandExecution>> lastCommandExecutions = new HashMap<>();

    public BotStats(Command... commands)
    {
        addCommands(commands);
    }

    public static void addCommands(Command... commands)
    {
        for (Command cmd: commands)
        {
            log.debug("Monitoring timings for {}", cmd.getClass().getCanonicalName());
            lastCommandExecutions.put(cmd, new ArrayList<>());
        }
    }

    public static void addCommands(Collection<Command> commands)
    {
        for (Command cmd: commands)
        {
            log.debug("Monitoring timings for {}", cmd.getClass().getCanonicalName());
            lastCommandExecutions.put(cmd, new ArrayList<>());
        }
    }

    public static void addCommandExecutionStat(Command command, long timeTaken, CommandParameters input)
    {
        if(!lastCommandExecutions.containsKey(command)) { return; /* Not tracking this command */ }

        ArrayList<CommandExecution> commandHistory = lastCommandExecutions.get(command);
        if (commandHistory.size() > HISTORY_SIZE)
        {
            commandHistory.remove(0);
        }
        log.info("Command {} finished execution in {}ms", command.getPrimaryAlias(), timeTaken);
        CommandExecution ce = new CommandExecution(command, timeTaken, input);
        commandHistory.add(ce);
    }

    /**
     * Returns a HashMap of CommandExecutions for each specified command
     * the key being the Command, and the value being a CommandExecution
     * @param commands commands to fetch for.
     * @return HashMap of Command, CommandExecution
     */
    public static HashMap<Command, ArrayList<CommandExecution>> getCommandExecutionHistoryFor(Command... commands)
    {
        HashMap<Command, ArrayList<CommandExecution>> histories = new HashMap<>();
        for (Command command: commands)
        {
            if (lastCommandExecutions.containsKey(command))
            {
                histories.put(command, lastCommandExecutions.get(command));
            }
        }
        return histories;
    }

    public static HashMap<Command, Float> getAvgExecutionTimeFor(Command... commands)
    {
        HashMap<Command, Float> avgs = new HashMap<>();

        HashMap<Command, ArrayList<CommandExecution>> history = getCommandExecutionHistoryFor(commands);
        history.forEach((command, commandExecutions) -> {

            List<Float> individualTimings = commandExecutions.stream().map(ce -> ce.timeTaken).toList();
            float f = 0f;
            for(float i: individualTimings)
            {
                f += i;
            }

            f = f / individualTimings.size();

            avgs.put(command, f);
        });

        return avgs;
    }

    public static HashMap<Command, Float> getAvgExecutionTime() {
        return getAvgExecutionTimeFor(lastCommandExecutions.keySet().toArray(new Command[0]));
    }

    public static void reportToConsole()
    {
        log.info("BotStat Timings");
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Command", "Time (ms)", "Severity");
        for (Map.Entry<Command, Float> entry: getAvgExecutionTime().entrySet())
        {
            Command c = entry.getKey();
            Float f = entry.getValue();

            if (f.isNaN())
            {
                f = 0f;
            }

            String severity;
            if (f < 50) { severity = "NEGLIGIBLE"; }
            else if (f > 50 && f < 500) { severity = "MINOR"; }
            else if (f > 500 && f < 1000) { severity = "MAJOR"; }
            else { severity = "SEVERE"; }


            table.addRule();
            table.addRow(c.getClass().getSimpleName(), f, severity);

        }
        table.addRule();
        for (Iterator<String> it = table.renderAsIterator(); it.hasNext(); ) {
            String ln = it.next();
            log.info(ln);

        }
    }
}
