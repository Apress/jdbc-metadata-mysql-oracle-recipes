Hello Reader,

This archive contains the example source code (and
compiled .class files) for "JDBC Metadata, MySQL, and 
Oracle Recipes: A Problem-Solution Approach", first 
edition, by Mahmoud Parsian.

1. I believe in "free" software and I do support 
   The Free Software Foundation's principle: 
   Free software is a matter of liberty not price. 
   You should think of "free" as in "free speech"

2. All of the code in this package/zip has been
   written by Mahmoud Parsian (unless otherwise 
   it is noted); each chapter has source code for 
   that chapter. Please feel free to cut-and-paste 
   my code in your JDBC applications.  You do not 
   need to mention my name.

3. The packages directory contains packages used by 
   the author for creating client programs; to compile 
   these, go to specific directories and use "javac" 
   (Java compiler). You may use JDK 1.4.2 or JDK 1.5 
   for compiling provided programs (I do recommend using
   JDK 1.5).
   
4. I have included some extra programs in the "jcb" 
   package, which are not included in the book. For 
   compiling these extra programs, you need the 
   following JAR files in your CLASSPATH. When including 
   .jar files in your CLASSPATH, you must use the full
   file name:
   
   Windows: set CLASSPATH=%CLASSPATH%;c:\myjars\jdom.jar;...
   
   Linux: export CLASSPATH=$CLASSPATH:/home/me/myjars/jdom.jar:...

   JAR File                      From Source   
   --------                  ----------------------------
   dt.jar                    JDK installation
   tools.jar                 JDK installation
   jdom.jar                  www.jdom.org
   mysql.jar                 http://dev.mysql.com/downloads
   ojdbc14.jar               www.oracle.com
   servlet-api.jar           Apache's Tomcat Lib
   commons-digester-1.7.jar  Apache's Common Digester
   xercesImpl.jar            Apache's Xerces
   xml-apis.jar              Apache's Xerces
   resolver.jar              Apache's Xerces
   xalan.jar                 Apache's Xalan
   serializer.jar            Apache's Xalan


5. Make sure to use proper/valid Driver and database 
   user/password for provided programs.
   
6. Make sure to include MySQL's/Oracle's driver package 
   (JDBC Driver .jar files) in your CLASSPATH (environment 
   variable).
   
7. NOTE: do not install these examples in a folder whose 
   path name contains spaces.

8. If you use my code for production systems, please make 
   sure that you replace getConnection() method with a 
   "production quality" pool management system such as
   Apache's Excalibur (http://excalibur.apache.org/).

9. If you encounter any problems in compiling/running 
   programs, please let me know by sending email to: 
   admin@jdbccookbook.com

10. Happy reading!

Best regards,
Mahmoud Parsian, Ph.D.
Principal Software Engineer
admin@jdbccookbook.com