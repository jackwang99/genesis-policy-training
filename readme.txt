Exercise Preparation Guide
======Install======
Java OpenJDK 11+
Maven 3.6.3+
Nexus: http://luxor.exigengroup.com/nexus/content/groups/GENESIS/
Docker: https://docs.docker.com/docker-for-windows/

GIT
======Config=========
git config --system core.longpaths true
git config --global core.autocrlf input
docker login suzeisnexus03.exigengroup.com:5000

======Build and Deploy======
Follow the bat from 1~4.2
1.build.bat
...
4.2 start auto.bat


After all the steps success and done:
http://localhost:8084/api/common/schema/v1/swagger-ui/index.html

execute POST {{url}}/policy/PersonalAuto/v1/command/quote/init
default request body has to be a bit changed to:
{
	"body": {
		"country": "USA",
		"currencyCd": "USD",
		"customer": {
			"_uri": "geroot://Customer/INDIVIDUALCUSTOMER//TEST"
		}
	}
}

======Exercise=========
documentation folder has all the training stuff


