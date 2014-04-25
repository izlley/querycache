package com.skplanet.querycache.server;

public class QCConfigKeys {
  public static final String  QC_SERVER_PORT = "qc.server.port";
  public static final int     QC_SERVER_PORT_DEFAULT = 8655;
  public static final String  QC_WORKERTHREAD_MIN = "qc.workerthread.min";
  public static final int     QC_WORKERTHREAD_MIN_DEFAULT = 16;
  public static final String  QC_WORKERTHREAD_MAX = "qc.workerthread.max";
  public static final int     QC_WORKERTHREAD_MAX_DEFAULT = 64;
  public static final String  QC_CONNECTIONPOOL_FREE_INIT_SIZE = "qc.connpool.free.init.size";
  public static final int     QC_CONNECTIONPOOL_FREE_INIT_SIZE_DEFAULT = 16;
  public static final String  QC_CONNECTIONPOOL_FREE_RESIZING_F = "qc.connpool.free.resizing.factor";
  public static final float   QC_CONNECTIONPOOL_FREE_RESIZING_F_DEFAULT = 0.2f;
  public static final String  QC_QUERY_PROFILING = "qc.query.profiling";
  public static final boolean QC_QUERY_PROFILING_DEFAULT = true;
  public static final String  QC_OBJECTPOOL_MAX_SIZE = "qc.objpool.max.size";
  public static final int     QC_OBJECTPOOL_MAX_SIZE_DEFAULT = 1024 * 1024;
  public static final String  QC_OBJECTPOOL_RESIZING_CYCLE_MILLI = "qc.objpool.reload.cycle.milli";
  public static final long    QC_OBJECTPOOL_RESIZING_CYCLE_MILLI_DEFAULT = 15 * 1000;
  public static final String  QC_OBJECTPOOL_RESIZING_F = "qc.objpool.resizing.factor";
  public static final float   QC_OBJECTPOOL_RESIZING_F_DEFAULT = 0.2f;
  public static final String  QC_STORAGE_DRIVERS = "qc.storage.jdbc.drivers";
  public static final String  QC_STORAGE_DRIVERS_PATH = "qc.storage.jdbc.drivers.path";
  public static final String  QC_STORAGE_DRIVERS_PATH_DEFAULT = "../lib/drivers";
  public static final String  QC_STORAGE_DRIVERS_ADDRESS_PREFIX = "qc.storage.jdbc.drivers.address";
  public static final String  QC_STORAGE_DRIVERS_USER_PREFIX = "qc.storage.jdbc.drivers.user";
  public static final String  QC_STORAGE_DRIVERS_PASSWORD_PREFIX = "qc.storage.jdbc.drivers.password";
}
