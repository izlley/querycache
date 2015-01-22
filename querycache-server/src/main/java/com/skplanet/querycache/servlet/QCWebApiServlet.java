package com.skplanet.querycache.servlet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skplanet.querycache.server.CLIHandler;
import com.skplanet.querycache.server.ConnMgr;
import com.skplanet.querycache.server.util.ObjectPool;
import com.skplanet.querycache.server.util.ObjectPool.Profile;
import com.skplanet.querycache.server.util.RuntimeProfile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by nazgul33 on 15. 1. 16.
 */
public class QCWebApiServlet extends HttpServlet {
    public class ConDesc {
        public String driver;
        public int free;
        public int using;
        public ConDesc(String driver, int free, int using) {
            this.driver = driver;
            this.free = free;
            this.using = using;
        }
    }

    public QCWebApiServlet() {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter writer = response.getWriter();
        RuntimeProfile rt = CLIHandler.gConnMgr.runtimeProfile;

        // path excluding prefix e.g.) http://localhost:8080/api/apitest?arg1=blah -> apitest
        String path = request.getRequestURI().substring(request.getContextPath().length());

        Gson gson = new Gson();
        switch (path) {
            case "/queries": {
                List<RuntimeProfile.QueryProfile> listRQ = rt.getRunningQueries();
                List<RuntimeProfile.QueryProfile> listCQ = rt.getCompleteQueries();

                Type qpListType = new TypeToken<List<RuntimeProfile.QueryProfile>>(){}.getType();
                String rq = gson.toJson(listRQ, qpListType);
                String cq = gson.toJson(listCQ, qpListType);

                writer.printf("{\"runningQueries\":%s, \"completeQueries\":%s}", rq, cq);
                break;
            }

            case "/connections": {
                List<ConnMgr.ConnMgrofOne> conMgrs = new ArrayList<ConnMgr.ConnMgrofOne>(CLIHandler.gConnMgr.getConnMgrofAll().values());
                List<ConDesc> lCD = new ArrayList<ConDesc>(conMgrs.size());
                for (ConnMgr.ConnMgrofOne mgr:conMgrs) {
                    ConDesc cd = new ConDesc(mgr.connType, mgr.getFreeConnCount(), mgr.getUsingConnCount());
                    lCD.add(cd);
                }
                writer.print(gson.toJson(lCD));
                break;
            }

            case "/objectpool": {
                ObjectPool pool = CLIHandler.getObjPool();
                Profile profile = pool.new Profile();
                writer.print(gson.toJson(profile));
                break;
            }
        }
    }
}
