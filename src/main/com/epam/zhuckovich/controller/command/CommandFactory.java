package com.epam.zhuckovich.controller.command;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CommandFactory {

    private static final Logger LOGGER = LogManager.getLogger(CommandFactory.class.getName());

    public static Command defineCommand(String commandValue, HttpServletRequest request, HttpServletResponse response){
        Command command = null;
        CommandType commandType = null;
        try {
            commandType = CommandType.valueOf(commandValue);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.ERROR, "IllegalArgumentException was occurred during definition of the command");
        }
        if (commandType != null) {
            switch (commandType) {
                case ADD_DATA:
                    CommandType addDataCommand = commandType;
                    command = httpRequest -> new ActionCommand().changeList(request,response, addDataCommand);
                    break;
                case DELETE_DATA:
                    CommandType deleteDataCommand = commandType;
                    command = httpRequest -> new ActionCommand().changeList(request, response, deleteDataCommand);
                    break;
                case EDIT_DATA:
                    CommandType editDataCommand = commandType;
                    command = httpRequest -> new ActionCommand().changeList(request,response,editDataCommand);
                    break;
                case GET_DATA:
                    command = httpRequest -> new ActionCommand().readExcelFile(request, response);
                    break;
            }
        }
        return command;
    }
}
