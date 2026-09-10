#Recipe-Website-Testing

Instructions for Phase-03

1. For TestNG testcases (SignUp, Login, Home) run the resources/testng.xml file
2. for the rest of the testcases (JUnit) run the java/JunitTests.java

Instructions for Phase - 04 

1. For TestNG testcases , I have added a TestNG Listener
2. For JUnit testcases , I have added a JUnit Listener
3. Both the listeners have one common screenshot capturing control that would be handled by screenshot util which are saves in Test Screenshots Folder
4. These Screenshots are further populated to HTML Report util under utils and the Report is created outside of the utils package
5. The HTML and Screenshots are still not working properly , thus added clear console stack tracing that would be further populated as a Jira Report
   
