ARG  GENESIS_BASE_IMAGES=suzeisnexus03.exigengroup.com:5000/genesis-base:1.4.3
FROM ${GENESIS_BASE_IMAGES}
# Copy fat-jar
COPY --chown=eisci:root target/crm-deployer-app-1.0-SNAPSHOT-fat.jar /usr/lib/genesis/deployer/crm-deployer-fat.jar
# Copy entrypoint
COPY --chown=eisci:root entrypoint /usr/lib/genesis/bin/deploy
# Change deploy script permissions
RUN chmod 775 /usr/lib/genesis/bin/deploy
WORKDIR /home/eisci
USER eisci
# Configure PATH
ENV PATH /usr/lib/genesis/bin:$PATH