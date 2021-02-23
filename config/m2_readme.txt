copy and rename m2_settings to either:
a) The Maven install: ${maven.home}/conf/settings.xml
b) A users install: ${user.home}/.m2/settings.xml



Edit file:
replace /username/ with username
replace /password/ with:
 1) mvn --encrypt-master-password
    - it will generate an encrypted version of the password like {jSMOWnoPFgsHVpMvz5VrIt5kRbzGpI8u+9EF1iFQyJQ=}
 2) create settings-security.xml under /.m2 folder. It should look like
    <settingsSecurity>
      <master>{jSMOWnoPFgsHVpMvz5VrIt5kRbzGpI8u+9EF1iFQyJQ=}</master>
    </settingsSecurity>
 3) execute command mvn --encrypt-password
    - it will produce en encrypted version
 4) copy string with brackets
 5) replace /password/


reference:
https://maven.apache.org/guides/mini/guide-encryption.html