# syntax=docker/dockerfile:1

FROM alpine:3.20

ARG KUBECTL_VERSION=v1.34.0

RUN apk add --no-cache bash curl ca-certificates git docker-cli && \
    curl -fsSL -o /usr/local/bin/kubectl "https://dl.k8s.io/release/${KUBECTL_VERSION}/bin/linux/amd64/kubectl" && \
    chmod +x /usr/local/bin/kubectl

WORKDIR /workspace

ENV PATH="/workspace/devops:$PATH"
ENV KUBECONFIG=/root/.kube-w/config

COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
CMD ["sleep", "infinity"]


