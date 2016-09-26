package ch.fhnw.webfr.flashcard.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

@WebFilter(servletNames = "BasicServlet")
public class LanguageFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		PrintWriter printWriter = response.getWriter();
		HttpServletResponseWrapper httpServletResponseWrapper = new HttpServletResponseWrapper(
				(HttpServletResponse) response);
		chain.doFilter(request, httpServletResponseWrapper);
		String body = httpServletResponseWrapper.getBody();
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
