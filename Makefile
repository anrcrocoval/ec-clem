install:
	mvn clean install && find target -type f -regex ".*\/ec_clem-[0-9.]*\(-SNAPSHOT\)?\.jar" -exec cp {} lib/icy/plugins/ \;

