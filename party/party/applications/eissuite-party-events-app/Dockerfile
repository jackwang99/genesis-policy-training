ARG  GENESIS_BASE_IMAGES=suzeisnexus03.exigengroup.com:5000/genesis-base:1.4.3
FROM ${GENESIS_BASE_IMAGES}
# Copy fat-jar
COPY --chown=eisci:root target/eissuite-party-events-app-1.0-SNAPSHOT-fat.jar /usr/lib/genesis/events/eissuite-party-events-app.jar
USER eisci
WORKDIR /home/eisci
# Run task-executor
CMD java ${JAVA_OPTS} -Deis_base.baseUrl="${EISCORE_BASE_URL}" -Dgenesis.cassandra.url="${genesis_cassandra_url}" -Dgenesis.zookeeper.url="${zookeeper_connect}" -jar /usr/lib/genesis/events/eissuite-party-events-app.jar