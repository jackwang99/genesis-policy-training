ARG  GENESIS_BASE_IMAGES=suzeisnexus03.exigengroup.com:5000/genesis-base:1.4.3
FROM ${GENESIS_BASE_IMAGES}
# Copy fat-jar
COPY --chown=eisci:root target/product-server-app-1.0-SNAPSHOT-fat.jar  /usr/lib/genesis/app/product-server-app.jar
USER eisci
WORKDIR /home/eisci
# Run task-executor
CMD java ${JAVA_OPTS} -Dgenesis.zookeeper.url="${zookeeper_connect}" -Dgenesis.cassandra.url="${genesis_cassandra_url}" -Dgenesis.jenkins.rest.endpoint="${genesis_jenkins_rest_endpoint}" -Dgenesis.jenkins.rest.authorization.token="${genesis_jenkins_rest_authorization_token}" -jar /usr/lib/genesis/app/product-server-app.jar
