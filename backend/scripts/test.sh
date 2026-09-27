#!/usr/bin/env bash
# 运行后端测试。Testcontainers 通过 colima 访问 Docker 时需要显式指定 socket。
set -euo pipefail

if [[ -z "${DOCKER_HOST:-}" ]]; then
  if [[ -S "$HOME/.colima/docker/docker.sock" ]]; then
    export DOCKER_HOST="unix://$HOME/.colima/docker/docker.sock"
  elif [[ -S "$HOME/.colima/default/docker.sock" ]]; then
    export DOCKER_HOST="unix://$HOME/.colima/default/docker.sock"
  fi
fi

# colima 中宿主 socket 在 VM 内的挂载点
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE="${TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE:-/var/run/docker.sock}"

cd "$(dirname "$0")/.."
exec mvn test "$@"
