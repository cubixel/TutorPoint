package application.controller.services;

import application.AccountManager;
import application.controller.AccountLoginResult;
import application.model.account.Account;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.codec.digest.DigestUtils;

// extends the javafx implementation of multithreading (service)
// this should connect to the server, send over the user details and confirm
// the result
public class LoginService extends Service<AccountLoginResult> {

    Account account;
    AccountManager accountManager;

    public LoginService(Account account, AccountManager accountManager) {
        this.account = account;
        this.accountManager = accountManager;
    }

    private AccountLoginResult login(){
        /* Do login process */
        return null;
    }

    @Override
    protected Task<AccountLoginResult> createTask() {
        return null;
    }

    public String hashPassword(String password){
        String hash = DigestUtils.sha256Hex(password);
        System.out.println(hash);
        return hash;
    }
}

