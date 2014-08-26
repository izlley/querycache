
-- create table query for Impala and Hive
CREATE EXTERNAL TABLE admin.audit_query_qc (                                               
  log_date STRING,                                                                    
  hostname STRING,                                                                  
  user STRING,                                                                    
  query_id STRING,                                                                       
  query_type STRING,                                                                    
  query_str STRING,                                                                  
  stmt_state STRING,                                                                      
  rowcnt INT,
  start_time STRING,                                                                   
  end_time STRING,                                                                     
  time_histogram STRING,                                                                         
  total_elapsedtime INT
)                                                                                        
PARTITIONED BY (                                                                         
  part_date STRING                                                                      
)                                                                                        
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'                  
WITH SERDEPROPERTIES ('line.delim'='\n', 'field.delim'='\t', 'serialization.format'='\t')
STORED AS TEXTFILE                                                                       
LOCATION 'hdfs://skpds/data/admin/tsv/qc-query-audit';                                

ALTER TABLE admin.audit_query_qc ADD PARTITION (part_date='20140826');
