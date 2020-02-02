package application.controller.Services;

import application.controller.AccountLoginResult;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LoginService extends Service<AccountLoginResult> {
    @Override
    protected Task<AccountLoginResult> createTask() {
        return null;
    }
}
