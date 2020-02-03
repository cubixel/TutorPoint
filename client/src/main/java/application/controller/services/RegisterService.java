package application.controller.services;

import application.AccountManager;
import application.model.account.Account;
import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;

public class RegisterService extends Service<AccountRegisterResult> {

    Account account;
    AccountManager accountManager;
    MainConnection connection;

    public RegisterService(Account account, AccountManager accountManager, MainConnection connection) {
        this.account = account;
        this.accountManager = accountManager;
        this.connection = connection;
    }

    private AccountRegisterResult register() {
        // check username no other users
        // wait for response from server as to success of registering
        // respond with AccountRegisterResults for each case.
        String jsonAccount = connection.packageClass(this.account);
        try {
            connection.sendString(jsonAccount);
            String serverReply = connection.listenForString();
            return new Gson().fromJson(serverReply, AccountRegisterResult.class);
        } catch (IOException e){
            e.printStackTrace();
            return AccountRegisterResult.FAILED_BY_NETWORK;
        }catch (Exception e){
            e.printStackTrace();
            return  AccountRegisterResult.FAILED_BY_UNEXPECTED_ERROR;
        }
    }

    @Override
    protected Task<AccountRegisterResult> createTask() {
        return new Task<AccountRegisterResult>() {
            @Override
            protected AccountRegisterResult call() throws Exception {
                return register();
            }
        };
    }


}
