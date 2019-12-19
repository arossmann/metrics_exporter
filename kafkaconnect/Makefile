DOCKER_IMAGE_VERSION=0.1
DOCKER_IMAGE_NAME=kafka-connect
DOCKER_IMAGE_TAGNAME=$(DOCKER_IMAGE_NAME):$(DOCKER_IMAGE_VERSION)
REGISTRY=

default: build

build: ## build the container
	docker build -t $(DOCKER_IMAGE_TAGNAME) .
	docker tag $(DOCKER_IMAGE_TAGNAME) $(DOCKER_IMAGE_NAME):latest

push: ## push container to registry
	docker push $(REGISTRY)/$(DOCKER_IMAGE_NAME):latest

test: ## test the container
	docker run --rm $(DOCKER_IMAGE_TAGNAME) /bin/echo "Success."

rmi: ## cleanup / remove image
	docker rmi -f $(DOCKER_IMAGE_TAGNAME)

rebuild: rmi build

run: ## run the container
	docker run --env-file=.env $(DOCKER_IMAGE_NAME):latest


help: ## This help dialog
	@IFS=$$'\n' ; \
		help_lines=(`fgrep -h "##" $(MAKEFILE_LIST) | fgrep -v fgrep | sed -e 's/\\$$//'`); \
		for help_line in $${help_lines[@]}; do \
			IFS=$$'#' ; \
			help_split=($$help_line) ; \
			help_command=`echo $${help_split[0]} | sed -e 's/^ *//' -e 's/ *$$//'` ; \
			help_info=`echo $${help_split[2]} | sed -e 's/^ *//' -e 's/ *$$//'` ; \
			printf "%-10s %s\n" $$help_command $$help_info ; \
		done

.PHONY: help test build push rmi rebuild run