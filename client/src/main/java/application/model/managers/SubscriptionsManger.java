package application.model.managers;

import application.model.requests.SubjectRequestSubscription;

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
