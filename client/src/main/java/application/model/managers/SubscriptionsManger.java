package application.model.managers;

/**
 * A SubscriptionManager contains all the information needed
 * to manage associated relevant subjects based on a reference
 * subject used to offer the user suggestions on new topics to follow.
 *
 * @author James Gardner
 * @see SubjectManager
 */
public class SubscriptionsManger {
  private String referenceSubject;
  private int numberOfSubjectsBeforeRequest;
  private final SubjectManager subjectManagerRecommendations;

  public SubscriptionsManger() {
    this.subjectManagerRecommendations = new SubjectManager();
  }

  public String getReferenceSubject() {
    return referenceSubject;
  }

  public void setReferenceSubject(String referenceSubject) {
    this.referenceSubject = referenceSubject;
  }

  public int getNumberOfSubjectsBeforeRequest() {
    return numberOfSubjectsBeforeRequest;
  }

  public void setNumberOfSubjectsBeforeRequest() {
    numberOfSubjectsBeforeRequest = subjectManagerRecommendations.getNumberOfSubjects();
  }

  public SubjectManager getSubjectManagerRecommendations() {
    return subjectManagerRecommendations;
  }
}