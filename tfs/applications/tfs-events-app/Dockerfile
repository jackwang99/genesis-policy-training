ARG  GENESIS_BASE_IMAGES=sfoeisnexus03.exigengroup.com/genesis-docker/genesis-base:openjdk-11-oraclelinux7
FROM ${GENESIS_BASE_IMAGES}
# Copy fat-jar
COPY --chown=eisci:root target/tfs-events-app-1.0-SNAPSHOT-fat.jar /usr/lib/genesis/tasks/tfs-events-fat.jar
USER eisci
WORKDIR /home/eisci
# Run task-executor
CMD java ${JAVA_OPTS} -Deis.url.server="${EISCORE_BASE_URL}" -Dgenesis.cassandra.url="${genesis_cassandra_url}" -Dgenesis.zookeeper.url="${zookeeper_connect}" -jar /usr/lib/genesis/tasks/tfs-events-fat.jar