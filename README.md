### DATABASE HINT

Sarbbottam-Bandyopadhyays-MacBook-Pro:bin sarbbottam$ mysql -u root
mysql> create database tutorialdb;
mysql> create user 'tutorialuser'@'localhost' identified by 'tutorialuser';
mysql> grant all on tutorialdb.* to 'tutorialuser'@'localhost';
mysql> exit
Bye

Sarbbottam-Bandyopadhyays-MacBook-Pro:bin sarbbottam$ mysql -u tutorialuser -p
Enter password:

Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 1532
Server version: 5.5.9 MySQL Community Server (GPL)

Copyright (c) 2000, 2010, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.
mysql> 

https://apple.stackexchange.com/questions/12322/how-to-create-new-mysql-user-db-in-mac-os-x
https://spring.io/guides/gs/accessing-data-mysql/