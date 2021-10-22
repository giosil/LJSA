# Import / Export procedures

## First check MySQL service

On Linux:

- `service mysql status`
- `service mysql start`

On Windows:

- `sc query MySQL56`
- `sc start MySQL56`

## Export

- `mysqldump -u user -p[password] [database] > file`
- `mysqldump --user=user --password[=password] [database] > file`

Example (add --routines to export functions and procedures):

`mysqldump --routines --user=root --password ljsa > C:\prj\dew\ljsa\mysql\ljsa_dump.sql`

## Import (after create database: 01_setup.sql)

- `mysql --user=user --password[=password] [database] < file`

Example:

`mysql -u root -p ljsa < C:\prj\dew\ljsa\mysql\ljsa_dump.sql`

## Connect to mysql

`mysql --user=root --password[=password] [database]`

### Some commands:

- `show databases;`
- `select database() from dual;`
- `use ljsa`
- `show tables;`
- `show full tables in ljsa where table_type like 'VIEW';`
- `show triggers;`
- `show function status where db='ljsa';`
- `show procedure status where db='ljsa';`
- `source C:/prj/dew/ljsa/mysql/05_data.sql;`