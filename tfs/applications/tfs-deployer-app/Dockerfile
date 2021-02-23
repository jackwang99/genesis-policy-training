ARG  GENESIS_BASE_IMAGES=sfoeisnexus03.exigengroup.com/genesis-docker/genesis-base:openjdk-11-oraclelinux7
FROM ${GENESIS_BASE_IMAGES}
# Copy fat-jar
COPY --chown=eisci:root target/tfs-deployer-app-1.0-SNAPSHOT-fat.jar /usr/lib/genesis/deployer/tfs-deployer-fat.jar
# Copy entrypoint
COPY --chown=eisci:root entrypoint /usr/lib/genesis/bin/deploy
# Change deploy script permissions
RUN chmod 775 /usr/lib/genesis/bin/deploy
USER eisci
WORKDIR /home/eisci
# Configure PATH
ENV PATH /usr/lib/genesis/bin:$PATH