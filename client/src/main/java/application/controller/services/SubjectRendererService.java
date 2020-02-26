package application.controller.services;

import application.model.managers.SubjectManager;
import com.google.gson.Gson;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;

public class SubjectRendererService extends Service<Void> {

    private MainConnection connection;
    private SubjectManager subjectManager;

    public SubjectRendererService(MainConnection connection){
        this.connection = connection;
        subjectManager = new SubjectManager();
    }

    private void fetchSubjects(){
        try {
            connection.sendString(connection.packageClass(this.subjectManager));
            String serverReply = connection.listenForString();
            //return new Gson().fromJson(serverReply, AccountRegisterResult.class);
        } catch (IOException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    fetchSubjects();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
