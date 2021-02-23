ARG  GENESIS_BASE_IMAGES=suzeisnexus03.exigengroup.com:5000/genesis-base:1.4.3
FROM ${GENESIS_BASE_IMAGES}
# Copy fat-jar
COPY --chown=eisci:root target/policy-garage-facade-app-1.0-SNAPSHOT-fat.jar /usr/lib/genesis/facade/policy-garage-facade-fat.jar
# Configure listen ports
EXPOSE 8080
USER eisci
WORKDIR /home/eisci
# Run web-server
CMD NM_HOST=$EXTERNAL_IP && NM_HOST=${NM_HOST:-$( hostname -I | cut -d ' ' -f 1)} && export NM_HOST && java ${JAVA_OPTS} -Dgenesis.cassandra.url="${genesis_cassandra_url}" -Dgenesis.zookeeper.url="${zookeeper_connect}" -jar /usr/lib/genesis/facade/policy-garage-facade-fat.jar