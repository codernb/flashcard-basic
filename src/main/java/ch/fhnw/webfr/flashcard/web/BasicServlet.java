package ch.fhnw.webfr.flashcard.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ch.fhnw.webfr.flashcard.domain.Questionnaire;
import ch.fhnw.webfr.flashcard.persistence.QuestionnaireRepository;
import ch.fhnw.webfr.flashcard.util.QuestionnaireInitializer;

@SuppressWarnings("serial")
public class BasicServlet extends HttpServlet {

	Logger logger = Logger.getLogger(BasicServlet.class);
	/*
	 * Attention: This repository will be used by all clients, concurrency could
	 * be a problem. THIS VERSION IS NOT PRODUCTION READY!
	 */
	private QuestionnaireRepository questionnaireRepository;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html; charset=utf-8");

		String[] pathElements = request.getRequestURI().split("/");

		Optional<Integer> idOptional = getIdOptional(pathElements);

		if (idOptional.isPresent()) {
			handleQuestionnairesRequest(request, response, idOptional.get());
		} else if (isLastPathElementQuestionnaires(pathElements)) {
			handleQuestionnairesRequest(request, response);
		} else {
			handleIndexRequest(request, response);
		}
	}

	private boolean isLastPathElementQuestionnaires(String[] pathElements) {
		String last = pathElements[pathElements.length - 1];
		return last.equals("questionnaires");
	}

	private Optional<Integer> getIdOptional(String[] pathElements) {
		String last = pathElements[pathElements.length - 1];
		try {
			return Optional.of(Integer.parseInt(last));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	private void handleQuestionnairesRequest(HttpServletRequest request, HttpServletResponse response, int id)
			throws IOException {
		List<Questionnaire> questionnaires = new ArrayList<>();
		questionnaires.add(questionnaireRepository.findById((long) id));
		handleQuestionnairesRequest(request, response, questionnaires);
	}

	private void handleQuestionnairesRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		List<Questionnaire> questionnaires = questionnaireRepository.findAll();
		handleQuestionnairesRequest(request, response, questionnaires);
	}

	private void handleQuestionnairesRequest(HttpServletRequest request, HttpServletResponse response,
			List<Questionnaire> questionnaires) throws IOException {
		PrintWriter writer = response.getWriter();
		writer.append("<html><head><title>Example</title></head><body>");
		writer.append("<h3>Fragebögen</h3>");
		for (Questionnaire questionnaire : questionnaires) {
			String url = request.getContextPath() + request.getServletPath();
			url = url + "/questionnaires/" + questionnaire.getId().toString();
			writer.append("<p><a href='" + response.encodeURL(url) + "'>" + questionnaire.getTitle() + "</a></p>");
		}
		writer.append("</body></html>");
	}

	private void handleIndexRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter writer = response.getWriter();
		writer.append("<html><head><title>Example</title></head><body>");
		writer.append("<h3>Welcome</h3>");
		String url = request.getContextPath() + request.getServletPath();
		writer.append("<p><a href='" + response.encodeURL(url) + "/questionnaires'>All Questionnaires</a></p>");
		writer.append("</body></html>");
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		questionnaireRepository = new QuestionnaireInitializer().initRepoWithTestData();
	}

}
