#!/bin/sh
# Double-click on macOS, or run ./start-website.command from a terminal.
set -eu
project_dir=$(CDPATH= cd -- "$(dirname -- "$0")/2- Code/NonProfitSystem" && pwd)
cd "$project_dir"
if ! command -v java >/dev/null 2>&1; then
  printf '%s\n' 'Java 17 or newer is required to run Hope Bridge.'
  exit 1
fi
printf '%s\n' 'Starting Hope Bridge at http://localhost:8080' 'Keep this window open. Press Control+C to stop the website.'
# Open the browser after Spring Boot has started, while the server stays in this window.
(
  attempts=0
  while [ "$attempts" -lt 180 ]; do
    if curl --noproxy '*' --max-time 1 -fsS http://localhost:8080/ >/dev/null 2>&1; then
      if command -v open >/dev/null 2>&1; then
        open http://localhost:8080/
      elif command -v xdg-open >/dev/null 2>&1; then
        xdg-open http://localhost:8080/
      fi
      exit 0
    fi
    attempts=$((attempts + 1))
    sleep 1
  done
) &
browser_wait_pid=$!
trap 'kill "$browser_wait_pid" 2>/dev/null || true' EXIT
./mvnw spring-boot:run
