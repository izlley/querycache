package com.skplanet.querycache.servlet;

import com.skplanet.querycache.server.CLIHandler;
import com.skplanet.querycache.server.util.RuntimeProfile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by nazgul33 on 11/19/14.
 */
public class QCStatusServlet extends HttpServlet {
    public QCStatusServlet() {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter writer = response.getWriter();
        RuntimeProfile rt = CLIHandler.gConnMgr.runtimeProfile;

        int nTotalReq = rt.getNumReq();
        int n10SecReq = rt.getNumReqPer10s();

        writer.println("<h1>Hello World</h1>");
        writer.printf("<h2>req.total : %d</h2>\n", nTotalReq);
        writer.printf("<h2>req.10sec : %d</h2>\n", n10SecReq);

        List<RuntimeProfile.QueryProfile> listRQ = rt.getRunningQueries();
        List<RuntimeProfile.QueryProfile> listCQ = rt.getCompleteQueries();

        // sort
        Comparator<RuntimeProfile.QueryProfile> qComparator = new Comparator<RuntimeProfile.QueryProfile>() {
            @Override
            public int compare(RuntimeProfile.QueryProfile q1, RuntimeProfile.QueryProfile q2) {
                return (int)(q1.startTime - q1.startTime);
            }
        };
        Collections.sort(listRQ, qComparator);
        Collections.sort(listCQ, qComparator);

        // write queries
        writer.println("<h2>Running queries</h2>\n");
        writer.println("<table><tr><th>id</th><th>type</th><th>user</th><th>statement</th><th>client ip</th><th>rows</th><th>startTime</th><th>endTime</th></tr>");
        for (RuntimeProfile.QueryProfile q : listRQ) {
            writer.printf("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%d</td><td>%d</td><td>%d</td></tr>",
                    q.queryId, q.connType, q.user, q.queryStr, q.clientIp, q.rowCnt, q.startTime, q.endTime);
        }
        writer.println("</table>");

        writer.println("<h2>Complete queries</h2>\n");
        writer.println("<table><tr><th>id</th><th>type</th><th>user</th><th>statement</th><th>client ip</th><th>rows</th><th>startTime</th><th>endTime</th></tr>");
        for (RuntimeProfile.QueryProfile q : listCQ) {
            writer.printf("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%d</td><td>%d</td><td>%d</td></tr>",
                    q.queryId, q.connType, q.user, q.queryStr, q.clientIp, q.rowCnt, q.startTime, q.endTime);
        }
        writer.println("</table>");
    }
    /*    public String queryId = "";
    public String connType = "";
    public String user = "";
    public QueryType queryType = null;
    public String queryStr = "";
    public String clientIp = "";
    public State stmtState = State.CLOSE;
    public long rowCnt = -1;
    public long startTime = 0;
    public long endTime = 0;
    // 0:exec/1:getmeta/2:fetch/3:stmtclose or
    // 1:getschemas/2:fetch
    public long[] timeHistogram = {0,0,0,0};
    public long[] execProfile   = null;
    public long[] fetchProfile  = {0,0,0,-1,-1,-1,-1,0,0,-1,-1};
*/
}
