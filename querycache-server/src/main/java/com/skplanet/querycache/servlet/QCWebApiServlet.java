package com.skplanet.querycache.servlet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skplanet.querycache.server.CLIHandler;
import com.skplanet.querycache.server.ConnMgrCollection;
import com.skplanet.querycache.server.QueryCacheServer;
import com.skplanet.querycache.server.util.ObjectPool;
import com.skplanet.querycache.server.util.ObjectPool.Profile;
import com.skplanet.querycache.server.util.RuntimeProfile;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by nazgul33 on 15. 1. 16.
 */
@WebServlet(asyncSupported = true)
public class QCWebApiServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(QCWebApiServlet.class);
    private static final String ASYNC_REQ_ATTR = QCWebApiServlet.class.getName() + ".async";
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

    // the info from java.lang.Runtime is specific to current JVM
    // system info should be obtained seperately
    public class RuntimeInfo {
        int nProcessors;
        long memFree;
        long memTotal;
        long memMax;

        public RuntimeInfo(Runtime runtime) {
            this.nProcessors = runtime.availableProcessors();
            this.memFree = runtime.freeMemory();
            this.memTotal = runtime.totalMemory();
            this.memMax = runtime.maxMemory();
            if (this.memMax == Long.MAX_VALUE) {
                this.memMax = -1;
            }
        }
    }

    public class ThreadInfo {
        int webServerThreads;
        int webServerThreadsIdle;
        int handlerThreads;
        int handlerThreadsIdle;
        int thriftServerThreads;
        int thriftServerThreadsIdle;
        int totalThreads;

        public ThreadInfo() {
            if (QueryCacheServer.webServer != null) {
                webServerThreads = QueryCacheServer.webServer.getThreadPool().getThreads();
                webServerThreadsIdle = QueryCacheServer.webServer.getThreadPool().getIdleThreads();
            }

            ThreadGroup rootGroup = Thread.currentThread( ).getThreadGroup( );
            ThreadGroup parentGroup;
            while ( ( parentGroup = rootGroup.getParent() ) != null ) {
                rootGroup = parentGroup;
            }
            totalThreads = rootGroup.activeCount();

            handlerThreads = CLIHandler.getThreadPoolSize();
            handlerThreadsIdle = CLIHandler.getThreadPoolSize() - CLIHandler.getThreadPoolActiveCount();
            if (QueryCacheServer.qcServerExecutorService == null) {
                thriftServerThreads = 0;
                thriftServerThreadsIdle = 0;
            } else {
                thriftServerThreads = QueryCacheServer.qcServerExecutorService.getPoolSize();
                thriftServerThreadsIdle = thriftServerThreads - QueryCacheServer.qcServerExecutorService.getActiveCount();
            }
        }
    }

    public class SystemInfo {
        double loadSystem;
        double loadProcess;
        long memPhysFree;
        long memPhysTotal;
        long swapFree;
        long swapTotal;

        public SystemInfo(OperatingSystemMXBean bean) {
            loadSystem = bean.getSystemCpuLoad();
            loadProcess = bean.getProcessCpuLoad();
            memPhysFree = bean.getFreePhysicalMemorySize();
            memPhysTotal = bean.getTotalPhysicalMemorySize();
            swapFree = bean.getFreeSwapSpaceSize();
            swapTotal = bean.getTotalSwapSpaceSize();
        }
    }

    public QCWebApiServlet() {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        RuntimeProfile rt = CLIHandler.gConnMgrs.runtimeProfile;

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

                response.setContentType("application/json; charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                writer.printf("{\"runningQueries\":%s, \"completeQueries\":%s}", rq, cq);
                break;
            }

            case "/connections": {
                List<ConnMgrCollection.ConnMgr> conMgrs = new ArrayList<ConnMgrCollection.ConnMgr>(CLIHandler.gConnMgrs.getConnMgrMap().values());
                List<ConDesc> lCD = new ArrayList<ConDesc>(conMgrs.size());
                for (ConnMgrCollection.ConnMgr mgr:conMgrs) {
                    ConDesc cd = new ConDesc(mgr.connType, mgr.getFreeConnCount(), mgr.getUsingConnCount());
                    lCD.add(cd);
                }
                response.setContentType("application/json; charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                writer.print(gson.toJson(lCD));
                break;
            }

            case "/objectpool": {
                ObjectPool pool = CLIHandler.getObjPool();
                Profile profile = pool.new Profile();
                response.setContentType("application/json; charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                writer.print(gson.toJson(profile));
                break;
            }

            case "/system": {
                RuntimeInfo runtime = new RuntimeInfo(Runtime.getRuntime());
                SystemInfo system = new SystemInfo((OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean());
                ThreadInfo threads = new ThreadInfo();
                response.setContentType("application/json; charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                writer.printf("{\"jvm\":%s,\"system\":%s,\"threads\":%s}", gson.toJson(runtime), gson.toJson(system), gson.toJson(threads));
                break;
            }

            case "/hostname": {
                response.setContentType("application/json; charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                writer.print(gson.toJson(QueryCacheServer.hostname));
                break;
            }

            case "/cancelQuery": {
                // if async marker is not set, check update time and initiate async
                if ( request.getAttribute(ASYNC_REQ_ATTR) == null ) {
                    final String qId = request.getParameter("id");
                    final String driver = request.getParameter("driver");

                    if (qId != null && qId.length() > 0 && driver != null && driver.length() > 0) {
                        LOG.debug("Starting cancel async job. " + qId);
                        final AsyncContext async = request.startAsync();
                        final Boolean asyncYes = new Boolean(true);
                        request.setAttribute(ASYNC_REQ_ATTR, asyncYes);
                        async.setTimeout(30000);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                CLIHandler ch = CLIHandler.getInstance();
                                LOG.debug("calling cancel " + qId);
                                ch.internalCancelStatement(qId, driver);
                                async.dispatch();

                            }
                        }).start();
                    }
                    else {
                        LOG.debug("cancel finished." + qId);
                        response.setContentType("application/json; charset=utf-8");
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        writer.printf("{\"result\":\"%s\", \"msg\":\"%s\"}", "error", "invalid query specifier");
                    }
                }
                else {
                    response.setContentType("application/json; charset=utf-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                    writer.printf("{\"result\":\"%s\", \"msg\":\"%s\"}", "ok", "successful");
                }
                break;
            }
        }
    }
}
