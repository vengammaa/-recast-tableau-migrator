FROM openjdk
COPY target/*.jar recast-tableau-migrator.jar 
EXPOSE 9087
ENTRYPOINT ["java","-jar","/recast-tableau-migrator.jar"]
