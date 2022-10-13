#!/bin/bash

set -x #echo on

# Execute for project root

kubectl cp database/geode/lib/ default/gemfire1-locator-0:/tmp/
kubectl cp database/geode/scripts default/gemfire1-locator-0:/tmp/
kubectl exec -it gemfire1-locator-0 -- gfsh
connect
run --file=/tmp/scripts/gfsh/setup.gfsh


