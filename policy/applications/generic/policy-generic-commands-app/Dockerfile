ARG  GENESIS_BASE_IMAGES=suzeisnexus03.exigengroup.com:5000/genesis-base:1.4.3
FROM ${GENESIS_BASE_IMAGES}
# Copy fat-jar
COPY --chown=eisci:root target/policy-generic-commands-app-1.0-SNAPSHOT-fat.jar /usr/lib/genesis/tasks/policy-generic-commands-fat.jar
USER eisci
WORKDIR /home/eisci
# Run task-executor
CMD java ${JAVA_OPTS} -Dgenesis.cassandra.url="${genesis_cassandra_url}" -Dgenesis.zookeeper.url="${zookeeper_connect}"  -Dgenesis.party.facade.url="${GENESIS_PARTY_FACADE_URL}" -jar /usr/lib/genesis/tasks/policy-generic-commands-fat.jar