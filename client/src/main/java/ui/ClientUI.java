package ui;

import exception.ResponseException;

public interface ClientUI {
    ClientResult eval(String arg) throws ResponseException;
}
