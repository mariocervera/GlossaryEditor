package es.cv.gvcase.ide.redmine.samples;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.redmine.ta.AuthenticationException;
import org.redmine.ta.NotFoundException;
import org.redmine.ta.RedmineException;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.RedmineManager.INCLUDE;
import org.redmine.ta.beans.Issue;
import org.redmine.ta.beans.IssueRelation;
import org.redmine.ta.beans.Project;
import org.redmine.ta.beans.SavedQuery;
import org.redmine.ta.beans.TimeEntry;
import org.redmine.ta.beans.User;
import org.redmine.ta.internal.RedmineXMLGenerator;

public class Sample {
	private static String redmineHost = "https://moskitt.gva.es/redmine";
	private static String apiAccessKey = "";
	private static String projectKey = "moskittbase";

	private static Integer queryId = null; // any

	public static void main(String[] args) {
		// RedmineManager mgr = new RedmineManager(redmineHost, login,
		// password);
		RedmineManager mgr = new RedmineManager(redmineHost, apiAccessKey);
		try {
			// getIssueWithRelations(mgr);
			// tryCreateIssue(mgr);
			// tryGetIssues(mgr);
			// tryGetAllIssues(mgr);
			// printCurrentUser(mgr);
			// generateXMLForUser();
			// generateXMLForTimeEntry();
			// getSavedQueries(mgr);
			getProjects(mgr);
			// tryCreateRelation(mgr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void tryCreateRelation(RedmineManager mgr)
			throws IOException, AuthenticationException, NotFoundException,
			RedmineException {
		IssueRelation r = mgr.createRelation(projectKey, 49, 50,
				IssueRelation.TYPE.precedes.toString());
		System.out.println(r);
	}

	private static void getProjects(RedmineManager mgr) throws IOException,
			AuthenticationException, RedmineException {
		List<Project> projects = mgr.getProjects();
		System.out.println(projects);

	}

	private static void getSavedQueries(RedmineManager mgr) throws IOException,
			AuthenticationException, NotFoundException, RedmineException {
		List<SavedQuery> savedQueries = mgr.getSavedQueries("test");
		System.out.println(savedQueries);

	}

	private static void getIssueWithRelations(RedmineManager mgr)
			throws IOException, AuthenticationException, NotFoundException,
			RedmineException {
		Issue issue = mgr.getIssueById(24580, INCLUDE.relations);
		List<IssueRelation> r = issue.getRelations();
		System.out.println(r);

	}

	private static void tryCreateIssue(RedmineManager mgr) throws IOException,
			AuthenticationException, NotFoundException, RedmineException {
		Issue issue = new Issue();
		issue.setSubject("test123");
		mgr.createIssue(projectKey, issue);
	}

	private static void generateXMLForTimeEntry() {
		TimeEntry o = new TimeEntry();
		o.setId(13);
		o.setIssueId(45);
		o.setActivityId(3);
		o.setProjectId(55);
		o.setUserId(66);
		o.setHours(123f);
		o.setComment("text here");
		o.setSpentOn(new Date());
		String xml = RedmineXMLGenerator.toXML(o);
		System.out.println(xml);
	}

	private static void generateXMLForUser() {
		User u = new User();
		u.setLogin("newlogin");
		String xml = RedmineXMLGenerator.toXML(u);
		System.out.println(xml);

	}

	private static void tryGetIssues(RedmineManager mgr) throws Exception {
		List<Issue> issues = mgr.getIssues(projectKey, queryId);
		for (Issue issue : issues) {
			System.out.println(issue.toString());
		}
	}

	private static void tryGetAllIssues(RedmineManager mgr) throws Exception {
		List<Issue> issues = mgr.getIssues(projectKey, null);
		for (Issue issue : issues) {
			System.out.println(issue.toString());
		}
	}

	private static void printCurrentUser(RedmineManager mgr) throws Exception {
		User currentUser = mgr.getCurrentUser();
		System.out.println("user=" + currentUser.getMail());

		currentUser.setMail("ne@com123.com");
		mgr.updateUser(currentUser);
		System.out.println("updated user");

		User currentUser2 = mgr.getCurrentUser();
		System.out.println("updated user's mail: " + currentUser2.getMail());

	}
}
