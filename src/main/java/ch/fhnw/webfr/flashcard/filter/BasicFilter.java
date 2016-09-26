package ch.fhnw.webfr.flashcard.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter(filterName = "BasicFilter", servletNames = "BasicServlet")
public class BasicFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println(((HttpServletRequest) request).getRequestURL());
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		;
	}

}
