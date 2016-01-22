package filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by lijun on 16/1/1.
 */
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        String testParam = filterConfig.getInitParameter("test-param");

        System.out.println("testParam:" + testParam);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String ipAddress = servletRequest.getRemoteAddr();

        System.out.println("IP: "+ ipAddress + ", Time: "
                + new Date().toString());

        filterChain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {

    }
}
