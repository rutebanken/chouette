FROM jboss/wildfly:8.2.1.Final

USER root
RUN yum -y update && yum -y install wget && yum clean all
USER jboss

RUN mkdir /opt/jboss/wildfly/customization/

#Copy iev.properties
COPY docker/files/wildfly/iev.properties /etc/chouette/iev/

RUN touch /opt/jboss/wildfly/build.log
RUN chmod a+w /opt/jboss/wildfly/build.log

# Deploying by copying to deployment directory
COPY chouette_iev/target/chouette.ear /opt/jboss/wildfly/standalone/deployments/

# Copy standalone customizations
COPY docker/files/wildfly/standalone.conf /opt/jboss/wildfly/bin
# From http://stackoverflow.com/questions/20965737/docker-jboss7-war-commit-server-boot-failed-in-an-unrecoverable-manner
RUN rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history \
  && mkdir -p /opt/jboss/data \
  && chown jboss:jboss /opt/jboss/data

# Configuration of Prometheus agent
RUN  mkdir -p /opt/jboss/wildfly/prometheus && chown jboss:jboss /opt/jboss/wildfly/prometheus
COPY docker/lib/jmx_prometheus_javaagent-0.12.0.jar /opt/jboss/wildfly/prometheus/jmx_prometheus_javaagent.jar
COPY docker/files/jmx_exporter_config.yml /opt/jboss/wildfly/prometheus

EXPOSE 8778 9779

# Running as root, in order to get mounted volume writable:
USER root

COPY docker/files/disk_usage_notifier.sh /disk_usage_notifier.sh
RUN chmod a+x /disk_usage_notifier.sh

COPY docker/files/disk_usage_notifier.sh /disk_usage_notifier.sh






# This argument comes from https://github.com/jboss-dockerfiles/wildfly
# It enables the admin interface.

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "--read-only-server-config=standalone.xml"]
