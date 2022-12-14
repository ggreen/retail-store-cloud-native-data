#!/bin/bash

set -x #echo on

# Set GemFire Pre-Requisite

kubectl create namespace cert-manager
kubectl create namespace gemfire-system

helm repo add jetstack https://charts.jetstack.io

helm repo update

helm install cert-manager jetstack/cert-manager --namespace cert-manager  --version v1.0.2 --set installCRDs=true

kubectl create secret docker-registry image-pull-secret --namespace=gemfire-system --docker-server=registry.pivotal.io --docker-username=$HARBOR_USER --docker-password=$HARBOR_PASSWORD

kubectl create secret docker-registry image-pull-secret --docker-server=registry.pivotal.io --docker-username=$HARBOR_USER --docker-password=$HARBOR_PASSWORD


export HELM_EXPERIMENTAL_OCI=1

helm registry login registry.tanzu.vmware.com \
       --username=$HARBOR_USER \
       --password=$HARBOR_PASSWORD


helm pull oci://registry.tanzu.vmware.com/tanzu-sql-postgres/postgres-operator-chart --version v1.7.2 --untar --untardir /tmp

kubectl create secret docker-registry regsecret \
    --docker-server=https://registry.tanzu.vmware.com/ \
    --docker-username="$HARBOR_USER" \
    --docker-password="$HARBOR_PASSWORD"

docker load -i ~/dataServices/postgres/postgres-for-kubernetes-v1.7.2/images/postgres-instance
docker load -i ~/dataServices/postgres/postgres-for-kubernetes-v1.7.2/images/postgres-operator

docker images "postgres-*"

kubectl get crd postgres.sql.tanzu.vmware.com


helm install my-postgres-operator /tmp/postgres-operator/ --wait

---------------------------------

# Install Postgres





docker load -i ~/dataServices/postgres/postgres-for-kubernetes-v1.2.0/images/postgres-instance
docker load -i ~/dataServices/postgres/postgres-for-kubernetes-v1.2.0/images/postgres-operator
docker images "postgres-*"
export HELM_EXPERIMENTAL_OCI=1
helm registry login registry.pivotal.io --username=$HARBOR_USER --password=$HARBOR_PASSWORD

helm chart pull registry.pivotal.io/tanzu-sql-postgres/postgres-operator-chart:v1.2.0
helm chart export registry.pivotal.io/tanzu-sql-postgres/postgres-operator-chart:v1.2.0  --destination=/tmp/

kubectl create secret docker-registry regsecret \
--docker-server=https://registry.pivotal.io --docker-username=$HARBOR_USER \
--docker-password=$HARBOR_PASSWORD

helm install --wait postgres-operator /tmp/postgres-operator/

sleep 30
kubectl apply -f cloud/k8/data-services/postgres
sleep 40
kubectl wait pod -l=app=postgres --for=condition=Ready --timeout=360s
kubectl wait pod -l=statefulset.kubernetes.io/pod-name=postgres-0 --for=condition=Ready --timeout=360s


# ---------------

kubectl exec -it postgres-0 -- psql -c "ALTER USER postgres PASSWORD 'CHANGEME'"
