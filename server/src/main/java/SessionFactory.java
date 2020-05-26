public class SessionFactory {
  public SessionFactory() {
  }

  public Session createSession(int sessionID, ClientHandler clientHandler) {
    return new Session(sessionID, clientHandler);
  }
}