package kr.neolab.sdk.pen.bluetooth.cmd;

import java.util.LinkedHashMap;
import kr.neolab.sdk.util.NLog;

/**
 * @author CHY
 *
 */
public class CommandManager 
{
    public LinkedHashMap<Integer, ICommand> commands = new LinkedHashMap<Integer, ICommand>();
    
    public void execute(ICommand command) 
    {
        if ( !commands.containsKey(command.getId()) )
        {
            commands.put(command.getId(), command);
            command.excute();
            return;
        }

        if ( commands.get(command.getId()).isAlive() )
        {
            NLog.e("[CommandManager] Command is still excuting.");
            command = null;
            return;
        }
        
        commands.remove(command.getId());
        commands.put(command.getId(), command);
        command.excute();
    }
    
    public void kill(int key) 
    {
        ICommand command = commands.get(key);
        
        if ( command != null )
        {
            command.finish();
        }
    }
}
