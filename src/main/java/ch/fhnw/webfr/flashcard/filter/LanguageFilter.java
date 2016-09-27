package ch.fhnw.webfr.flashcard.filter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class LanguageFilter implements Filter {

	private Map<String, String> translations = new HashMap<>();
	private String language;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String fileName = filterConfig.getInitParameter("languageFile");
		if (fileName == null)
			return;
		language = filterConfig.getInitParameter("language");
		File file = new File(getClass().getClassLoader().getResource(fileName).getFile());
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String[] line = scanner.nextLine().split(":");
				translations.put(line[0].trim(), line[1].trim());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		PrintWriter printWriter = response.getWriter();
		HttpServletResponseWrapper httpServletResponseWrapper = new HttpServletResponseWrapper(
				(HttpServletResponse) response);
		chain.doFilter(request, httpServletResponseWrapper);
		String body = httpServletResponseWrapper.getBody();
		if (language.equals("en"))
			for (Entry<String, String> entry : translations.entrySet())
				body = body.replaceAll(entry.getKey(), entry.getValue());
		else if (language.equals("de"))
			for (Entry<String, String> entry : translations.entrySet())
				body = body.replaceAll(entry.getValue(), entry.getKey());
		printWriter.append(body);
	}

	@Override
	public void destroy() {
		;
	}

	private class HttpServletResponseWrapper extends javax.servlet.http.HttpServletResponseWrapper {

		private ByteArrayOutputStream byteArrayOutputStream;
		private PrintWriter printWriter;

		public HttpServletResponseWrapper(HttpServletResponse response) {
			super(response);
			byteArrayOutputStream = new ByteArrayOutputStream(response.getBufferSize());
			printWriter = new PrintWriter(byteArrayOutputStream);
		}

		public String getBody() throws IOException {
			printWriter.close();
			return byteArrayOutputStream.toString();
		}

		@Override
		public PrintWriter getWriter() {
			return printWriter;
		}

	}

}
