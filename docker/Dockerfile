# websphere
#FROM websphere-liberty
#COPY server/postgresql-42.2.4.jar /opt/ibm/wlp/usr/shared/resources/
#open liberty
FROM open-liberty:javaee8
COPY server/postgresql-42.2.4.jar /opt/ol/wlp/usr/shared/resources/

COPY server/server.xml /config/
ADD javaee-cafe.war /config/dropins/javaee-cafe.war