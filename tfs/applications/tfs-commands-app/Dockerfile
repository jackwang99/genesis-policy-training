ARG  GENESIS_BASE_IMAGES=sfoeisnexus03.exigengroup.com/genesis-docker/genesis-base:openjdk-11-oraclelinux7
FROM ${GENESIS_BASE_IMAGES}
# Copy fat-jar
COPY --chown=eisci:root target/tfs-commands-app-1.0-SNAPSHOT-fat.jar /usr/lib/genesis/tasks/tfs-commands-fat.jar
USER eisci
WORKDIR /home/eisci
# Run task-executor
CMD java ${JAVA_OPTS} -Dgenesis.cassandra.url="${genesis_cassandra_url}" -Dgenesis.zookeeper.url="${zookeeper_connect}" -Dgenesis.party.facade.url="${GENESIS_PARTY_FACADE_URL}" -jar /usr/lib/genesis/tasks/tfs-commands-fat.jar