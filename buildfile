# Artefacts definitions
#require "architecture/artefacts"

repositories.remote << "https://repository.jboss.org/nexus/content/repositories/thirdparty-releases"
repositories.remote << "http://repo1.maven.org/maven2"

SCANNOTATION = transitive('org.scannotation:scannotation:jar:1.0.3')
SLF4J = 'org.slf4j:slf4j-api:jar:1.7.5'
JAVAX_WEB = 'javax:javaee-web-api:jar:6.0'
FREEMARKER='org.freemarker:freemarker:jar:2.3.20'
LOG4J=transitive('org.slf4j:slf4j-log4j12:jar:1.7.5')

desc 'Rest JS Generator'
define 'RestJS' do
	# project setup
	project.version = '0.0.1'
	project.group = 'santos.fabiano'
	package :jar
	compile.options.target = '1.7'
	compile.with SCANNOTATION, SLF4J, JAVAX_WEB, FREEMARKER
	test.with SCANNOTATION, SLF4J, JAVAX_WEB, FREEMARKER, LOG4J

	# create javadoc for all projects ...
#	javadoc projects
#	package :javadoc

end


# Project structure
puts project('RestJS').projects.inspect
