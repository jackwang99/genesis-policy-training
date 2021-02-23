ARG  GENESIS_BASE_IMAGES=suzeisnexus03.exigengroup.com:5000/genesis-base:1.4.3
FROM ${GENESIS_BASE_IMAGES}
# Copy fat-jar
COPY --chown=eisci:root target/conversion-events-app-1.0-SNAPSHOT-fat.jar /usr/lib/genesis/tasks/conversion-events-fat.jar
USER eisci
WORKDIR /home/eisci
# Run task-executor
CMD NM_HOST=$EXTERNAL_IP && NM_HOST=${NM_HOST:-$( hostname -I | cut -d ' ' -f 1)} && export NM_HOST && java ${JAVA_OPTS} -Dgenesis.cassandra.url="${genesis_cassandra_url}" -Dgenesis.zookeeper.url="${zookeeper_connect}" -jar /usr/lib/genesis/tasks/conversion-events-fat.jar
