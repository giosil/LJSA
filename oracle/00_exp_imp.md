# Import / Export procedures

## First check Oracle service

On Windows:

- `sc query OracleOraDb11g_home1TNSListener`
- `sc query OracleServiceORCL`
- `sc start OracleOraDb11g_home1TNSListener`
- `sc start OracleServiceORCL`

## Export

`exp LJSA/PASS123 FIlE=LJSA.dmp OWNER=LJSA STATISTICS=NONE`

## Import

`imp system/password@ORCL file=LJSA.dmp log=imp_LJSA.log fromuser=LJSA touser=LJSA`

## Connect to Oracle Instance

`sqlplus / AS SYSDBA`

### Some commands:

- `select * from all_users;`
- `connect LJSA;`
- `use LJSA`
- `select table_name from all_tables;`
- `desc ACTIVITIES;`
- `@/path/script.sql` or `start /path/script.sql`
