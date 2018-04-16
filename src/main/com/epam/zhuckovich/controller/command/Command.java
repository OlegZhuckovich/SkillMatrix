package com.epam.zhuckovich.controller.command;

import javax.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface Command {
    void execute(HttpServletRequest request);
}
