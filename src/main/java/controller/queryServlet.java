package controller;

import model.CourseWare;
import service.CourseService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by lijun on 16/1/1.
 */
@WebServlet("/query")
public class queryServlet extends HttpServlet {

    private CourseService courseService;

    @Override
    public void init() throws ServletException {
        courseService = new CourseService();
    }

    /**
     * 处理检索GET请求
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<CourseWare> wareList;

        String param = (String) req.getParameter("zjz");
        System.out.println("zjz is:" + param);
        if (param == null){
            wareList = courseService.getAllCourseList();
        }else {
            wareList = courseService.getCourseListByCourseName(param);
//            wareList = courseService.getCourseListByCourseNameGAG(param);
        }

        System.out.println("servlet ----> list size():" + wareList.size());
        req.setAttribute("wareList", wareList);
        req.getRequestDispatcher("query.jsp").forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
