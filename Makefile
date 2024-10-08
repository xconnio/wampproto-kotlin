lint:
	ktlint

format:
	ktlint -F

test:
	./gradlew test

install-wampproto:
	sudo snap install wampproto --edge
